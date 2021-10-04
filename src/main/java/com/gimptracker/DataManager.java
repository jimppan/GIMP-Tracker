package com.gimptracker;

import com.google.gson.JsonObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.util.Arrays;

@Getter
public class DataManager {

    public DataBuilder currentPacket;
    public DataBuilder previousPacket;

    public DataManager()
    {
        currentPacket = new DataBuilder();
        previousPacket = new DataBuilder();
    }

    // compare 2 packets and remove unchanged values from previous packet
    public DataBuilder finalizePacket()
    {
        DataBuilder builder = new DataBuilder();
        if(!currentPacket.wasChanged)
            return currentPacket;

        currentPacket.wasChanged = false;
        builder.wasChanged = false;

        // name was changed since last packet, keep it
        if(currentPacket.name != previousPacket.name)
            builder.setName(currentPacket.name);

        if(currentPacket.world != previousPacket.world)
            builder.setWorld(currentPacket.world);

        if(currentPacket.pos != null && !currentPacket.pos.equals(previousPacket.pos))
            builder.setPosition(currentPacket.pos.getX(), currentPacket.pos.getY(), currentPacket.pos.getPlane());

        if(currentPacket.inventory != null && !Arrays.equals(currentPacket.inventory, previousPacket.inventory))
        {
            if(previousPacket.inventory == null)
                builder.setInventory(currentPacket.inventory);
            else
            {
                DataItem[] items = new DataItem[DataBuilder.INVENTORY_SIZE];
                for(int i = 0; i < DataBuilder.INVENTORY_SIZE; i++)
                {
                    items[i] = new DataItem();
                    if(currentPacket.inventory[i].id != previousPacket.inventory[i].id ||
                        currentPacket.inventory[i].quantity != previousPacket.inventory[i].quantity)
                    {
                        items[i].id = currentPacket.inventory[i].id;
                        items[i].quantity = currentPacket.inventory[i].quantity;
                    }
                }
                builder.setInventory(items);
            }
        }

        if(currentPacket.skills != null && !Arrays.equals(currentPacket.skills, previousPacket.skills))
        {
            if(previousPacket.skills == null)
                builder.setSkills(currentPacket.skills);
            else
            {
                DataSkill[] skills = new DataSkill[Skill.values().length - 1];
                for(int i = 0; i < Skill.values().length - 1; i++)
                {
                    skills[i] = new DataSkill(-1, 0);
                    if(currentPacket.skills[i].experience != previousPacket.skills[i].experience)
                    {
                        skills[i].id = currentPacket.skills[i].id;
                        skills[i].experience = currentPacket.skills[i].experience;
                    }
                }
                builder.setSkills(skills);
            }
        }

        if(currentPacket.equipment != null && !Arrays.equals(currentPacket.equipment, previousPacket.equipment))
        {
            if(previousPacket.equipment == null)
                builder.setEquipment(currentPacket.equipment);
            else
            {
                DataItem[] items = new DataItem[DataBuilder.EQUIPMENT_SIZE];
                for(int i = 0; i < DataBuilder.EQUIPMENT_SIZE; i++)
                {
                    items[i] = new DataItem();
                    if(currentPacket.equipment[i].id != previousPacket.equipment[i].id ||
                            currentPacket.equipment[i].quantity != previousPacket.equipment[i].quantity)
                    {
                        items[i].id = currentPacket.equipment[i].id;
                        items[i].quantity = currentPacket.equipment[i].quantity;
                    }
                }
                builder.setEquipment(items);
            }
        }

        if(currentPacket.loot != null && !currentPacket.loot.equals(previousPacket.loot))
        {
            builder.setLoot(currentPacket.loot);
            currentPacket.loot = null;
        }

        if(builder.wasChanged)
            previousPacket = new DataBuilder(currentPacket);

        return builder;
    }

    public void resetPackets()
    {
        currentPacket = new DataBuilder();
        previousPacket = new DataBuilder();
    }
}
