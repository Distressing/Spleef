package dev.distressing.spleef.runnables;

import dev.distressing.spleef.events.GameStateChangeEvent;
import dev.distressing.spleef.managers.GameManager;
import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Set;

public class SpleefGameTicker implements Runnable {

    private GameManager gameManager;

    public SpleefGameTicker(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        Optional<Set<SpleefGame>> activeOptional = gameManager.getActiveGames();

        if(!activeOptional.isPresent())
            return;

        activeOptional.get().forEach(game -> {
            switch (game.getGameState()) {
                case GRACE:
                    Integer waitTime = game.getWaitTime();
                    if(waitTime != 0) {
                        waitTime--;
                        game.setWaitTime(waitTime);
                        return;
                    }

                    GameStateChangeEvent event = new GameStateChangeEvent(game);
                    Bukkit.getPluginManager().callEvent(event);

            }
        });
    }
}
