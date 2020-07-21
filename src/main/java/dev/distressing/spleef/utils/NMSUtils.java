package dev.distressing.spleef.utils;

import dev.distressing.spleef.objects.DBlock;
import dev.distressing.spleef.objects.DBlockPosition;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NMSUtils {

    public static void spawnArena(HashMap<ChunkCoordIntPair, HashSet<DBlock>> request, Location location) {
        Map<org.bukkit.Material, IBlockData> dataMap = new HashMap<>();
        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        request.forEach((key, value) -> {
            Chunk nmsChunk = nmsWorld.getChunkAt(key.x, key.z);
            System.out.println("Pasting " + value.size());

            final ChunkSection[] chunkSection = {null};

            value.forEach(dBlock -> {
                BlockPosition blockPosition = getBlockPosition(dBlock.getLocation());
                int indexY = blockPosition.getY() >> 4;

                if (chunkSection[0] == null || chunkSection[0].getYPosition() != indexY) {
                    chunkSection[0] = nmsChunk.getSections()[indexY];
                    if (chunkSection[0] == null)
                        chunkSection[0] = nmsChunk.getSections()[indexY] = new ChunkSection(indexY << 4, !nmsChunk.world.worldProvider.o());
                }

                IBlockData data = dataMap.computeIfAbsent(dBlock.getMaterial(), mat -> CraftMagicNumbers.getBlock(dBlock.getMaterial()).getBlockData());
                chunkSection[0].setType(blockPosition.getX() & 15, blockPosition.getY() & 15, blockPosition.getZ() & 15, data);
            });
            refreshChunk(nmsChunk, location.getWorld());
        });
    }

    private static void refreshChunk(Chunk chunk, org.bukkit.World world) {

        chunk.e();
        HashSet<Packet> packets = new HashSet<>();

        packets.add(new PacketPlayOutMapChunk(chunk, false, 65280));
        packets.add(new PacketPlayOutMapChunk(chunk, false, 255));
        chunk.getTileEntities().forEach((key, value) -> packets.add(value.getUpdatePacket()));

        int viewDistance = Bukkit.getViewDistance() << 4;

        Set<EntityPlayer> players = world.getNearbyEntities(new Location(world, chunk.locX << 4, 100, chunk.locZ << 4), viewDistance, 256, viewDistance).stream()
                .filter(ent -> ent instanceof Player)
                .map(entity -> ((CraftPlayer) entity).getHandle())
                .collect(Collectors.toSet());

        packets.forEach(packet -> {
            players.forEach(player -> player.playerConnection.sendPacket(packet));
        });
    }

    private static BlockPosition getBlockPosition(DBlockPosition dBlockPosition) {
        return new BlockPosition(dBlockPosition.getX(), dBlockPosition.getY(), dBlockPosition.getZ());
    }
}
