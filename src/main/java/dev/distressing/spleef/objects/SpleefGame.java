package dev.distressing.spleef.objects;

import dev.distressing.spleef.api.SpleefPluginApi;
import dev.distressing.spleef.api.enums.GameState;
import dev.distressing.spleef.api.enums.LeaveReason;
import dev.distressing.spleef.api.events.game.GameStateChangeEvent;
import dev.distressing.spleef.api.events.game.SpleefStartGameEvent;
import dev.distressing.spleef.api.events.player.PlayerGameJoin;
import dev.distressing.spleef.api.events.player.PlayerLeaveEvent;
import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.utils.ChatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SpleefGame {

    private final Set<Player> players;
    private final Integer minimumToRun;
    private final UUID arenaID;
    private final Location gameOrigin;
    private final Location eZoneMin;
    private final Location eZoneMax;
    private final String name;
    private Integer waitTime;
    private GameState gameState;

    public SpleefGame(String name, Location gameOrigin, SpleefArea spleefArea, Integer count) {
        players = new HashSet<>();
        this.minimumToRun = count;
        this.gameOrigin = gameOrigin.clone();
        this.arenaID = spleefArea.getIdentifier();
        this.name = name;
        this.gameState = GameState.WAITING;

        LocationTriplet eZoneMaxOff = spleefArea.getEZoneMax();
        LocationTriplet eZoneMinOff = spleefArea.getEZoneMin();
        this.eZoneMax = this.gameOrigin.clone().add(eZoneMaxOff.getX(), eZoneMaxOff.getY(), eZoneMaxOff.getZ());
        this.eZoneMin = this.gameOrigin.clone().add(eZoneMinOff.getX(), eZoneMinOff.getY(), eZoneMinOff.getZ());
    }

    public Optional<Location> getSpawn() {
        SpleefArea arena = getArea();

        if(arena == null){
            return Optional.empty();
        }

        return Optional.ofNullable(arena.getSpawn(getGameOrigin()));
    }

    @Nullable
    public SpleefArea getArea(){

        Optional<SpleefArea> spleefAreaOptional = SpleefPluginApi.getArenaManager().getArena(arenaID);

        if(!spleefAreaOptional.isPresent()) {
            System.out.println("No arena found for game:"+getName());
            return null;
        }
        return spleefAreaOptional.get();
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

        if (event.isCancelled()) {
            setGameState(GameState.WAITING);
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this));
            return;
            //Handle canceled event
        }

        setGameState(GameState.GRACE);
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this));

        Optional<Location> spawn = getSpawn();

        if(spawn.isPresent()) {
            players.forEach(player -> player.teleport(spawn.get()));
        } else {
            players.forEach(player -> Bukkit.getPluginManager().callEvent(new PlayerLeaveEvent(player, this, LeaveReason.FORCED)));
            setGameState(GameState.WAITING);
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this));
        }

    }
}
