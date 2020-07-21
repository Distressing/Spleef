package dev.distressing.spleef.persist.typeadapter;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("x", new JsonPrimitive(location.getX()));
            object.add("y", new JsonPrimitive(location.getY()));
            object.add("z", new JsonPrimitive(location.getZ()));
            object.add("pitch", new JsonPrimitive(location.getPitch()));
            object.add("yaw", new JsonPrimitive(location.getYaw()));
            object.add("world", new JsonPrimitive(location.getWorld() == null ? Bukkit.getWorlds().get(0).getName() : location.getWorld().getName()));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while serializing a Location.");
            return object;
        }
    }


    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            Location location = new Location(Bukkit.getWorld(object.get("world").getAsString()),
                    object.get("x").getAsDouble(),
                    object.get("y").getAsDouble(),
                    object.get("z").getAsDouble(),
                    object.get("yaw").getAsFloat(),
                    object.get("pitch").getAsFloat());
            return location;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while" +
                    " deserializing a Location.");
            return null;
        }


    }
}
