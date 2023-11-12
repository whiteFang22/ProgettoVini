package com.example.classes;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VinoDeserializer implements JsonDeserializer<Vino> {
    @Override
    public Vino deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.getAsJsonPrimitive("id").getAsInt();
        String nome = jsonObject.getAsJsonPrimitive("nome").getAsString();
        String produttore = jsonObject.getAsJsonPrimitive("produttore").getAsString();
        String provenienza = jsonObject.getAsJsonPrimitive("provenienza").getAsString();
        int anno = jsonObject.getAsJsonPrimitive("anno").getAsInt();
        String noteTecniche = jsonObject.getAsJsonPrimitive("noteTecniche").getAsString();
        float prezzo = jsonObject.getAsJsonPrimitive("prezzo").getAsFloat();
        int numeroVendite = jsonObject.getAsJsonPrimitive("numeroVendite").getAsInt();
        int disponibilita = jsonObject.getAsJsonPrimitive("disponibilita").getAsInt();

        // Deserialize the list of vitigni
        List<String> vitigni = new ArrayList<>();
        JsonArray vitigniArray = jsonObject.getAsJsonArray("vitigni");
        for (JsonElement vitignoElement : vitigniArray) {
            vitigni.add(vitignoElement.getAsString());
        }

        return new Vino(id,nome,produttore,provenienza,anno,noteTecniche,vitigni,prezzo,numeroVendite,disponibilita);
    }
}
