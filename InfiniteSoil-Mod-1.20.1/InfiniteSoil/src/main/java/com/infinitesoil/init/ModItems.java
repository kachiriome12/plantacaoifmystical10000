package com.infinitesoil.init;

import com.infinitesoil.InfiniteSoilMod;
import com.infinitesoil.item.EssenceSeedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InfiniteSoilMod.MOD_ID);

    public static final RegistryObject<Item> INFINITE_SOIL_ITEM = ITEMS.register("infinite_soil",
            () -> new BlockItem(ModBlocks.INFINITE_SOIL.get(), new Item.Properties()));

    public static final RegistryObject<Item> ESSENCE_SEED = ITEMS.register("essence_seed",
            () -> new EssenceSeedItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
