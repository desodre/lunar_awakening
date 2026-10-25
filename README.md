# Ciclos Celestiais — protótipo 0.1.1

Mod Fabric para **Minecraft Java 26.3**. Código principal em Kotlin; os mixins ficam em Java.

## Mecânicas

- No Overworld, cada dia tem **5%** de chance de eclipse solar e cada noite tem **10%** de chance de Lua de Sangue. O sorteio usa a semente e o número do dia, então continua igual depois de reiniciar o mundo.
- O eclipse escurece a luz do céu, tinge o céu, permite spawns hostis em locais que ficariam claros durante o dia e evita que mobs usem a lógica padrão de queimadura solar.
- A Lua de Sangue tinge o céu de vermelho e impede dormir.
- Nos dois eventos, o limite **global e local** da categoria `MONSTER` sobe de 70 para 88 (`round(70 × 1,25)`). Isso aumenta a capacidade de mobs naturais, mas **não garante 25% mais spawns reais**: tentativas de spawn, regras de luz, biomas e espaço disponível continuam valendo.
- O mod não adiciona criaturas ou itens.

## Compilar

Instale JDK 25 e execute `./gradlew build`. O arquivo instalável sai em `build/libs/ciclos-celestiais-0.1.1.jar`. Não é necessário ter Gradle instalado globalmente.

No jogo, instale o `.jar` junto com **Fabric Loader**, **Fabric API** e **Fabric Language Kotlin** para Minecraft 26.3. Para ver as cores do céu, instale também no cliente; as regras de spawn e sono são controladas pelo servidor.

## Testar

Com permissão de operador, use:

- `/celestialcycles eclipse`
- `/celestialcycles bloodmoon`
- `/celestialcycles off`
- `/celestialcycles auto`
- `/celestialcycles status`

Os comandos de forçar ou desligar eventos valem até reiniciar o servidor. `auto` restaura o ciclo sorteado.

## Estado atual

O projeto compila com Fabric 26.3. A versão 0.1.1 corrige o crash de carregamento do mixin ao entrar em um mundo. O JAR pré-compilado está em `dist/`. Ainda é necessário testar visualmente os eventos e medir spawns dentro de um mundo jogável.

Baseado no [template oficial do Fabric](https://github.com/FabricMC/fabric-example-mod/tree/26.3), disponibilizado em CC0.
