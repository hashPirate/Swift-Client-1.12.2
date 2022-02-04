package me.pignol.swift;

import me.pignol.swift.client.managers.*;
import me.pignol.swift.client.managers.config.ConfigManager;
import me.pignol.swift.client.managers.config.FileManager;
import me.pignol.swift.client.managers.lookup.LookUpManager;
import me.pignol.swift.client.modules.other.FontModule;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Swift.MOD_ID, name = Swift.MOD_NAME, version = Swift.VERSION)
public class Swift {

    public static final Logger LOGGER = LogManager.getLogger("Swift");
    public static final String MOD_ID = "swift";
    public static final String MOD_NAME = "Swift";
    public static final String VERSION = "1.1.1";

    @Mod.Instance(MOD_ID)
    private static Swift INSTANCE;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        long ms = System.currentTimeMillis();
        FileManager.getInstance().init();
        FriendManager.getInstance().getFriends();
        ModuleManager.getInstance().load();
        ConfigManager.getInstance().load();
        CommandManager.getInstance().load();
        EventManager.getInstance().load();
        ServerManager.getInstance().load();
        SwitchManager.getInstance().load();
        HoleManager.getInstance().load();
        LookUpManager.getInstance().init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::unload));
        LOGGER.info("Loaded swift in " + (System.currentTimeMillis() - ms) + "ms");
    }

    @Mod.EventHandler
    public void initPost(FMLPostInitializationEvent event) {
        FontManager.getInstance().updateFontRenderer();
        FontModule.INSTANCE.receive = true;
    }

    public void unload() {
        ConfigManager.getInstance().save();
    }

    public static Swift getInstance() {
        return INSTANCE;
    }

}
