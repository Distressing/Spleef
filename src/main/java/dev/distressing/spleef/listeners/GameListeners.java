package dev.distressing.spleef.listeners;

import dev.distressing.spleef.api.events.game.GameStateChangeEvent;
import dev.distressing.spleef.api.events.game.SpleefGameEndEvent;
import dev.distressing.spleef.api.events.game.SpleefStartGameEvent;
import dev.distressing.spleef.api.events.player.PlayerGameWinEvent;
import dev.distressing.spleef.api.managers.GameManager;
import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.objects.SpleefArea;
import dev.distressing.spleef.objects.SpleefGame;
import dev.distressing.spleef.utils.ChatUtil;
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
                String confirmed = ChatUtil.color(Messages.GAME_STARTING.getWithPrefix().replace("%time%", game.getWaitTime() / gameManager.getGameTickRate() + ""));
                game.getPlayers().forEach(player -> player.sendMessage(confirmed));
                break;

            case GRACE:
                String grace = ChatUtil.color(Messages.GAME_GRACE_START.getWithPrefix());
                game.getPlayers().forEach(player -> {
                    player.sendMessage(grace);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                });
                break;

            case RUNNING:
                String running = ChatUtil.color(Messages.GAME_GRACE_END.getWithPrefix());
                game.getPlayers().forEach(player -> player.sendMessage(running));
                break;

            case WAITING:
                SpleefArea area = game.getArea();

                if(area != null)
                    return;

                gameManager.removeGame(game);
                System.out.println(game.getName()+" was deleted due to it missing an arena");

            default:
                break;
        }

    }

    @EventHandler
    public void onWin(PlayerGameWinEvent event) {
        Player player = event.getPlayer();

        Bukkit.broadcastMessage(ChatUtil.color(Messages.GAME_WIN.getWithPrefix(player)));
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
