package me.jadenp.notregionkills;

import java.util.HashMap;
import java.util.Map;

public class RegionStat {
    private final Map<String, PlayerStat> regionKills;
    public RegionStat(){
        regionKills = new HashMap<>();
    }
    public RegionStat(Map<String, PlayerStat> regionKills) {
        this.regionKills = regionKills;
    }

    public void killEntity(String regionName, String entityType) {
        if (regionKills.containsKey(regionName)) {
            regionKills.get(regionName).killEntity(entityType);
        } else {
            PlayerStat stat = new PlayerStat();
            stat.killEntity(entityType);
            regionKills.put(regionName, stat);
        }
    }

    public Map<String, PlayerStat> getRegionKills() {
        return regionKills;
    }
}
