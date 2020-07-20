package dev.distressing.spleef.objects;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Getter
public class SpleefArea {
    private List<OffsetCloneBlock> spleefArenaBlocks;
    private Location arenaMinLocation;
    private Location arenaMaxLocation;
    private LocationTriplet eZoneMin;
    private LocationTriplet eZoneMax;
    private LocationTriplet spawnPoint;
    private Location center;
    private LocationTriplet centerOffset;

    public SpleefArea(Location minLocation, Location maxLocation, Location spawnPoint, Location eZoneMin, Location eZoneMax) {
        this.arenaMinLocation = minLocation;
        this.arenaMaxLocation = maxLocation;
        this.spawnPoint = new LocationTriplet(spawnPoint.getBlockX(), spawnPoint.getBlockY(), spawnPoint.getBlockZ());

        //Obtain center of arena via the mean
        this.center = new Location(
                minLocation.getWorld(),
                (minLocation.getBlockX()+maxLocation.getBlockX())/2,
                (minLocation.getBlockY()+maxLocation.getBlockY())/2,
                (minLocation.getBlockZ()+maxLocation.getBlockZ())/2
        );

        centerOffset = new LocationTriplet(center.getBlockX() - minLocation.getBlockX(), center.getBlockY() - minLocation.getBlockY(), center.getBlockZ() - minLocation.getBlockZ());

        refreshArena();
    }

    public Optional<List<OffsetCloneBlock>> getArena(){
        return Optional.ofNullable(spleefArenaBlocks);
    }

    public void refreshArena(){
        if(!arenaMinLocation.getWorld().equals(arenaMaxLocation.getWorld())) {
            Bukkit.getLogger().log(Level.SEVERE, "Arena points must be in the same world");
            return;
        }

        List<OffsetCloneBlock> blocks = new ArrayList<>();

        for(int x = 0; arenaMinLocation.getBlockX() + x < arenaMaxLocation.getBlockX(); x++){
            for(int y = 0; arenaMinLocation.getBlockY() + y < arenaMaxLocation.getBlockY(); y++){
                for(int z = 0; arenaMinLocation.getBlockZ() + z < arenaMaxLocation.getBlockZ(); z++){
                    OffsetCloneBlock offsetCloneBlock = new OffsetCloneBlock(x,y,z, arenaMinLocation.add(x,y,z).getBlock());
                    blocks.add(offsetCloneBlock);
                }
            }
        }
        spleefArenaBlocks = blocks;
    }

    public Location getSpawn(Location origin) {
        Location spawn = origin.clone();
        return spawn.add(spawnPoint.getx(), spawnPoint.gety(), spawnPoint.gety());
    }

    public LocationTriplet getCenterOffset() {
        return centerOffset;
    }

    public void paste(Location origin) {

    }
}
