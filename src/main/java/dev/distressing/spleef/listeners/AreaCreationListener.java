package dev.distressing.spleef.listeners;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.managers.AreaCreationManager;
import dev.distressing.spleef.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AreaCreationListener implements Listener {

    private final AreaCreationManager areaCreationManager;

    public AreaCreationListener(AreaCreationManager areaCreationManager) {
        this.areaCreationManager = areaCreationManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null)
            return;

        if (!areaCreationManager.isBuilding(player))
            return;

        areaCreationManager.processBuilderInteract(player, event.getClickedBlock());

    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!areaCreationManager.isBuilding(player))
            return;

        if (!event.getMessage().equalsIgnoreCase("cancel"))
            return;

        event.setCancelled(true);
        areaCreationManager.stopBuilder(player);
        player.sendMessage(Messages.AREA_BUILDING_CANCELED.getWithPrefix());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!areaCreationManager.isBuilding(player))
            return;

        areaCreationManager.stopBuilder(player);
    }
}
