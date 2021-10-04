package com.gimptracker;

import net.runelite.http.api.loottracker.LootRecordType;

public class DataLoot {
    public int metadata;
    public int combatLevel;
    public String name;
    public LootRecordType type;
    public String timestamp;
    public DataItem items[];

    public DataLoot(){

    }

    public DataLoot(int metadata, int combatLevel, String name, LootRecordType type, String timestamp)
    {
        this.metadata = metadata;
        this.combatLevel = combatLevel;
        this.name = name;
        this.type = type;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o)
    {
        DataLoot other = (DataLoot) o;
        return other != null && metadata == other.metadata &&
                combatLevel == other.combatLevel && name.equals(other.name) &&
                type == other.type && timestamp.equals(other.timestamp);
    }
}
