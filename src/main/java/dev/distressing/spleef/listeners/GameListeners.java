package dev.distressing.spleef.listeners;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.events.Game.GameStateChangeEvent;
import dev.distressing.spleef.events.Game.SpleefGameEndEvent;
import dev.distressing.spleef.events.Game.SpleefStartGameEvent;
import dev.distressing.spleef.events.Player.PlayerGameWinEvent;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefGame;
import dev.distressing.spleef.utils.Chat;
import dev.distressing.spleef.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GameListeners implements Listener {

    private final GameManager gameManager;

    public GameListeners(GameManager manager) {
        this.gameManager = manager;
    }

    @EventHandler
    public void onGameStateChangeEvent(GameStateChangeEvent event) {
        SpleefGame game = event.getSpleefGame();

        switch (game.getGameState()) {
            case CONFIRMED:
                String confirmed = Chat.color(Messages.GAME_STARTING.getWithPrefix().replace("%time%", game.getWaitTime() / gameManager.getGameTickRate() + ""));
                game.getPlayers().forEach(player -> player.sendMessage(confirmed));
                break;

            case GRACE:
                String grace = Chat.color(Messages.GAME_GRACE_START.getWithPrefix());
                game.getPlayers().forEach(player -> player.sendMessage(grace));
                break;

            case RUNNING:
                String running = Chat.color(Messages.GAME_GRACE_END.getWithPrefix());
                game.getPlayers().forEach(player -> player.sendMessage(running));
                break;
        }

    }

    @EventHandler
    public void onWin(PlayerGameWinEvent event) {
        Player player = event.getPlayer();

        Bukkit.broadcastMessage(Chat.color(Messages.GAME_WIN.getWithPrefix(player)));
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.getInventory().clear();
    }

    @EventHandler
    public void onEnd(SpleefGameEndEvent event) {
        event.getSpleefGame().getPlayers().clear();
    }

    @EventHandler
    public void onStart(SpleefStartGameEvent event) {
        SpleefGame game = event.getSpleefGame();
        ItemStack shovel = new ItemBuilder(new ItemStack(Material.IRON_SPADE))
                .setAmount(1)
                .setGlowing(true)
                .setIndistructable(true)
                .build();

        game.getPlayers().forEach(player -> {
            player.setHealth(20);
            player.getInventory().clear();
            player.getInventory().setItem(0, shovel.clone());
        });
    }
}
