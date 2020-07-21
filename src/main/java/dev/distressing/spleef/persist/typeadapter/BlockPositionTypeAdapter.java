package dev.distressing.spleef.persist.typeadapter;

import com.google.gson.*;
import net.minecraft.server.v1_8_R3.BlockPosition;

import java.lang.reflect.Type;

public class BlockPositionTypeAdapter implements JsonSerializer<BlockPosition>, JsonDeserializer<BlockPosition> {
    @Override
    public JsonElement serialize(BlockPosition blockPosition, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("x", new JsonPrimitive(blockPosition.getX()));
            object.add("y", new JsonPrimitive(blockPosition.getY()));
            object.add("z", new JsonPrimitive(blockPosition.getZ()));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while serializing a Location.");
            return object;
        }
    }


    @Override
    public BlockPosition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        try {

            return new BlockPosition(
                    object.get("x").getAsDouble(),
                    object.get("y").getAsDouble(),
                    object.get("z").getAsDouble());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while" +
                    " deserializing a Blockposition.");
            return null;
        }


    }
}
