package com.gimptracker;

import net.runelite.api.Item;

public class DataItem
{
    public int id = DataBuilder.DataInventoryID.NOT_SET;
    public int quantity = 0;

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Item)
        {
            Item other = (Item)o;
            return other != null && id == other.getId() && quantity == other.getQuantity();
        }

        DataItem other = (DataItem)o;
        return other != null && id == other.id && quantity == other.quantity;
    }
}
