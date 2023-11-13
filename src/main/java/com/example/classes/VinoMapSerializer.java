package com.example.classes;

import com.google.gson.*;
import java.util.Map;
import java.lang.reflect.Type;

 public class VinoMapSerializer implements JsonSerializer<Map<Vino, Integer>> {
    @Override
    public JsonElement serialize(Map<Vino, Integer> vinoMap, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<Vino, Integer> entry : vinoMap.entrySet()) {
            String key = context.serialize(entry.getKey(), Vino.class).getAsString();
            int value = entry.getValue();
            jsonObject.addProperty(key, value);
        }

        return jsonObject;
    }
}
