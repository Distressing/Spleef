package dev.distressing.spleef.objects;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.managers.AreaCreationManager;
import dev.distressing.spleef.managers.ArenaManager;
import dev.distressing.spleef.utils.ArenaUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class SpleefAreaBuilder {
    private final String name;
    private final Player player;
    private int creationStep;
    private Location pos1, pos2, eZonePos1, eZonePos2, spawnPoint;

    public SpleefAreaBuilder(Player player, String name) {
        this.name = name;
        this.player = player;
        this.creationStep = 0;
    }

    public boolean outsideDefinedBounds(Location location) {
        return !ArenaUtils.withinBounds(pos1, pos2, location);
    }

    public boolean withinElimination(Location location) {
        return ArenaUtils.withinBounds(eZonePos1, eZonePos2, location);
    }

    public void increment() {
        creationStep++;
    }

    public void build(ArenaManager arenaManager, AreaCreationManager areaCreationManager) {
        if (arenaManager.getArena(name).isPresent()) {
            player.sendMessage(Messages.AREA_EXISTS.getWithPrefix());
            areaCreationManager.stopBuilder(player);
            return;
        }

        Location minLocation = ArenaUtils.getMinLocation(pos1, pos2);
        Location maxLocation = ArenaUtils.getMaxLocation(pos1, pos2);
        Location minEliminationLoc = ArenaUtils.getMinLocation(eZonePos1, eZonePos2);
        Location maxEliminationLoc = ArenaUtils.getMaxLocation(eZonePos1, eZonePos2);

        SpleefArea area = new SpleefArea(minLocation, maxLocation, spawnPoint, minEliminationLoc, maxEliminationLoc);
        arenaManager.addArena(name, area);
        areaCreationManager.stopBuilder(player);

        player.sendMessage(Messages.AREA_CREATION_SUCCESS.getWithPrefix());
    }

}
