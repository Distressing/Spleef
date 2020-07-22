package dev.distressing.spleef.api.managers;

import dev.distressing.spleef.objects.SpleefArea;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class ArenaManager {
    private final HashMap<UUID, SpleefArea> arenas = new HashMap<>();

    public Optional<SpleefArea> getArena(UUID uuid) {
        return Optional.ofNullable(arenas.get(uuid));
    }

    public HashMap<UUID, SpleefArea> getArenas() {
        return arenas;
    }

    public Optional<SpleefArea> getByName(String name) {
        return arenas.values().stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void addArena(SpleefArea area) {
        arenas.put(area.getIdentifier(), area);
        area.refreshArena();
    }

    public void removeArena(SpleefArea area) {
        arenas.remove(area.getIdentifier());
    }

    public void clearCache() {
        arenas.values().forEach(SpleefArea::clearBlocks);
    }

}
