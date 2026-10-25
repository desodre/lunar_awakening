# Lunar Awakening — protótipo 0.1.6

Mod Fabric para **Minecraft Java 26.3**. Código principal em Kotlin; os mixins ficam em Java.

## Mecânicas

- No Overworld, cada dia tem **5%** de chance de eclipse solar e cada noite tem **10%** de chance de Lua de Sangue. Quando sorteado, o eclipse começa em um horário variável do dia e dura **2 minutos** (2.400 ticks). O sorteio usa a semente e o número do dia, então continua igual depois de reiniciar o mundo.
- O eclipse escurece o céu e o horizonte atmosférico e mostra um Sol **inteiramente quadrado**, com centro escuro e halo luminoso também quadrado. Também permite spawns hostis em locais que ficariam claros durante o dia e evita que mobs usem a lógica padrão de queimadura solar.
- A Lua de Sangue tinge o céu, o horizonte e todas as fases visíveis da Lua de vermelho, além de impedir dormir.
- Nos dois eventos, o limite **global e local** da categoria `MONSTER` sobe de 70 para 88 (`round(70 × 1,25)`). Isso aumenta a capacidade de mobs naturais, mas **não garante 25% mais spawns reais**: tentativas de spawn, regras de luz, biomas e espaço disponível continuam valendo.
- O mod não adiciona criaturas ou itens.

## Compilar

Instale JDK 25 e execute `./gradlew build`. O arquivo instalável sai em `build/libs/lunar_awakening-0.1.6.jar`. Não é necessário ter Gradle instalado globalmente.

No jogo, instale o `.jar` junto com **Fabric Loader**, **Fabric API** e **Fabric Language Kotlin** para Minecraft 26.3. Para ver as cores do céu, instale também no cliente; as regras de spawn e sono são controladas pelo servidor.

## Testar

Com permissão de operador, use:

- `/lunar_awakening eclipse`
- `/lunar_awakening bloodmoon`
- `/lunar_awakening off`
- `/lunar_awakening auto`
- `/lunar_awakening status`

`eclipse` ajusta o relógio do Overworld para **dia** e `bloodmoon` para **noite**, como `/time set day` e `/time set night`, antes de forçar o evento. O eclipse forçado permanece ativo até `off`, `auto` ou reinício; a duração de 2 minutos vale apenas para eclipses naturais. Os comandos funcionam mesmo se forem executados em outra dimensão, mas o efeito ocorre no Overworld. `off` e `auto` não alteram o horário; `auto` restaura o ciclo sorteado. Os estados forçados valem até reiniciar o servidor.

## Estado atual

O projeto compila com Fabric 26.3. A versão 0.1.1 corrigiu o crash de carregamento do mixin; a 0.1.2 acrescentou os astros personalizados e o ajuste de horário; a 0.1.3 preservou o Sol quadrado. A 0.1.4 reduziu o tamanho aparente do eclipse e ajustou o horizonte dos dois eventos. A 0.1.5 permite eclipses naturais em horários variados do dia. A 0.1.6 renomeia o mod para `lunar_awakening`, inclusive o ID do Fabric e o comando. Remova a versão antiga ao instalar esta. O JAR pré-compilado está em `dist/`. Ainda é necessário validar os efeitos visualmente e medir spawns dentro de um mundo jogável.

Baseado no [template oficial do Fabric](https://github.com/FabricMC/fabric-example-mod/tree/26.3), disponibilizado em CC0.

## Licença

O projeto é distribuído sob a [licença MIT](LICENSE).
