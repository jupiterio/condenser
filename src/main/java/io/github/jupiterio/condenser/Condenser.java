package io.github.jupiterio.condenser;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condenser implements ModInitializer {
    public static final String MOD_ID = "condenser";
    public static final String MOD_NAME = "Condenser";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Item CONDENSER;
    public static Item INFUSED_QUARTZ;
    public static Item COMPRESSED;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        log("Initializing!");

        CONDENSER = Registry.register(Registry.ITEM, new Identifier("condenser", "condenser"), new CondenserItem(new FabricItemSettings().group(ItemGroup.TOOLS)));
        INFUSED_QUARTZ = Registry.register(Registry.ITEM, new Identifier("condenser", "infused_quartz"), new InfusedQuartzItem(new FabricItemSettings().group(ItemGroup.MISC)));
        COMPRESSED = Registry.register(Registry.ITEM, new Identifier("condenser", "compressed"), new CompressedItem(new FabricItemSettings()));
    }

    public static void log(String message){
        LOGGER.info(message);
    }

    public static void warn(String message){
        LOGGER.warn(message);
    }

    public static void warn(String message, Throwable t){
        LOGGER.warn(message, t);
    }

    public static void error(String message, Throwable t){
        LOGGER.error(message, t);
    }

    public static void debug(String message){
        LOGGER.debug(message);
    }

    public static void debug(String message, Throwable t){
        LOGGER.debug(message, t);
    }
}
