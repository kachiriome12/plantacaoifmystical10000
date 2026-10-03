# Infinite Soil Mod (Terra Infinita)

Mod para Minecraft **1.20.1 Forge**.

## O que o mod faz

### 1. Terra Infinita (Infinite Soil)
- **Craft**: 8 Terras + 1 Diamante no centro
- Qualquer planta colocada nela cresce **instantaneamente** (velocidade máxima)
- Cresce **infinitamente** (não para)
- Se colocar uma **Hopper embaixo**, ela puxa os itens infinitamente (a terra colhe automaticamente e coloca na hopper)
- Funciona com seeds do **Mystical Agriculture** e qualquer outro mod de seeds

### 2. Semente de Essência (Essence Seed)
- Semente única que funciona para **todas as essências** do Mystical Agriculture
- **Craft**: 4 Essências iguais + 1 Semente qualquer no centro
- A semente "lembra" qual essência foi usada (via NBT)
- Quando plantada e colhida, dropa a essência correspondente
- Cresce especialmente bem na Terra Infinita

## Como instalar e compilar

1. Baixe o **Forge MDK 1.20.1** oficial:  
   https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html  
   (botão MDK)

2. Extraia o MDK em uma pasta.

3. **Substitua** ou copie os arquivos deste projeto para dentro do MDK:
   - `src/main/java/...` → copie a pasta `com/infinitesoil`
   - `src/main/resources/...` → copie tudo
   - Atualize `gradle.properties` e `build.gradle` conforme os arquivos deste projeto

4. No `build.gradle`, adicione a dependência do Mystical Agriculture (opcional mas recomendado):

```gradle
repositories {
    maven { url = "https://maven.blamejared.com" } // ou o maven do BlakeBr0
    // ou curse maven
}

dependencies {
    // ... existing
    // Soft dependency example (ajuste a versão):
    // compileOnly fg.deobf("curse.maven:mystical-agriculture-246640:XXXXXXX")
}
```

5. Rode:
```bash
./gradlew genEclipseRuns   # ou genIntellijRuns
./gradlew build
```

O jar vai aparecer em `build/libs/`.

## Arquivos importantes

- `InfiniteSoilMod.java` → classe principal
- `ModBlocks.java` / `ModItems.java` → registros
- `InfiniteSoilBlock.java` → a terra mágica + BlockEntity
- `EssenceSeedItem.java` + `EssenceCropBlock.java` → a semente dinâmica
- Recipes JSON

## Como usar a Semente de Essência

Como a semente é dinâmica (uma só para todas as essências), o craft automático de "qualquer essência" precisa de RecipeSerializer customizada (avançado).

**Formas de obter a semente:**

1. **Criativo / Comando**:
```
/give @p infinitesoil:essence_seed{EssenceId:"mysticalagriculture:inferium_essence"} 1
```
Troque o ID pela essência desejada (ex: `mysticalagriculture:diamond_essence`).

2. **Adicionar recipes manuais** no datapack ou no mod para as essências que você mais usa (copie o formato do infinite_soil.json e mude).

3. **KubeJS** (recomendado se você usa):
```js
// server_scripts
ServerEvents.recipes(event => {
  // Exemplo para Inferium
  event.shaped('infinitesoil:essence_seed', [
    'EEE',
    'ESE',
    'EEE'
  ], {
    E: 'mysticalagriculture:inferium_essence',
    S: '#forge:seeds'
  }).modifyResult((grid, result) => {
    result.nbt = {EssenceId: "mysticalagriculture:inferium_essence"}
    return result
  })
})
```

## Observações técnicas

- A Terra Infinita usa **BlockEntity** com inventário de 9 slots → Hopper extrai normalmente.
- Crescimento forçado **a cada tick** (velocidade máxima absoluta).
- Quando a planta atinge idade máxima, ela é colhida automaticamente e **resetada para idade 0** → infinito.
- Funciona com qualquer bloco que tenha a property `age` (CropBlock, MA crops, etc.).
- A semente de essência guarda o ID da essência no NBT e o crop dropa ela na colheita.

## Limitações conhecidas do template

- Texturas são placeholders (usa farmland/wheat).
- A semente de essência não tem recipe JSON automática para "qualquer essência" (precisa NBT).
- O getDrops do EssenceCrop pode precisar de ajuste fino dependendo da versão exata do Forge.
- Teste bem com Mystical Agriculture instalado.

Se quiser que eu adicione RecipeSerializer completa, texturas melhores, suporte a mais mods ou corrija algum bug específico, é só pedir!

