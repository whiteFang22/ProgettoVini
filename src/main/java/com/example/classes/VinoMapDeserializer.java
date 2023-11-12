package com.example.classes;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VinoMapDeserializer implements JsonDeserializer<Map<Vino, Integer>> {
    @Override
    public Map<Vino, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<Vino, Integer> result = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonObject vinoClass = JsonParser.parseString(entry.getKey()).getAsJsonObject();
            Vino vino = context.deserialize(vinoClass,Vino.class);
            int quantity = entry.getValue().getAsInt();
            result.put(vino, quantity);
        }

        return result;
    }
}