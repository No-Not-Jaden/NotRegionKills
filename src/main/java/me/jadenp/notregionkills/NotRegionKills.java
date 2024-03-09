package me.jadenp.notregionkills;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

/**
 * Track living entity kills per player
 * add mythicmobs as a soft depend
 * add a region flag for stat tracking
 */
public final class NotRegionKills extends JavaPlugin {
    private static NotRegionKills instance;
    public static boolean mythicMobsEnabled = false;
    public static StateFlag trackKills;
    private static boolean failedStartup = false;

    @Override
    public void onLoad() {

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "track-kills", defaulting to true
            StateFlag flag = new StateFlag("track-kills", true);
            registry.register(flag);
            trackKills = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("track-kills");
            if (existing instanceof StateFlag) {
                trackKills = (StateFlag) existing;
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
                Bukkit.getLogger().warning("[NotRegionKills] Another plugin is using the same WorldGuard flag, but with a different data type!");
                failedStartup = true;
            }
        }
    }

    public static NotRegionKills getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        // check if startup was successful
        if (failedStartup) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        instance = this; // for reference in other classes
        try {
            StatManager.loadSavedKills(); // load saved stats
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // load mythic mobs if enabled
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            MythicMobsClass.loadMobTypes();
            mythicMobsEnabled = true;
        }

        // register placeholder expansion
        new RegionKillsExpansion().register();
        // register events
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        // auto-save
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    StatManager.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimer(this, 15 * 60 * 20 + 34, 15 * 60 * 20); // 15 min
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            StatManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
