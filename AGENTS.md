# Guia do projeto: Ciclos Celestiais

Este arquivo orienta quem desenvolver o mod em `/home/desodre/Projects/lunar_awake`. Leia também `README.md`, `gradle.properties` e `fabric.mod.json` antes de alterar código. A versão efetiva das dependências está em `gradle.properties`; não substitua essas versões por valores encontrados em páginas genéricas sem conferir compatibilidade.

## Escopo e ambiente

- Mod para **Minecraft Java 26.3**, **Fabric Loader**, **Fabric API** e **Fabric Language Kotlin**. A lógica principal é Kotlin; mixins são Java. O projeto usa **JDK 25** e Gradle Wrapper (`./gradlew`).
- A documentação Fabric pode apresentar exemplos de outra versão. Use os guias para os conceitos, mas confirme nomes de classes, métodos, eventos e assinaturas nas fontes/Javadocs das dependências fixadas para **26.3**. Este projeto já encontrou diferenças em relógio do mundo, callbacks de tick e renderização do céu entre versões.
- `src/main` contém lógica comum e de servidor. `src/client` contém somente código do cliente. Mantenha regras de jogo no servidor e sincronize ao cliente apenas o estado necessário para exibição.

## Documentação primária

| Assunto | Fonte | Uso neste projeto |
| --- | --- | --- |
| Referência geral Fabric | [Documentação Fabric](https://docs.fabricmc.net/develop/) | Guias de desenvolvimento; conferir a versão exibida antes de copiar APIs. |
| Exemplo para 26.3 | [fabric-example-mod, ramo 26.3](https://github.com/FabricMC/fabric-example-mod/tree/26.3) | Estrutura e configuração compatíveis com a versão alvo. |
| Configuração do ambiente | [Setting Up](https://docs.fabricmc.net/develop/getting-started/setting-up) | JDK e IDE. |
| Estrutura e source sets | [Project Structure](https://docs.fabricmc.net/develop/getting-started/project-structure) | Separação entre `main`, `client`, recursos e entrypoints. |
| Metadados do mod | [fabric.mod.json](https://docs.fabricmc.net/develop/loader/fabric-mod-json) | Dependências, entrypoints e configurações de mixins. |
| Build e execução | [Fabric Loom](https://docs.fabricmc.net/develop/loom/) e [Building a Mod](https://docs.fabricmc.net/develop/getting-started/building-a-mod) | Gradle, tarefas e JAR em `build/libs/`. |
| Kotlin | [Fabric Language Kotlin](https://github.com/FabricMC/fabric-language-kotlin/blob/master/README.md) | Dependência e adapter `kotlin` nos entrypoints. |
| Hooks de jogo | [Fabric Events](https://docs.fabricmc.net/develop/events/) | Preferir eventos oficiais quando eles cobrem o caso. |
| Cliente/servidor | [Networking](https://docs.fabricmc.net/develop/networking) | Enviar o evento ativo do servidor para a renderização cliente. |
| Comandos | [Commands](https://docs.fabricmc.net/develop/commands/basics) | Comandos de teste `/celestialcycles`. |
| Renderização | [Rendering: Basic Concepts](https://docs.fabricmc.net/develop/rendering/basic-concepts) | Alteração visual do céu no cliente. |
| Testes de jogo | [Automated Testing](https://docs.fabricmc.net/develop/automatic-testing) | Testes que entram em um mundo e executam ticks. |
| Mixins | [SpongePowered Mixin: The Mixin Environment](https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment) | Pacotes, carregamento e limites de mixins. |
| Versões publicadas | [Fabric Develop](https://fabricmc.net/develop/) e [Maven do Fabric](https://maven.fabricmc.net/) | Conferir artefatos reais antes de atualizar. |

## Mapa do código

- `src/main/kotlin/com/cicloscelestiais/CycleEventManager.kt`: sorteio determinístico por semente e dia, ciclo e evento atual no Overworld. Eclipses têm 5% de chance por dia; Luas de Sangue, 10% por noite.
- `CelestialCycles.kt`: inicialização, callbacks de tick e regra de sono. `CelestialCommands.kt`: comandos para forçar eventos e consultar estado. Estados forçados são temporários e terminam ao reiniciar.
- `EventNetworking.kt`, `EventSyncPayload.java` e `EventStateBridge.kt`: sincronização servidor → cliente. Mesmo no modo de um jogador, trate servidor lógico e cliente como lados distintos.
- `src/main/java/com/cicloscelestiais/mixin/`: somente classes anotadas como mixins. Interceptam luz/combustão solar e o limite de spawn de monstros. Configuração em `src/main/resources/ciclos_celestiais.mixins.json`.
- `src/main/java/com/cicloscelestiais/spawn/`: interface e contexto auxiliares de spawn; **não** mover para `mixin/`.
- `src/client/java/com/cicloscelestiais/client/mixin/SkyRendererMixin.java`: cor do céu, Lua vermelha e seleção do sprite de eclipse. O sprite está em `src/client/resources/assets/ciclos_celestiais/textures/environment/celestial/eclipse_sun.png`, pode ser regenerado com `python3 tools/generate_eclipse_sun.py` e é adicionado ao atlas por `src/client/resources/assets/minecraft/atlases/celestials.json`. Configuração do mixin em `src/client/resources/ciclos_celestiais.client.mixins.json`.
- `CelestialCommands.kt`: `eclipse` e `bloodmoon` movem o relógio do Overworld para os marcadores oficiais `DAY`/`NIGHT` antes de forçar o evento. `off` e `auto` preservam o horário; todos os comandos atuam no Overworld.
- `src/main/resources/fabric.mod.json`: ID, dependências, entrypoints e configurações de mixins.

## Regras de implementação

1. Use um evento do Fabric API quando existir um hook adequado. Empregue Mixin para pontos sem evento público e verifique o alvo contra a versão 26.3 antes de editar injeções.
2. **Reserve cada pacote declarado no campo `package` de um arquivo `*.mixins.json` exclusivamente para classes mixin.** Não coloque interfaces, helpers, enums ou classes comuns nele nem em seus subpacotes. O Mixin exclui esse pacote do classloader de transformação; uma referência direta a uma classe comum nele causou `IllegalClassLoadError` ao entrar em um mundo. A regra vem da [documentação do SpongePowered Mixin](https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---The-Mixin-Environment).
3. Preserve a autoridade do servidor para sorteio, spawn, sono e comandos. O cliente recebe o estado para renderizar o céu; não sorteie eventos separadamente nele.
4. Mantenha o sorteio estável para a mesma semente e dia. Se adicionar estado persistente, use o mecanismo apropriado de [Saved Data](https://docs.fabricmc.net/develop/serialization/saved-data) e defina migração de dados.
5. O aumento de 25% aplica-se ao **limite global e local** de `MONSTER`: 70 → 88 com arredondamento. Não descreva isso como garantia de 25% mais criaturas geradas; o resultado depende das demais regras de spawn.
6. Atualize `README.md`, textos `pt_br.json` e `en_us.json`, metadados e versão sempre que uma mudança visível para o jogador os afetar.
7. Preserve a silhueta **quadrada** do Sol durante o eclipse. O sprite e a sombra central devem ter bordas retas e cantos quadrados, como o Sol original do Minecraft; não substitua por um disco ou coroa circular.

## Verificação e entrega

1. Execute `./gradlew clean build` com JDK 25. O JAR instalável fica em `build/libs/`.
2. Antes de distribuir uma mudança de mixin, carregue **um mundo jogável e avance ticks** com o JAR final instalado; compilar ou apenas iniciar até a tela/EULA não detecta todos os erros de classloader e injeção. Se não for possível fazer esse teste, declare essa limitação na entrega.
3. Confira `jar tf build/libs/<arquivo>.jar`: as classes auxiliares devem estar fora dos pacotes `mixin`, e as classes mixin e respectivos JSONs devem estar presentes.
4. Teste no Overworld os comandos `/celestialcycles eclipse`, `bloodmoon`, `off`, `auto` e `status`; confirme céu, luz, sono, combustão solar e spawn em cliente e servidor quando a alteração envolver essas áreas.
5. Atualize `dist/` somente com o JAR da versão atual e, se necessário, o ZIP do código. Não entregue versões antigas junto da atual. Arquivos `.gradle/`, `.kotlin/`, `build/`, `run/` e caches locais não entram no ZIP nem no Git.
6. Ao investigar um crash, identifique a primeira exceção relevante, a classe e a fase (carregamento, entrada no mundo, tick ou render), então confira versão, pacote, configuração de mixins e causa encadeada antes de alterar código.

## Estado conhecido

- A versão 0.1.1 corrige o `IllegalClassLoadError` causado pelas classes auxiliares dentro do pacote de mixins na versão 0.1.0.
- `./gradlew clean build` passou para 0.1.3. Essa versão mantém o Sol quadrado durante o eclipse. Ainda falta verificar visualmente os astros e os comandos em um mundo jogável.
