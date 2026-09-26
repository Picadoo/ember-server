package com.ember.stackdisplay;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = EmberStackDisplay.MODID,
        name = EmberStackDisplay.NAME,
        version = EmberStackDisplay.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        clientSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class EmberStackDisplay {

    public static final String MODID = "emberstackdisplay";
    public static final String NAME = "Ember Stack Display";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Ember Stack Display {} — client overlay fix for stacks up to 127", VERSION);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            ClientHooks.install();
        }
    }
}
