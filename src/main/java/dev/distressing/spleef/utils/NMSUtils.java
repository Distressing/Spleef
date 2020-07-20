package dev.distressing.spleef.utils;

import dev.distressing.spleef.objects.DBlock;
import dev.distressing.spleef.objects.DBlockPosition;
import dev.distressing.spleef.objects.LocationTriplet;
import dev.distressing.spleef.objects.SpleefArea;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NMSUtils {

    private Map<org.bukkit.Material, IBlockData> dataMap = new HashMap<>();

    public void spawnArena(HashMap<ChunkCoordIntPair, HashSet<DBlock>> request, Location location) {
        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld)location.getWorld()).getHandle();
        request.forEach((key, value) -> {
            Chunk nmsChunk = nmsWorld.getChunkAt(key.x, key.z);

            value.forEach(dBlock -> {
                BlockPosition blockPosition = getBlockPosition(dBlock.getLocation());
                int indexY = blockPosition.getY() >> 4;
                ChunkSection section = nmsChunk.getSections()[indexY];

                if(section == null)
                    section = nmsChunk.getSections()[indexY] = new ChunkSection(indexY << 4, !nmsChunk.world.worldProvider.o());

                IBlockData data = dataMap.computeIfAbsent(dBlock.getMaterial(), mat -> CraftMagicNumbers.getBlock(dBlock.getMaterial()).getBlockData());
                section.setType(blockPosition.getX() & 16, blockPosition.getY() & 16, blockPosition.getZ() & 16, data);
            });

        });
    }

    public void refreshArena(SpleefArea area, World world, Location origin) {
        HashSet<ChunkCoordIntPair> chunkCoordIntPairs = new HashSet<>();

        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld)world).getHandle();

        int xmin = area.getArenaMinLocation().getBlockX() >> 4;
        int xmax = area.getArenaMaxLocation().getBlockX() >> 4;
        int zmin = area.getArenaMinLocation().getBlockZ() >> 4;
        int zmax = area.getArenaMaxLocation().getBlockZ() >> 4;

        for(; xmin <= xmax; xmin++){
            for(; zmin <= zmax; zmax++){
                chunkCoordIntPairs.add(new ChunkCoordIntPair(xmin, zmin));
            }
        }

        HashSet<Packet> packets = new HashSet<>();

        chunkCoordIntPairs.stream().filter(pair -> ((CraftWorld)world).getHandle().getPlayerChunkMap().isChunkInUse(pair.x, pair.z)).map(pair -> nmsWorld.getChunkAt(pair.x, pair.z)).forEach(chunk -> {
            packets.add(new PacketPlayOutMapChunk(chunk, false, 65280));
            packets.add(new PacketPlayOutMapChunk(chunk, false, 255));
            chunk.getTileEntities().forEach((key, value) -> packets.add(value.getUpdatePacket()));
        });

        LocationTriplet center = area.getCenterOffset();
        int viewDistance = Bukkit.getViewDistance() << 4;

        Set<EntityPlayer> players = world.getNearbyEntities(origin.add(center.getX(), center.getY(), center.getZ()), viewDistance, 256, viewDistance).stream()
                .filter(ent -> ent instanceof Player)
                .map(entity -> ((CraftPlayer)entity).getHandle())
                .collect(Collectors.toSet());

        packets.forEach(packet -> {
            players.forEach(player -> player.playerConnection.sendPacket(packet));
        });
    }

    private BlockPosition getBlockPosition(DBlockPosition dBlockPosition) {
        return new BlockPosition(dBlockPosition.getX(), dBlockPosition.getY(), dBlockPosition.getZ());
    }
}
