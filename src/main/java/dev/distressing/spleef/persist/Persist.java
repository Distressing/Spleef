package dev.distressing.spleef.persist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.distressing.spleef.SpleefPlugin;
import dev.distressing.spleef.persist.typeadapter.BlockPositionTypeAdapter;
import dev.distressing.spleef.persist.typeadapter.ChunkCoordTypeAdapter;
import dev.distressing.spleef.persist.typeadapter.LocationTripletTypeAdapter;
import dev.distressing.spleef.persist.typeadapter.LocationTypeAdapter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

public class Persist {


    private final Gson gson = buildGson().create();

    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //

    public static String getName(Object o) {
        return getName(o.getClass());
    }

    public static String getName(Type type) {
        return getName(type.getClass());
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(BlockPosition.class, new BlockPositionTypeAdapter())
                .registerTypeAdapter(ChunkCoordIntPair.class, new ChunkCoordTypeAdapter())
                .registerTypeAdapter(LocationTripletTypeAdapter.class, new LocationTripletTypeAdapter());
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object?
    // ------------------------------------------------------------ //

    public File getFile(String name) {
        return new File(SpleefPlugin.getInstance().getDataFolder(), name + ".json");
    }

    public File getFile(Class<?> clazz) {
        return getFile(getName(clazz));
    }

    public File getFile(Object obj) {
        return getFile(getName(obj));
    }

    // SAVE

    public void save(Object instance) {
        save(instance, getFile(instance));
    }

    public void save(Object instance, File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DiscUtil.writeCatch(file, gson.toJson(instance));
    }

    // LOAD BY CLASS

    public <T> T load(Class<T> clazz) {
        return load(clazz, getFile(clazz));
    }

    public <T> T load(Class<T> clazz, File file) {
        String content = DiscUtil.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return gson.fromJson(content, clazz);
        } catch (Exception ex) {
            SpleefPlugin.getInstance().getLogger().severe("Failed to parse " + file.toString() + ": " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(SpleefPlugin.getInstance());
        }

        return null;
    }
}