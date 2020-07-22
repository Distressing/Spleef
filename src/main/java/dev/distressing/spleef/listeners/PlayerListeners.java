package dev.distressing.spleef.listeners;

import dev.distressing.spleef.api.enums.GameState;
import dev.distressing.spleef.api.events.player.PlayerLeaveEvent;
import dev.distressing.spleef.api.managers.GameManager;
import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.data.SpleefDataManager;
import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerListeners implements Listener {

    private final SpleefDataManager dataManager;
    private final GameManager gameManager;

    public PlayerListeners(SpleefDataManager spleefDataManager, GameManager gameManager) {
        this.dataManager = spleefDataManager;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        dataManager.loadData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataManager.save(event.getPlayer());
        gameManager.processLogout(event.getPlayer());
    }

    @EventHandler
    public void onEliminate(PlayerLeaveEvent event) {
        Player player = event.getPlayer();
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.getInventory().clear();

        String deathMessage = Messages.PLAYER_ELIMINATED.getWithPrefix(player);
        event.getSpleefGame().getPlayers().forEach(player1 -> player1.sendMessage(deathMessage));
        player.sendMessage(Messages.GAME_LOSS.getWithPrefix());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (gameManager.getGame(player).isPresent())
            event.setCancelled(true);
    }

    @EventHandler
    public void onStarve(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if (gameManager.getGame(player).isPresent())
            player.setFoodLevel(20);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Optional<SpleefGame> spleefGameOptional = gameManager.getGame(player);

        if (!spleefGameOptional.isPresent())
            return;

        SpleefGame game = spleefGameOptional.get();
        Block block = event.getBlock();
        GameState state = game.getGameState();

        event.setCancelled(true);

        if (state.equals(GameState.WAITING) || state.equals(GameState.CONFIRMED)) {
            return;
        }

        if (state.equals(GameState.GRACE)) {
            player.sendMessage(Messages.GAME_GRACE_NOTIFICATION.getWithPrefix());
            return;
        }

        if (state.equals(GameState.RUNNING) && block.getType().equals(Material.SNOW_BLOCK)) {
            block.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (gameManager.getGame(event.getPlayer()).isPresent())
            event.setCancelled(true);
    }
}
