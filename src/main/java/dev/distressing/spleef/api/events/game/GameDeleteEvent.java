package dev.distressing.spleef.api.events.game;

import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;

@Getter
public class GameDeleteEvent extends SpleefGameEvent {

    private SpleefGame spleefGame;

    public GameDeleteEvent(SpleefGame spleefGame){
        this.spleefGame = spleefGame;
    }
}
