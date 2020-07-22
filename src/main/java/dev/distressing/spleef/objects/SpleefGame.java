package dev.distressing.spleef.objects;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.data.enums.LeaveReason;
import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.game.GameStateChangeEvent;
import dev.distressing.spleef.events.game.SpleefStartGameEvent;
import dev.distressing.spleef.events.player.PlayerGameJoin;
import dev.distressing.spleef.events.player.PlayerLeaveEvent;
import dev.distressing.spleef.utils.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class SpleefGame {

    private final Set<Player> players;
    private final Integer minimumToRun;
    private final SpleefArea arena;
    private final Location arenaOrigin;
    private final Location eZoneMin;
    private final Location eZoneMax;
    private final String name;
    private Integer waitTime;
    private GameState gameState;

    public SpleefGame(String name, Location arenaLocation, SpleefArea spleefArea, Integer count) {
        players = new HashSet<>();
        this.minimumToRun = count;
        this.arenaOrigin = arenaLocation.clone();
        this.arena = spleefArea;
        this.name = name;
        this.gameState = GameState.WAITING;

        LocationTriplet eZoneMaxOff = spleefArea.getEZoneMax();
        LocationTriplet eZoneMinOff = spleefArea.getEZoneMin();
        this.eZoneMax = arenaOrigin.clone().add(eZoneMaxOff.getX(), eZoneMaxOff.getY(), eZoneMaxOff.getZ());
        this.eZoneMin = arenaOrigin.clone().add(eZoneMinOff.getX(), eZoneMinOff.getY(), eZoneMinOff.getZ());
    }

    public Location getSpawn() {
        return arena.getSpawn(arenaOrigin);
    }

    public void eliminate(Player player) {
        if (!players.contains(player))
            return;

        PlayerLeaveEvent event = new PlayerLeaveEvent(player, this, LeaveReason.DEATH);
        Bukkit.getPluginManager().callEvent(event);
        players.remove(player);
    }

    public void processJoin(Player player) {
        if (!gameState.equals(GameState.WAITING) && !gameState.equals(GameState.CONFIRMED))
            return;

        if (players.contains(player))
            return;

        players.add(player);
        players.forEach(gamePlayer -> gamePlayer.sendMessage(ChatUtil.color(Messages.PLAYER_JOINED_LOBBY.getWithPrefix(player))));

        Bukkit.getPluginManager().callEvent(new PlayerGameJoin(player, this));
    }

    public void processLeave(Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerLeaveEvent(player, this, LeaveReason.QUIT));
        players.remove(player);
        players.forEach(gamePlayer -> gamePlayer.sendMessage(ChatUtil.color(Messages.PLAYER_QUIT_LOBBY.getWithPrefix(player))));
    }

    public void start() {

        SpleefStartGameEvent event = new SpleefStartGameEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        setGameState(GameState.GRACE);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this));

        if (event.isCancelled()) {
            return;
            //Handle canceled event
        }

        Location spawn = getSpawn();
        players.forEach(player -> player.teleport(spawn));

    }
}
