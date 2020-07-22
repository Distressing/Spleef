package dev.distressing.spleef.managers;

import dev.distressing.spleef.configuration.Messages;
import dev.distressing.spleef.objects.SpleefAreaBuilder;
import dev.distressing.spleef.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AreaCreationManager {
    private final ArenaManager arenaManager;
    private final HashMap<UUID, SpleefAreaBuilder> areaBuilders = new HashMap<>();

    public AreaCreationManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    public boolean isBuilding(Player player) {
        return areaBuilders.containsKey(player.getUniqueId());
    }

    public void startBuilder(Player player, String name) {

        if (arenaManager.getArena(name).isPresent()) {
            player.sendMessage(Messages.AREA_EXISTS.getWithPrefix());
            return;
        }

        areaBuilders.put(player.getUniqueId(), new SpleefAreaBuilder(player, name));
        player.sendMessage(ChatUtil.color(Messages.AREA_DEFINE_START.getWithPrefix()));
    }

    public void stopBuilder(Player player) {
        areaBuilders.remove(player.getUniqueId());
    }

    public void processBuilderInteract(Player player, Block block) {
        SpleefAreaBuilder builder = areaBuilders.get(player.getUniqueId());
        Location location = block.getLocation();
        boolean failiure = false;

        switch (builder.getCreationStep()) {

            case 0:
                builder.setPos1(location);
                player.sendMessage(Messages.AREA_DEFINE_POS1.getWithPrefix());
                break;
            case 1:
                builder.setPos2(location);
                player.sendMessage(Messages.AREA_DEFINE_POS2.getWithPrefix());
                break;
            case 2:
                if (builder.outsideDefinedBounds(location)) {
                    failiure = true;
                    player.sendMessage(Messages.AREA_OUTSIDE_BOUNDS.getWithPrefix());
                    break;
                }
                builder.setEZonePos1(location);
                player.sendMessage(Messages.AREA_DEFINE_ELIM_POS1.getWithPrefix());
                break;
            case 3:
                if (builder.outsideDefinedBounds(location)) {
                    failiure = true;
                    player.sendMessage(Messages.AREA_OUTSIDE_BOUNDS.getWithPrefix());
                    break;
                }
                builder.setEZonePos2(location);
                player.sendMessage(Messages.AREA_DEFINE_ELIM_POS2.getWithPrefix());
                break;
            case 4:
                if (builder.outsideDefinedBounds(location)) {
                    failiure = true;
                    player.sendMessage(Messages.AREA_OUTSIDE_BOUNDS.getWithPrefix());
                    break;
                }

                if (builder.withinElimination(location)) {
                    failiure = true;
                    player.sendMessage(Messages.AREA_WITHIN_ELIMINATION.getWithPrefix());
                    break;
                }

                builder.setSpawnPoint(location.add(0, 1, 0));
                player.sendMessage(Messages.AREA_DEFINE_SPAWN.getWithPrefix().replace("%name%", builder.getName()));
                builder.build(arenaManager, this);
                break;
            default:
                //This should never be met
                player.sendMessage("Please send a message to Distressing#0175 on discord as and error has occurred whilst creating a spleef arena");
                break;
        }
        if (!failiure)
            builder.increment();

    }

}
