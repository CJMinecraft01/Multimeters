package cjminecraft.multimeters;

import cjminecraft.multimeters.proxy.CommonProxy;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Multimeters.MODID, name = Multimeters.NAME, version = Multimeters.VERSION, dependencies = Multimeters.DEPENDENCIES, acceptedMinecraftVersions = Multimeters.ACCEPTED_MINECRAFT_VERSIONS, updateJSON = Multimeters.UPDATE_JSON)
public class Multimeters {

    public static final String MODID = "multimeters";
    public static final String NAME = "Multimeters";
    public static final String VERSION = "${version}";
    public static final String DEPENDENCIES = "before:cjcore@[0.0.4.1,)";
    public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12,1.12.2]";
    public static final String SERVER_PROXY_CLASS = "cjminecraft.multimeters.proxy.ServerProxy";
    public static final String CLIENT_PROXY_CLASS = "cjminecraft.multimeters.proxy.ClientProxy";
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/CJMinecraft01/Multimeters/1.12/update.json";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MODID);

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

}
