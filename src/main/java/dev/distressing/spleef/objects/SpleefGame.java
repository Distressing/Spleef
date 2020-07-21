package dev.distressing.spleef.objects;

import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.Game.SpleefStartGameEvent;
import dev.distressing.spleef.events.Player.PlayerEliminateEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpleefGame {

    private final List<Player> players;
    private final Integer minimumToRun;
    private Integer waitTime;
    private final SpleefArea arena;
    private final Location arenaOrigin;
    private final Location eZoneMin;
    private final Location eZoneMax;
    private GameState gameState;

    public SpleefGame(Location arenaLocation, SpleefArea spleefArea) {
        players = new ArrayList<>();
        minimumToRun = 2;
        this.arenaOrigin = arenaLocation;
        this.arena = spleefArea;

        LocationTriplet eZoneMaxOff = spleefArea.getEZoneMax();
        LocationTriplet eZoneMinOff = spleefArea.getEZoneMin();
        this.eZoneMax = arenaOrigin.add(eZoneMaxOff.getX(), eZoneMaxOff.getY(), eZoneMaxOff.getZ());
        this.eZoneMin = arenaOrigin.add(eZoneMinOff.getX(), eZoneMinOff.getY(), eZoneMinOff.getZ());
    }

    public void start() {
        SpleefStartGameEvent event = new SpleefStartGameEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) ;
        //Handle canceled event

    }

    public void eliminate(Player player) {
        if (!players.contains(player))
            return;

        PlayerEliminateEvent event = new PlayerEliminateEvent(player, this);
        Bukkit.getPluginManager().callEvent(event);
        players.remove(player);
    }
}
