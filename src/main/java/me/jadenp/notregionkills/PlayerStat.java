package me.jadenp.notregionkills;

import java.util.HashMap;
import java.util.Map;

public class PlayerStat {
    private final Map<String, Long> entityKills;
    public PlayerStat() {
        entityKills = new HashMap<>();
    }
    public PlayerStat(Map<String, Long> entityKills) {
        this.entityKills = entityKills;
    }

    public Map<String, Long> getEntityKills() {
        return entityKills;
    }
    public void killEntity(String entityType) {
        if (entityKills.containsKey(entityType))
            entityKills.replace(entityType, entityKills.get(entityType) + 1);
        else
            entityKills.put(entityType, 1L);
    }
}
