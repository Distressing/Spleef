package dev.distressing.spleef.managers;

import dev.distressing.spleef.objects.SpleefArea;

import java.util.HashMap;
import java.util.Optional;

public class ArenaManager {
    private HashMap<String, SpleefArea> arenas = new HashMap<>();

    public Optional<SpleefArea> getArena(String string) {
        return Optional.ofNullable(arenas.get(string.toLowerCase()));
    }

}
