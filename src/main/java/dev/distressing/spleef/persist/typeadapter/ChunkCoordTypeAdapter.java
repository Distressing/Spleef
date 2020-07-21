package dev.distressing.spleef.persist.typeadapter;

import com.google.gson.*;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;

import java.lang.reflect.Type;

public class ChunkCoordTypeAdapter implements JsonSerializer<ChunkCoordIntPair>, JsonDeserializer<ChunkCoordIntPair> {
    @Override
    public JsonElement serialize(ChunkCoordIntPair CCIP, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("x", new JsonPrimitive(CCIP.x));
            object.add("z", new JsonPrimitive(CCIP.z));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while serializing a chunk location.");
            return object;
        }
    }


    @Override
    public ChunkCoordIntPair deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        try {
            return new ChunkCoordIntPair(object.get("x").getAsInt(), object.get("z").getAsInt());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error occurred whilst deserializing Chunk location");
            return null;
        }
    }
}
