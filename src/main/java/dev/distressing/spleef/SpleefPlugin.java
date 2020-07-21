package dev.distressing.spleef;

import dev.distressing.spleef.commands.SpleefCMD;
import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.configuration.SpleefConfig;
import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.listeners.PlayerListeners;
import dev.distressing.spleef.managers.ArenaManager;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.persist.Persist;
import dev.distressing.spleef.runnables.SpleefGameTicker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SpleefPlugin extends JavaPlugin {

    private static SpleefDataManager spleefDataManager;
    private static SpleefPlugin instance;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private final Persist persist = new Persist();

    public static SpleefPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        SpleefConfig.init(this);

        Messages.load();

        spleefDataManager = new SpleefDataManager(this.getConfig());

        this.gameManager = persist.getFile(GameManager.class).exists() ? persist.load(GameManager.class) : new GameManager();
        gameManager.resetGames();
        this.arenaManager = persist.getFile(ArenaManager.class).exists() ? persist.load(ArenaManager.class) : new ArenaManager();

        int tickSpeed = (int) (20 / SpleefConfig.getDouble("gameTps", 5D));
        gameManager.setGameTickRate(tickSpeed);
        Bukkit.getScheduler().runTaskTimer(this, new SpleefGameTicker(gameManager), tickSpeed, tickSpeed);

        registerCommands("spleef", new SpleefCMD(gameManager, arenaManager));
        registerListeners(new PlayerListeners(spleefDataManager));
    }

    @Override
    public void onDisable() {
        spleefDataManager.shutdown();
    }

    public void registerCommands(String command, CommandExecutor ex) {
        this.getCommand(command).setExecutor(ex);
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

}
