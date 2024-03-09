package me.jadenp.notregionkills;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.NotNull;

/**
 * (player) can be the player name or uuid
 * %notregionkills_total% - total mob kills
 * %notregionkills_mob_(mob type)% - mob kills of a certain type
 * %notregionkills_region_(region name)% - mob kills in a region
 * %notregionkills_(mob type)_(region name)% - mob kills of a certain type in a region
 */
public class RegionKillsExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "notregionkills";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Not_Jaden";
    }

    @Override
    public @NotNull String getVersion() {
        return NotRegionKills.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("total")) {
            // %notregionkills_total% - total mob kills
            return StatManager.getTotalMobKills(player.getUniqueId()) + "";
        }
        if (params.startsWith("mob_")) {
            // %notregionkills_mob_(mob type)%
            String type = params.substring(4);
            if (StatManager.isMob(type)) {
                return StatManager.getMobKills(player.getUniqueId(), type) + "";
            }
            return "";
        } else if (params.startsWith("region_")) {

        } else {

        }

        return null; // Placeholder is unknown by the Expansion
    }
}
