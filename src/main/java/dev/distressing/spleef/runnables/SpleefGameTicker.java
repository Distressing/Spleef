package dev.distressing.spleef.runnables;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.Game.GameStateChangeEvent;
import dev.distressing.spleef.events.Game.GameTieEvent;
import dev.distressing.spleef.events.Game.SpleefGameEndEvent;
import dev.distressing.spleef.events.Player.PlayerGameWinEvent;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefGame;
import dev.distressing.spleef.utils.ArenaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class SpleefGameTicker implements Runnable {

    private final GameManager gameManager;

    public SpleefGameTicker(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        Set<SpleefGame> active = gameManager.getGames();

        active.forEach(game -> {

            switch (game.getGameState()) {
                case GRACE:
                    int waitTime = game.getWaitTime();
                    if (waitTime != 0) {
                        waitTime--;
                        game.setWaitTime(waitTime);
                        return;
                    }

                    game.setGameState(GameState.RUNNING);
                    Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    break;

                case RUNNING:

                    if (game.getPlayers().size() <= 1) {
                        if (game.getPlayers().size() == 1) {
                            Player player = new ArrayList<Player>(game.getPlayers()).get(0);
                            Bukkit.getPluginManager().callEvent(new PlayerGameWinEvent(player, game));
                        } else {
                            Bukkit.getPluginManager().callEvent(new GameTieEvent(game));
                        }

                        Bukkit.getPluginManager().callEvent(new SpleefGameEndEvent(game));
                        game.setGameState(GameState.WAITING);
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    }

                    Set<Player> players = game.getPlayers().stream().filter(player -> ArenaUtils.isInArea(game, player)).collect(Collectors.toSet());
                    players.forEach(game::eliminate);

                    break;

                case WAITING:
                    if (game.getPlayers().size() >= game.getMinimumToRun()) {
                        game.setGameState(GameState.CONFIRMED);
                        game.setWaitTime(10 * gameManager.getGameTickRate());
                        game.getArena().paste(game.getArenaOrigin());
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    }
                    break;

                case CONFIRMED:
                    if (game.getPlayers().size() <= 1) {
                        game.setGameState(GameState.WAITING);
                        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
                    }
                    int waitTime2 = game.getWaitTime();
                    if (waitTime2 != 0) {
                        waitTime2--;
                        game.setWaitTime(waitTime2);

                        if (waitTime2 < 4 * gameManager.getGameTickRate() && (waitTime2 & gameManager.getGameTickRate()) == 0) {
                            String message = Messages.GAME_STARTING.getWithPrefix().replace("%time%", (waitTime2 / gameManager.getGameTickRate()) + "");
                            game.getPlayers().forEach(player -> player.sendMessage(message));
                        }

                        return;
                    }

                    game.start();
                    game.setWaitTime(10 * gameManager.getGameTickRate());

                    break;

                default:
                    break;
            }
        });
    }
}
