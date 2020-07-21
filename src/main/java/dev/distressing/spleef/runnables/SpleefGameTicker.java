package dev.distressing.spleef.runnables;

import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.Game.GameStateChangeEvent;
import dev.distressing.spleef.events.Game.SpleefGameEndEvent;
import dev.distressing.spleef.events.Player.PlayerEliminateEvent;
import dev.distressing.spleef.events.Player.PlayerGameWinEvent;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefGame;
import dev.distressing.spleef.utils.ArenaUtils;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Set;

public class SpleefGameTicker implements Runnable {

    private final GameManager gameManager;

    public SpleefGameTicker(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        Optional<Set<SpleefGame>> activeOptional = gameManager.getActiveGames();

        if (!activeOptional.isPresent())
            return;

        activeOptional.get().forEach(game -> {
            switch (game.getGameState()) {
                case GRACE:
                    int waitTime = game.getWaitTime();
                    if (waitTime != 0) {
                        waitTime--;
                        game.setWaitTime(waitTime);
                        return;
                    }

                    Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    break;

                case RUNNING:

                    if (game.getPlayers().size() <= 1) {
                        if (game.getPlayers().size() == 1) {
                            Bukkit.getPluginManager().callEvent(new PlayerGameWinEvent(game.getPlayers().get(0), game));
                        }

                        Bukkit.getPluginManager().callEvent(new SpleefGameEndEvent(game));
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    }

                    game.getPlayers().stream().filter(player -> ArenaUtils.isInArea(game, player)).forEach(player -> {
                        PlayerEliminateEvent event1 = new PlayerEliminateEvent(player, game);
                        Bukkit.getPluginManager().callEvent(event1);
                    });

                    game.setWaitTime(10);

                    break;

                case WAITING:
                    if (game.getPlayers().size() >= game.getMinimumToRun()) {
                        game.setGameState(GameState.CONFIRMED);
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                        game.setWaitTime(10);
                    }
                    break;

                case CONFIRMED:
                    if (game.getPlayers().size() <= 1) {
                        game.setGameState(GameState.WAITING);
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    }

                default:
                    break;
            }
        });
    }
}
