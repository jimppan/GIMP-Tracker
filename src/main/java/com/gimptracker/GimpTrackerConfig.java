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
            description = "Should we send data to the gimp tracker site or not"
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
}
