package dev.distressing.spleef.events.Game;

import dev.distressing.spleef.objects.SpleefGame;

public class GameTieEvent extends SpleefGameEvent {
    private final SpleefGame spleefGame;

    public GameTieEvent(SpleefGame spleefGame) {
        this.spleefGame = spleefGame;
    }

    public SpleefGame getSpleefGame() {
        return spleefGame;
    }
}
