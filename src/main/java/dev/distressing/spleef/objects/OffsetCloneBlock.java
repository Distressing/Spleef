package dev.distressing.spleef.objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class OffsetCloneBlock {
    private Material material;
    private LocationTriplet offsetLocation;

    public OffsetCloneBlock(Location origin, Block block) {
        Integer xOffset = block.getLocation().getBlockX() - origin.getBlockX();
        Integer yOffset = block.getLocation().getBlockY() - origin.getBlockY();
        Integer zOffset = block.getLocation().getBlockZ() - origin.getBlockZ();

        material = block.getType();
        offsetLocation = new LocationTriplet(xOffset, yOffset, zOffset);
    }

    public OffsetCloneBlock(int x, int y, int z, Block block) {
        material = block.getType();
        offsetLocation = new LocationTriplet(x, y, z);
    }
}
