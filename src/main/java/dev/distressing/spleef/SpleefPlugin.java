package dev.distressing.spleef;

import dev.distressing.spleef.commands.SpleefCMD;
import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.configuration.SpleefConfig;
import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.listeners.AreaCreationListener;
import dev.distressing.spleef.listeners.GameListeners;
import dev.distressing.spleef.listeners.PlayerListeners;
import dev.distressing.spleef.listeners.StatisticsListener;
import dev.distressing.spleef.managers.AreaCreationManager;
import dev.distressing.spleef.managers.ArenaManager;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefArea;
import dev.distressing.spleef.persist.Persist;
import dev.distressing.spleef.runnables.SpleefGameTicker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SpleefPlugin extends JavaPlugin {

    private static SpleefDataManager spleefDataManager;
    private static SpleefPlugin instance;
    private final Persist persist = new Persist();
    private ArenaManager arenaManager;
    private GameManager gameManager;

    public static SpleefPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        SpleefConfig.init(this);

        Messages.load();

        spleefDataManager = new SpleefDataManager();

        this.gameManager = persist.getFile(GameManager.class).exists() ? persist.load(GameManager.class) : new GameManager();
        this.arenaManager = persist.getFile(ArenaManager.class).exists() ? persist.load(ArenaManager.class) : new ArenaManager();
        AreaCreationManager areaCreationManager = new AreaCreationManager(arenaManager);

        gameManager.resetGames();
        arenaManager.getArenas().values().forEach(SpleefArea::refreshArena);
        gameManager.setGameTickRate((int) (20 / SpleefConfig.getDouble("gameTps", 5D)));
        Bukkit.getScheduler().runTaskTimer(this, new SpleefGameTicker(gameManager), gameManager.getGameTickRate(), gameManager.getGameTickRate());

        registerCommands("spleef", new SpleefCMD(gameManager, arenaManager, areaCreationManager, spleefDataManager));

        registerListeners(
                new PlayerListeners(spleefDataManager, gameManager),
                new AreaCreationListener(areaCreationManager),
                new GameListeners(gameManager),
                new StatisticsListener(spleefDataManager)
        );
    }

    @Override
    public void onDisable() {
        spleefDataManager.shutdown();
        gameManager.resetGames();
        arenaManager.clearCache();

        if (gameManager != null)
            persist.save(gameManager);

        if (arenaManager != null)
            persist.save(arenaManager);
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
