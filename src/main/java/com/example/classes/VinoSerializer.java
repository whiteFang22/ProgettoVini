package com.example.classes;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.*;

public class VinoSerializer implements JsonSerializer<Vino> {
    @Override
    public JsonElement serialize(Vino vino, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", vino.getId());
        jsonObject.addProperty("nome", vino.getNome());
        jsonObject.addProperty("produttore", vino.getProduttore());
        jsonObject.addProperty("provenienza", vino.getProvenienza());
        jsonObject.addProperty("anno", vino.getAnno());
        jsonObject.addProperty("noteTecniche", vino.getNoteTecniche());
        jsonObject.addProperty("prezzo", vino.getPrezzo());
        jsonObject.addProperty("numeroVendite", vino.getNumeroVendite());
        jsonObject.addProperty("disponibilita", vino.getDisponibilita());
        jsonObject.add("vitigni", context.serialize(vino.getVitigni(), List.class));

        /*
        // Serialize the list of vitigni
        JsonArray vitigniArray = new JsonArray();
        for (String vitigno : vino.getVitigni()) {
            vitigniArray.add(new JsonPrimitive(vitigno));
        }
        jsonObject.add("vitigni", vitigniArray);
        */

        return jsonObject;
    }
}
