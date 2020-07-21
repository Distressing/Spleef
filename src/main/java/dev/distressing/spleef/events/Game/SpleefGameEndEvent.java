package dev.distressing.spleef.events.Game;

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
