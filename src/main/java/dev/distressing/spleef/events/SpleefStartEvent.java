package dev.distressing.spleef.events;

import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.event.Cancellable;

public class SpleefStartEvent extends SpleefEvent implements Cancellable {

    private SpleefGame spleefGame;
    private boolean canceled;

    public SpleefStartEvent(SpleefGame spleefGame){
        this.spleefGame = spleefGame;
        this.canceled = false;
    }

    public SpleefGame getSpleefGame(){
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
