package com.gimptracker;

import net.runelite.api.Item;

public class DataSkill {
    public int id = 0;
    public int experience = 0;

    DataSkill(int id, int experience)
    {
        this.id = id;
        this.experience = experience;
    }

    @Override
    public boolean equals(Object o)
    {
        DataSkill other = (DataSkill)o;
        return other != null && id == other.id && experience == other.experience;
    }
}
