package com.infinitesoil;

import com.infinitesoil.init.ModBlocks;
import com.infinitesoil.init.ModItems;
import com.infinitesoil.init.ModBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(InfiniteSoilMod.MOD_ID)
public class InfiniteSoilMod {
    public static final String MOD_ID = "infinitesoil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InfiniteSoilMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Infinite Soil (Terra Infinita) carregado!");
    }
}
