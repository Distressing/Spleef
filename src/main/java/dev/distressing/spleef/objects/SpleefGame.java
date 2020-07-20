package dev.distressing.spleef.objects;

import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.SpleefStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpleefGame {

    private List<Player> players;
    private Integer minimumToRun;
    private Integer waitTime;
    private SpleefArea arena;
    private Location arenaLocation;
    private GameState gameState;

    public SpleefGame(Location arenaLocation, SpleefArea spleefArea) {
        players = new ArrayList<>();
        minimumToRun = 2;
        this.arenaLocation = arenaLocation;
        this.arena = spleefArea;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public SpleefArea getSpleefArea() {
        return arena;
    }

    public void setArena(SpleefArea arena){
        this.arena = arena;
    }

    public Integer getMinimumToRun(){
        return minimumToRun;
    }

    public Location getArenaLocation() {
        return arenaLocation;
    }

    public void setArenaLocation(Location arenaLocation) {
        this.arenaLocation = arenaLocation;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void start() {
        SpleefStartEvent event = new SpleefStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled());
            //Handle canceled event

    }
}
