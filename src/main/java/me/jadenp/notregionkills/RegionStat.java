package me.jadenp.notregionkills;

import java.util.HashMap;
import java.util.Map;

public class RegionStat {
    /**
     * Region Name, Player kill stats
     */
    private final Map<RegionType, PlayerStat> regionKills;
    public RegionStat(){
        regionKills = new HashMap<>();
    }
    public RegionStat(Map<RegionType, PlayerStat> regionKills) {
        this.regionKills = regionKills;
    }

    public void killEntity(RegionType region, String entityType) {
        if (regionKills.containsKey(region)) {
            regionKills.get(region).killEntity(entityType);
        } else {
            PlayerStat stat = new PlayerStat();
            stat.killEntity(entityType);
            regionKills.put(region, stat);
        }
    }

    public Map<RegionType, PlayerStat> getRegionKills() {
        return regionKills;
    }

    @Override
    public String toString() {
        return "RegionStat{" +
                "regionKills=" + regionKills +
                '}';
    }
}
