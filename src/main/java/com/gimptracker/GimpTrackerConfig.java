package com.gimptracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import javax.swing.*;

@ConfigGroup("gimptracker")
public interface GimpTrackerConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "url",
            name = "URL",
            description = "URL to backend"
    )
    default String url()
    {
        return "localhost";
    }

    @ConfigItem(
            position = 2,
            keyName = "password",
            name = "Password",
            description = "Password to backend"
    )
    default String password()
    {
        return "123";
    }

    @ConfigItem
    (
            position = 3,
            keyName = "sendData",
            name = "Send data",
            description = "Should we send data to the GIMP Tracker website or not"
    )
    default boolean sendData() { return true; }

    @ConfigItem
    (
            position = 4,
            keyName = "connectOnLogin",
            name = "Connect on login",
            description = "Should automatically connect to the tracker backend upon login"
    )
    default boolean connectOnLogin() { return true; }

    @ConfigItem
    (
            position = 5,
            keyName = "sendInventory",
            name = "Send inventory data",
            description = "Should we update our inventory on the GIMP Tracker website or not"
    )
    default boolean sendInventory() { return true; }

    @ConfigItem
    (
            position = 6,
            keyName = "sendEquipment",
            name = "Send equipment data",
            description = "Should we update our equipment on the GIMP Tracker website or not"
    )
    default boolean sendEquipment() { return true; }

    @ConfigItem
    (
            position = 7,
            keyName = "sendSkill",
            name = "Send skill data",
            description = "Should we update our skills/experience on the GIMP Tracker website or not"
    )
    default boolean sendSkill() { return true; }
}
