package dev.distressing.spleef;

import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.data.objects.SpleefPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class SpleefPlugin extends JavaPlugin {

    private static SpleefDataManager spleefDataManager;
    private static SpleefPlugin instance;

    @Override
    public void onEnable(){
        instance = this;
        this.saveDefaultConfig();
        spleefDataManager = new SpleefDataManager(this.getConfig());
    }

    @Override
    public void onDisable(){
        spleefDataManager.shutdown();
    }

    public static SpleefPlugin getInstance(){return instance;}

}
