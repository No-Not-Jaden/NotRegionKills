package me.jadenp.notregionkills;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Track living entity kills per player
 * add mythicmobs as a soft depend
 * add a region flag for stat tracking
 */
public final class NotRegionKills extends JavaPlugin {
    private static NotRegionKills instance;
    public static boolean mythicMobsEnabled = false;

    public static NotRegionKills getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            MythicMobsClass.loadMobTypes();
            mythicMobsEnabled = true;
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
