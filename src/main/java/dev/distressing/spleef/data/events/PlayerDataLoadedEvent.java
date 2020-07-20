package dev.distressing.spleef.data.events;

import dev.distressing.spleef.data.objects.SpleefPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDataLoadedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private SpleefPlayer spleefPlayer;

    public PlayerDataLoadedEvent(Player player, SpleefPlayer spleefPlayer){
        this.player = player;
        this.spleefPlayer = spleefPlayer;
    }

    public Player getPlayer(){
        return player;
    }

    public SpleefPlayer getSpleefPlayer() {return spleefPlayer;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
