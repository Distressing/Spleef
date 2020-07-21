package dev.distressing.spleef.listeners;

import dev.distressing.spleef.data.SpleefDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final SpleefDataManager dataManager;

    public PlayerListeners(SpleefDataManager spleefDataManager) {
        this.dataManager = spleefDataManager;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        dataManager.loadData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataManager.save(event.getPlayer());
    }

}
