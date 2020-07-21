package dev.distressing.spleef.events.Game;

import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.event.Cancellable;

public class SpleefStartGameEvent extends SpleefGameEvent implements Cancellable {

    private final SpleefGame spleefGame;
    private boolean canceled;

    public SpleefStartGameEvent(SpleefGame spleefGame) {
        this.spleefGame = spleefGame;
        this.canceled = false;
    }

    public SpleefGame getSpleefGame() {
        return spleefGame;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean b) {
        canceled = b;
    }
}
