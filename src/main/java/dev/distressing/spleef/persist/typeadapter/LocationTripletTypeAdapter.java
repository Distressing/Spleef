package dev.distressing.spleef.persist.typeadapter;

import com.google.gson.*;
import dev.distressing.spleef.objects.LocationTriplet;

import java.lang.reflect.Type;

public class LocationTripletTypeAdapter implements JsonSerializer<LocationTriplet>, JsonDeserializer<LocationTriplet> {
    @Override
    public JsonElement serialize(LocationTriplet triplet, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        try {
            object.add("x", new JsonPrimitive(triplet.getX()));
            object.add("y", new JsonPrimitive(triplet.getY()));
            object.add("z", new JsonPrimitive(triplet.getZ()));
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while serializing a Location.");
            return object;
        }
    }


    @Override
    public LocationTriplet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject object = jsonElement.getAsJsonObject();
        try {

            return new LocationTriplet(
                    object.get("x").getAsInt(),
                    object.get("y").getAsInt(),
                    object.get("z").getAsInt());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error encountered while" +
                    " deserializing a Blockposition.");
            return null;
        }


    }
}
