package dev.distressing.spleef.objects;

import dev.distressing.spleef.utils.ArenaUtils;
import dev.distressing.spleef.utils.NMSUtils;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Getter
public class SpleefArea {
    private final Location arenaMinLocation;
    private final Location arenaMaxLocation;
    private final LocationTriplet spawnPoint;
    private final Location center;
    private final LocationTriplet centerOffset;
    private final LocationTriplet eZoneMin;
    private final LocationTriplet eZoneMax;
    private final UUID identifier;
    private final String name;
    private List<OffsetCloneBlock> spleefArenaBlocks;

    public SpleefArea(String name, Location minLocation, Location maxLocation, Location spawnPoint, Location eZoneMin, Location eZoneMax) {
        this.arenaMinLocation = minLocation;
        this.arenaMaxLocation = maxLocation;
        this.identifier = UUID.randomUUID();
        this.name = name;

        this.eZoneMin = ArenaUtils.getOffsetLocationTriplet(minLocation, eZoneMin);
        this.eZoneMax = ArenaUtils.getOffsetLocationTriplet(minLocation, eZoneMax);
        this.spawnPoint = ArenaUtils.getOffsetLocationTriplet(minLocation, spawnPoint);

        //Obtain center of arena via the mean
        this.center = new Location(
                minLocation.getWorld(),
                (minLocation.getBlockX() + maxLocation.getBlockX()) / 2,
                (minLocation.getBlockY() + maxLocation.getBlockY()) / 2,
                (minLocation.getBlockZ() + maxLocation.getBlockZ()) / 2
        );

        centerOffset = ArenaUtils.getOffsetLocationTriplet(minLocation, center);
        spleefArenaBlocks = new ArrayList<>();
        refreshArena();
    }

    public Optional<List<OffsetCloneBlock>> getArena() {
        return Optional.ofNullable(spleefArenaBlocks);
    }

    public Location getSpawn(Location origin) {
        Location spawn = origin.clone();
        return spawn.clone().add(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());
    }

    public LocationTriplet getCenterOffset() {
        return centerOffset;
    }

    public void paste(Location origin) {

        if (spleefArenaBlocks.isEmpty())
            refreshArena();

        HashMap<ChunkCoordIntPair, HashSet<DBlock>> blocks = new HashMap<>();
        AtomicReference<ChunkCoordIntPair> currentCCIP = new AtomicReference<>(new ChunkCoordIntPair(0, 0));
        int oX = origin.getBlockX();
        int oY = origin.getBlockY();
        int oZ = origin.getBlockZ();
        spleefArenaBlocks.forEach(block -> {
            int x = oX + block.getOffsetLocation().getX();
            int y = oY + block.getOffsetLocation().getY();
            int z = oZ + block.getOffsetLocation().getZ();

            if (currentCCIP.get().x != x >> 4 || currentCCIP.get().z != z >> 4)
                currentCCIP.set(new ChunkCoordIntPair(x >> 4, z >> 4));

            DBlock newBlock = new DBlock(new DBlockPosition(x, y, z), block.getMaterial());
            HashSet<DBlock> dBlocks = blocks.getOrDefault(currentCCIP.get(), new HashSet<>());
            dBlocks.add(newBlock);
            blocks.put(currentCCIP.get(), dBlocks);
        });

        NMSUtils.spawnArena(blocks, origin);
    }

    public void refreshArena() {
        spleefArenaBlocks.clear();
        if (!arenaMinLocation.getWorld().equals(arenaMaxLocation.getWorld())) {
            Bukkit.getLogger().log(Level.SEVERE, "Arena points must be in the same world");
            return;
        }

        List<OffsetCloneBlock> blocks = new ArrayList<>();

        for (int x = 0; arenaMinLocation.getBlockX() + x <= arenaMaxLocation.getBlockX(); x++) {
            for (int y = 0; arenaMinLocation.getBlockY() + y <= arenaMaxLocation.getBlockY(); y++) {
                for (int z = 0; arenaMinLocation.getBlockZ() + z <= arenaMaxLocation.getBlockZ(); z++) {
                    OffsetCloneBlock offsetCloneBlock = new OffsetCloneBlock(x, y, z, arenaMinLocation.clone().add(x, y, z).getBlock());
                    blocks.add(offsetCloneBlock);
                }
            }
        }
        blocks.removeIf(block -> block.getMaterial().equals(Material.AIR));
        spleefArenaBlocks = blocks;
    }

    public void clearBlocks() {
        spleefArenaBlocks.clear();
    }
}
