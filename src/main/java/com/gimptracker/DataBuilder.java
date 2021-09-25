package com.gimptracker;

import com.google.gson.JsonObject;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;

public class DataBuilder {

    public class DataInventoryID
    {
        public final static int NOT_SET = -2;
        public final static int EMPTY = -1;
    }

    public class DataFlags
    {
        public final static int UNDEFINED = 0; // if set to undefined goal, the packet will send no matter what the upcomming tick
        public final static int NAME = 1;
        public final static int WORLD = 2;
        public final static int POSITION = 4;
        public final static int INVENTORY = 8;
        public final static int SKILLS = 16;
        public final static int EQUIPMENT = 32;
        public final static int HEALTH = 64;
        public final static int PRAYER = 128;
        public final static int ENERGY = 256;

        //public final static int ALL = NAME | POSITION | INVENTORY | SKILLS | HEALTH | PRAYER | ENERGY;
        public final static int ALL = NAME | WORLD | POSITION | INVENTORY | SKILLS | EQUIPMENT;
    }

    public final static int INVENTORY_SIZE = 28;
    public final static int EQUIPMENT_SIZE = 14; // 11 & 6 & 8 is missing for some reason, idk :D
    public final static int INVALID_WORLD  = -1;

    public String name = null;
    public int world = INVALID_WORLD;
    public WorldPoint pos = null;
    public DataItem[] inventory = null;
    public DataSkill[] skills = null;
    public DataItem[] equipment = null;

    public JsonObject data = null;

    public int goalProgress = DataFlags.UNDEFINED;
    public int goal = DataFlags.UNDEFINED;

    // indicates if data was modified
    public boolean wasChanged = false;

    public DataBuilder()
    {

    }

    public DataBuilder(DataBuilder other)
    {
        this.name = other.name;
        this.world = other.world;
        this.pos = other.pos;

        if(other.inventory != null)
        {
            this.inventory = new DataItem[INVENTORY_SIZE];
            System.arraycopy(other.inventory, 0, this.inventory, 0, other.inventory.length);
        }

        if(other.skills != null)
        {
            this.skills = new DataSkill[Skill.values().length - 1];
            System.arraycopy(other.skills, 0, this.skills, 0, other.skills.length);
        }

        if(other.equipment != null)
        {
            this.equipment = new DataItem[EQUIPMENT_SIZE];
            System.arraycopy(other.equipment, 0, this.equipment, 0, other.equipment.length);
        }

        if(other.data != null)
            this.data = other.data.deepCopy();

        this.wasChanged = other.wasChanged;
    }

    public void setName(String name)
    {
        setGoalProgressFlag(DataFlags.NAME);
        wasChanged = true;
        this.name = name;
    }

    public void setWorld(int world)
    {
        setGoalProgressFlag(DataFlags.WORLD);
        wasChanged = true;
        this.world = world;
    }

    public void setPosition(int x, int y, int plane)
    {
        setGoalProgressFlag(DataFlags.POSITION);
        wasChanged = true;
        this.pos = new WorldPoint(x, y, plane);
    }

    public void setInventory(Item[] inventory)
    {
        setGoalProgressFlag(DataFlags.INVENTORY);
        if(inventory == null)
            return;

        wasChanged = true;
        this.inventory = new DataItem[INVENTORY_SIZE];

        for(int i = 0; i < this.inventory.length; i++)
        {
            Item item = null;
            if(i < inventory.length)
                item = inventory[i];

            this.inventory[i] = new DataItem();
            if(item == null)
            {
                this.inventory[i].id = DataInventoryID.EMPTY;
                this.inventory[i].quantity = 0;
            }
            else
            {
                this.inventory[i].id = item.getId();
                this.inventory[i].quantity = item.getQuantity();
            }
        }
    }

    public void setInventory(DataItem[] inventory)
    {
        setGoalProgressFlag(DataFlags.INVENTORY);
        if(inventory == null)
            return;

        wasChanged = true;
        this.inventory = new DataItem[INVENTORY_SIZE];

        for(int i = 0; i < this.inventory.length; i++)
        {
            DataItem item = null;
            if(i < inventory.length)
                item = inventory[i];

            this.inventory[i] = new DataItem();
            if(item == null)
            {
                this.inventory[i].id = DataInventoryID.NOT_SET;
                this.inventory[i].quantity = 0;
            }
            else
            {
                this.inventory[i].id = item.id;
                this.inventory[i].quantity = item.quantity;
            }
        }
    }

    public void setEquipment(Item[] equipment)
    {
        setGoalProgressFlag(DataFlags.EQUIPMENT);
        if(equipment == null)
            return;

        wasChanged = true;
        this.equipment = new DataItem[EQUIPMENT_SIZE];

        for(int i = 0; i < this.equipment.length; i++)
        {
            Item item = null;
            if(i < equipment.length)
                item = equipment[i];

            this.equipment[i] = new DataItem();
            if(item == null)
            {
                this.equipment[i].id = DataInventoryID.EMPTY;
                this.equipment[i].quantity = 0;
            }
            else
            {
                this.equipment[i].id = item.getId();
                this.equipment[i].quantity = item.getQuantity();
            }
        }
    }

    public void setEquipment(DataItem[] equipment)
    {
        setGoalProgressFlag(DataFlags.EQUIPMENT);
        if(equipment == null)
            return;

        wasChanged = true;
        this.equipment = new DataItem[EQUIPMENT_SIZE];

        for(int i = 0; i < this.equipment.length; i++)
        {
            DataItem item = null;
            if(i < equipment.length)
                item = equipment[i];

            this.equipment[i] = new DataItem();
            if(item == null)
            {
                this.equipment[i].id = DataInventoryID.NOT_SET;
                this.equipment[i].quantity = 0;
            }
            else
            {
                this.equipment[i].id = item.id;
                this.equipment[i].quantity = item.quantity;
            }
        }
    }

    public void setSkills(DataSkill[] skills)
    {
        setGoalProgressFlag(DataFlags.SKILLS);
        if(skills == null)
            return;

        wasChanged = true;
        this.skills = skills;
    }

    public JsonObject build()
    {
        data = new JsonObject();

        if(name != null)
            data.addProperty("name", name);

        if(world != INVALID_WORLD)
            data.addProperty("world", world);

        if(pos != null)
        {
            JsonObject jsonPos = new JsonObject();
            jsonPos.addProperty("x", this.pos.getX());
            jsonPos.addProperty("y", this.pos.getY());
            jsonPos.addProperty("plane", this.pos.getPlane());

            data.add("pos", jsonPos);
        }

        JsonObject jsonInventory = new JsonObject();

        if(inventory != null)
        {
            for(int i = 0; i < INVENTORY_SIZE; i++)
            {
                if(this.inventory[i].id == DataInventoryID.NOT_SET)
                    continue;

                JsonObject jsonInvSlotData = new JsonObject();
                jsonInvSlotData.addProperty("id", this.inventory[i].id);
                jsonInvSlotData.addProperty("quantity", this.inventory[i].quantity);

                jsonInventory.add(String.valueOf(i), jsonInvSlotData);
            }

            data.add("inventory", jsonInventory);
        }

        JsonObject jsonSkills = new JsonObject();
        if(skills != null)
        {
            for(int i = 0; i < Skill.values().length - 1; i++)
            {
                if(this.skills[i].id == -1)
                    continue;

                JsonObject jsonSkillData = new JsonObject();
                jsonSkillData.addProperty("id", this.skills[i].id);
                jsonSkillData.addProperty("experience", this.skills[i].experience);

                jsonSkills.add(String.valueOf(i), jsonSkillData);
            }

            data.add("skills", jsonSkills);
        }

        JsonObject jsonEquipment = new JsonObject();
        if(equipment != null)
        {
            for(int i = 0; i < EQUIPMENT_SIZE; i++)
            {
                if(this.equipment[i].id == DataInventoryID.NOT_SET)
                    continue;

                JsonObject jsonEqpSlotData = new JsonObject();
                jsonEqpSlotData.addProperty("id", this.equipment[i].id);
                jsonEqpSlotData.addProperty("quantity", this.equipment[i].quantity);

                jsonEquipment.add(String.valueOf(i), jsonEqpSlotData);
            }

            data.add("equipment", jsonEquipment);
        }
        wasChanged = false;
        return data;
    }

    public JsonObject getBuiltData()
    {
        return data;
    }

    public void setGoalProgressFlag(int dataFlag)
    {
        goalProgress |= dataFlag;
    }

    public void setGoalFlags(int dataFlag)
    {
        goal = dataFlag;
    }

    public boolean hasReachedGoal()
    {
        return (goalProgress & goal) == goal;
    }

    public void resetGoal()
    {
        goal = DataFlags.UNDEFINED;
    }

    @Override
    public boolean equals(Object o)
    {
        DataBuilder other = (DataBuilder) o;
        return  name.equals(other.name) &&
                world == other.world &&
                pos.getX() == other.pos.getX() &&
                pos.getY() == other.pos.getY() &&
                pos.getPlane() == other.pos.getPlane() &&
                Arrays.equals(inventory, other.inventory) &&
                Arrays.equals(skills, other.skills) &&
                Arrays.equals(equipment, other.equipment);
    }
}
