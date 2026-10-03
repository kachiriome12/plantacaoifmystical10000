package com.infinitesoil.init;

import com.infinitesoil.InfiniteSoilMod;
import com.infinitesoil.block.entity.InfiniteSoilBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, InfiniteSoilMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<InfiniteSoilBlockEntity>> INFINITE_SOIL_BE =
            BLOCK_ENTITIES.register("infinite_soil",
                    () -> BlockEntityType.Builder.of(InfiniteSoilBlockEntity::new,
                            ModBlocks.INFINITE_SOIL.get()).build(null));

    public static final RegistryObject<BlockEntityType<com.infinitesoil.block.entity.EssenceCropBlockEntity>> ESSENCE_CROP_BE =
            BLOCK_ENTITIES.register("essence_crop",
                    () -> BlockEntityType.Builder.of(com.infinitesoil.block.entity.EssenceCropBlockEntity::new,
                            ModBlocks.ESSENCE_CROP.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
