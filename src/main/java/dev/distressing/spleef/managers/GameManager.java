package dev.distressing.spleef.managers;

import dev.distressing.spleef.enums.GameState;
import dev.distressing.spleef.objects.SpleefGame;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {
    private HashSet<SpleefGame> games = new HashSet<>();

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

}
