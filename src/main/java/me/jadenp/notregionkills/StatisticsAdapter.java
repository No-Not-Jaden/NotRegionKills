package me.jadenp.notregionkills;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsAdapter extends TypeAdapter<Map<UUID, RegionStat>> {
    @Override
    public void write(JsonWriter jsonWriter, Map<UUID, RegionStat> map) throws IOException {
        if (map == null || map.isEmpty()) {
            jsonWriter.nullValue();
            jsonWriter.close();
            return;
        }
        jsonWriter.beginArray();
        for (Map.Entry<UUID, RegionStat> entry : map.entrySet()) {
            jsonWriter.beginObject();
            jsonWriter.name("uuid").value(entry.getKey().toString());
            RegionStat regionStat = entry.getValue();
            jsonWriter.beginArray();
            for (Map.Entry<String, PlayerStat> regionEntry : regionStat.getRegionKills().entrySet()) {
                jsonWriter.beginObject();
                jsonWriter.name("!REGION_NAME!").value(regionEntry.getKey());
                PlayerStat playerStat = regionEntry.getValue();
                for (Map.Entry<String, Long> playerEntry : playerStat.getEntityKills().entrySet()) {
                    jsonWriter.name(playerEntry.getKey()).value(playerEntry.getValue());
                }
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.close();
    }

    @Override
    public Map<UUID, RegionStat> read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        Map<UUID, RegionStat> map = new HashMap<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            Map<String, PlayerStat> regionStat = new HashMap<>();
            UUID uuid = new UUID(0,0);
            jsonReader.beginObject();
            if ("uuid".equals(jsonReader.nextString()))
                uuid = UUID.fromString(jsonReader.nextString());
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Map<String, Long> playerStat = new HashMap<>();
                jsonReader.beginObject();
                String region = "?UNKNOWN?";
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (name.equals("!REGION_NAME!")) {
                        region = jsonReader.nextString();
                    } else {
                        playerStat.put(name, jsonReader.nextLong());
                    }
                }
                jsonReader.endObject();
                regionStat.put(region, new PlayerStat(playerStat));
            }
            jsonReader.endArray();
            jsonReader.endObject();
            map.put(uuid, new RegionStat(regionStat));
        }
        jsonReader.endArray();
        jsonReader.close();
        return map;
    }
}
