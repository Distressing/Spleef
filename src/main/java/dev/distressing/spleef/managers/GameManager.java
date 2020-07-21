package dev.distressing.spleef.managers;

import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.events.Game.GameStateChangeEvent;
import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {
    private final HashSet<SpleefGame> games = new HashSet<>();
    private int gameTickRate = 5;

    public void resetGames() {
        games.forEach(game -> {
            game.getPlayers().clear();
            game.setWaitTime(0);
            game.setGameState(GameState.WAITING);
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game));
        });
    }

    public void addGame(SpleefGame spleefGame) {
        games.add(spleefGame);
    }

    public void removeGame(SpleefGame spleefGame) {
        games.remove(spleefGame);
    }

    public Optional<Set<SpleefGame>> getOpenGames() {
        return Optional.of(games.stream().filter(game -> game.getGameState().equals(GameState.WAITING)).collect(Collectors.toSet()));
    }

    public Optional<Set<SpleefGame>> getActiveGames() {
        return Optional.of(games.stream().filter(game -> !game.getGameState().equals(GameState.WAITING)).collect(Collectors.toSet()));
    }

    public Set<SpleefGame> getGames() {
        return games;
    }

    public int getGameTickRate() {
        return gameTickRate;
    }

    public void setGameTickRate(int tickRate) {
        this.gameTickRate = tickRate;
    }

}
