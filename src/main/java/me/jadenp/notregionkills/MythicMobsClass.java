package me.jadenp.notregionkills;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;

import java.util.ArrayList;
import java.util.List;

public class MythicMobsClass {
    private static List<String> mobTypes = new ArrayList<>();
    public static boolean isMythicMob(String mobType) {
        mobType = mobType.toUpperCase();
        return mobTypes.contains(mobType);
    }

    public static void loadMobTypes() {
        mobTypes.clear();
        for (MythicMob mob : MythicBukkit.inst().getMobManager().getMobTypes()) {
            mobTypes.add(mob.getEntityTypeString().toUpperCase());
        }
    }
}
