package dev.distressing.spleef.api.managers;

import dev.distressing.spleef.api.enums.GameState;
import dev.distressing.spleef.api.events.game.GameCreateEvent;
import dev.distressing.spleef.api.events.game.GameDeleteEvent;
import dev.distressing.spleef.api.events.game.GameStateChangeEvent;
import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {
    private final HashSet<SpleefGame> games = new HashSet<>();
    private int gameTickRate = 5;

    public Optional<Set<SpleefGame>> getOpenGames() {
        return Optional.of(games.stream().filter(game -> game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.CONFIRMED)).collect(Collectors.toSet()));
    }

    public Optional<Set<SpleefGame>> getActiveGames() {
        return Optional.of(games.stream().filter(game -> !game.getGameState().equals(GameState.WAITING) && !game.getGameState().equals(GameState.CONFIRMED)).collect(Collectors.toSet()));
    }

    public Set<SpleefGame> getGames() {
        return games;
    }

    public Optional<SpleefGame> getGame(Player player) {
        return games.stream().filter(game -> game.getPlayers().contains(player)).findFirst();
    }

    public int getGameTickRate() {
        return gameTickRate;
    }

    public void setGameTickRate(int tickRate) {
        this.gameTickRate = tickRate;
    }

    public void resetGames() {
        games.forEach(game -> {
            game.getPlayers().clear();
            game.setWaitTime(0);
            game.getPlayers().forEach(game::eliminate);
            game.setGameState(GameState.WAITING);
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
        });
    }

    public void addGame(SpleefGame spleefGame) {
        games.add(spleefGame);
        Bukkit.getPluginManager().callEvent(new GameCreateEvent(spleefGame));
    }

    public void removeGame(SpleefGame spleefGame) {
        games.remove(spleefGame);
        Bukkit.getPluginManager().callEvent(new GameDeleteEvent(spleefGame));
    }

    public void shutdown() {
        games.forEach(game -> {
            game.getPlayers().forEach(game::processLeave);
            game.getPlayers().clear();
        });
    }

    public void processJoinRequest(Player player, SpleefGame spleefGame) {
        spleefGame.processJoin(player);
    }

    public void processLeaveRequest(Player player) {
        processLogout(player);
    }

    public void processLogout(Player player) {
        games.stream().filter(game -> game.getPlayers().contains(player)).forEach(game -> game.processLeave(player));
    }
}
