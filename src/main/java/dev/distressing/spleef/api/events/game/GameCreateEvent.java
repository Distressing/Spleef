package dev.distressing.spleef.api.events.game;

import dev.distressing.spleef.objects.SpleefGame;
import lombok.Getter;

@Getter
public class GameCreateEvent extends SpleefGameEvent {

    private SpleefGame spleefGame;

    public GameCreateEvent(SpleefGame spleefGame){
        this.spleefGame = spleefGame;
    }
}
