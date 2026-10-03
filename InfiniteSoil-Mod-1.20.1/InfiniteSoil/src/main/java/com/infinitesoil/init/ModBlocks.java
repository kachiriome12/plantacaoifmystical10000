package com.infinitesoil.init;

import com.infinitesoil.InfiniteSoilMod;
import com.infinitesoil.block.InfiniteSoilBlock;
import com.infinitesoil.block.EssenceCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, InfiniteSoilMod.MOD_ID);

    public static final RegistryObject<Block> INFINITE_SOIL = BLOCKS.register("infinite_soil",
            () -> new InfiniteSoilBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND)
                    .mapColor(MapColor.DIRT)
                    .strength(0.6f)
                    .sound(SoundType.GRAVEL)
                    .randomTicks()
                    .noOcclusion()));

    public static final RegistryObject<Block> ESSENCE_CROP = BLOCKS.register("essence_crop",
            () -> new EssenceCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
