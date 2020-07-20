package dev.distressing.spleef.events;

import dev.distressing.spleef.objects.SpleefGame;

public class GameStateChangeEvent extends SpleefEvent {
    private SpleefGame spleefGame;

    public GameStateChangeEvent(SpleefGame spleefGame){
        this.spleefGame = spleefGame;
    }

    public SpleefGame getSpleefGame(){
        return spleefGame;
    }
}
