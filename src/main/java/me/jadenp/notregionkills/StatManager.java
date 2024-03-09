package me.jadenp.notregionkills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StatManager {
    public static Map<UUID, RegionStat> allPlayerStats = new HashMap<>();

    /**
     * Loads saved kills from stats.json
     * @throws IOException if there was an error creating or reading from the file
     */
    public static void loadSavedKills() throws IOException {
        File save = getSaveFile();
        // check if the file exists/create a new file
        if (save.createNewFile()) {
            Bukkit.getLogger().info("[NotRegionKills] Created new save file.");
            return;
        }
        // create a Gson object
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(StatisticsAdapter.class, new StatisticsAdapter());
        Gson gson = builder.create();

        Type mapType = new TypeToken<Map<UUID, RegionStat>>(){}.getType();
        JsonReader reader = new JsonReader(new FileReader(save));
        allPlayerStats = gson.fromJson(reader , mapType);
        reader.close();

    }

    /**
     * Saves stats.json with the current mob kill stats
     * @throws IOException if there was an error creating or writing to the file
     */
    public static void save() throws IOException {
        File save = getSaveFile();
        // create new file if it's missing
        if (save.createNewFile()) {
            Bukkit.getLogger().info("[NotRegionKills] Created new save file.");
        }
        // create a Gson object
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(StatisticsAdapter.class, new StatisticsAdapter());
        Gson gson = builder.create();

        Type mapType = new TypeToken<Map<UUID, RegionStat>>(){}.getType();
        JsonWriter writer = new JsonWriter(new FileWriter(save));
        gson.toJson(allPlayerStats, mapType, writer);
        writer.close();

    }

    public static boolean isMob(String type) {
        type = type.toUpperCase();
        if (NotRegionKills.mythicMobsEnabled && MythicMobsClass.isMythicMob(type))
            return true;
        try {
            if (EntityType.valueOf(type).isAlive())
                return true;
        } catch (IllegalArgumentException ignored) {
            // unknown entity type;
        }
        return false;
    }

    /**
     * Add a player's kill to the statistics
     * @param killer player who killed the mob
     * @param regionName name of the region the mob was killed in
     * @param mobType the type of mob
     */
    public static void killMob(Player killer, String regionName, String mobType) {
        mobType = mobType.toUpperCase();
        if (allPlayerStats.containsKey(killer.getUniqueId())) {
            allPlayerStats.get(killer.getUniqueId()).killEntity(regionName, mobType);
        } else {
            RegionStat stat = new RegionStat();
            stat.killEntity(regionName, mobType);
            allPlayerStats.put(killer.getUniqueId(), stat);
        }
    }

    /**
     * Get the stats.json file from the data folder
     * @return the stats.json save file
     */
    private static File getSaveFile(){
        return new File(NotRegionKills.getInstance().getDataFolder() + File.separator + "stats.json");
    }

    /**
     * Get the total mob kills a player has
     * @param uuid UUID of the player
     * @return The total recorded mob kills
     */
    public static long getTotalMobKills(UUID uuid) {
        // check if uuid exists in stats
        if (!allPlayerStats.containsKey(uuid))
            return 0;
        // get region stats
        RegionStat regionStat = allPlayerStats.get(uuid);
        // go through every region stat and add up all the recorded kills
        AtomicLong total = new AtomicLong();
        regionStat.getRegionKills().forEach((key, value) -> value.getEntityKills().values().forEach(total::addAndGet));
        return total.get();
    }

    /**
     * Get the mob kills of a specific mob type in a specific region for a player
     * @param uuid UUID of the player
     * @param mobType Type of mob
     * @param regionName Region name (case-sensitive)
     * @return the number of killed mobs of mobType in the region for the player
     */
    public static long getRegionMobKills(UUID uuid, String mobType, String regionName) {
        mobType = mobType.toUpperCase();
        // check if uuid exists in stats
        if (!allPlayerStats.containsKey(uuid))
            return 0;
        // get region stats
        RegionStat regionStat = allPlayerStats.get(uuid);
        // check if the specified region has been recorded for this player
        if (!regionStat.getRegionKills().containsKey(regionName))
            return 0;
        // get player stats
        PlayerStat playerStat = regionStat.getRegionKills().get(regionName);
        // check if mob has been recorded for this player
        if (!playerStat.getEntityKills().containsKey(mobType))
            return 0;
        return playerStat.getEntityKills().get(mobType);
    }

    /**
     * Get the mob kills in a specific region for a player
     * @param uuid UUID of the player
     * @param regionName name of the region (case-sensitive)
     * @return the amount of mobs the player has killed in this region
     */
    public static long getRegionKills(UUID uuid, String regionName) {
        // check if uuid exists in stats
        if (!allPlayerStats.containsKey(uuid))
            return 0;
        // get region stats
        RegionStat regionStat = allPlayerStats.get(uuid);
        // check if the specified region has been recorded for this player
        if (!regionStat.getRegionKills().containsKey(regionName))
            return 0;
        // get player stats
        PlayerStat playerStat = regionStat.getRegionKills().get(regionName);
        AtomicLong total = new AtomicLong();
        // count up all the stored values in playerStat
        playerStat.getEntityKills().values().forEach(total::addAndGet);
        return total.get();
    }

    /**
     * Get the mob kills of a certain type for a player
     * @param uuid UUID of the player
     * @param mobType type of mob
     * @return the amount of mobs the player has killed that are mobType
     */
    public static long getMobKills(UUID uuid, String mobType) {
        mobType = mobType.toUpperCase();
        // check if uuid exists in stats
        if (!allPlayerStats.containsKey(uuid))
            return 0;
        // get region stats
        RegionStat regionStat = allPlayerStats.get(uuid);
        // iterate through all the regions in regionStat and get the sum of the matching mobTypes
        return regionStat.getRegionKills().values().stream().filter(playerStat -> playerStat.getEntityKills().containsKey(mobType)).mapToLong(playerStat -> playerStat.getEntityKills().get(mobType)).sum();
    }

}
