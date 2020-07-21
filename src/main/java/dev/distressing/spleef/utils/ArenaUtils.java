package dev.distressing.spleef.utils;

import dev.distressing.spleef.objects.SpleefGame;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaUtils {
    public static boolean isInArea(SpleefGame game, Player player) {
        return withinBounds(game.getEZoneMin(), game.getEZoneMax(), player.getLocation());
    }

    public static boolean withinBounds(Location loc1, Location loc2, Location checkLocation) {

        if (!loc1.getWorld().equals(loc2.getWorld()))
            return false;

        int x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        return x1 < checkLocation.getBlockX() && x2 > checkLocation.getBlockX() && y1 < checkLocation.getBlockY() && y2 > checkLocation.getBlockY() && z1 < checkLocation.getBlockZ() && z2 > checkLocation.getBlockZ() && checkLocation.getWorld().equals(loc1.getWorld());
    }

    public static Location getMinLocation(Location location, Location location2) {
        int minx, miny, minz;

        minx = Math.min(location.getBlockX(), location2.getBlockX());
        miny = Math.min(location.getBlockY(), location2.getBlockY());
        minz = Math.min(location.getBlockZ(), location2.getBlockZ());

        return new Location(location.getWorld(), minx, miny, minz);
    }

    public static Location getMaxLocation(Location location, Location location2) {
        int minx, miny, minz;

        minx = Math.max(location.getBlockX(), location2.getBlockX());
        miny = Math.max(location.getBlockY(), location2.getBlockY());
        minz = Math.max(location.getBlockZ(), location2.getBlockZ());

        return new Location(location.getWorld(), minx, miny, minz);
    }
}
