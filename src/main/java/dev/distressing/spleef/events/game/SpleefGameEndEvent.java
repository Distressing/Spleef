package dev.distressing.spleef.events.game;

import dev.distressing.spleef.objects.SpleefGame;

public class SpleefGameEndEvent extends SpleefGameEvent {

    private final SpleefGame spleefGame;

    public SpleefGameEndEvent(SpleefGame spleefGame) {
        this.spleefGame = spleefGame;
    }

    public SpleefGame getSpleefGame() {
        return spleefGame;
    }

}
