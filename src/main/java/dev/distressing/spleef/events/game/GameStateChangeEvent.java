package dev.distressing.spleef.events.game;

import dev.distressing.spleef.objects.SpleefGame;

public class GameStateChangeEvent extends SpleefGameEvent {
    private final SpleefGame spleefGame;

    public GameStateChangeEvent(SpleefGame spleefGame) {
        this.spleefGame = spleefGame;
    }

    public SpleefGame getSpleefGame() {
        return spleefGame;
    }
}
