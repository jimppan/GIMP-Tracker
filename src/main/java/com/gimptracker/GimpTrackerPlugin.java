//package net.runelite.client.plugins.gimptracker;
package com.gimptracker;

import com.google.gson.*;
import com.google.inject.Provides;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.NonNull;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.game.WorldService;

import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.PlayerLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.loottracker.LootRecordType;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Sends data to a backend which then relays it further to frontend
 * which displays live data of the player
 */
@PluginDescriptor(
        name = "GIMP Tracker",
        description = "Tracks players movement/inventory/skills etc..",
        tags = {"tracker", "gimp"},
        enabledByDefault = true
)

@Getter
public class GimpTrackerPlugin extends Plugin implements ActionListener, ConnectionManager.ConnectionListener
{
    private static final String CONFIG_GROUP = "gimptracker";

    private WorldPoint previousTile = new WorldPoint(0, 0, 0);

    private int tickCountSinceLogged = 0;

    // I need to be able to check tripple gamestates lol
    // so we can check if this user actually logged in or if it was just a chunk change
    public GameState previousGameState = GameState.UNKNOWN;
    public GameState previousGameState2 = GameState.UNKNOWN;

    public JButton connectButton;
    public JLabel connectButtonLabel;
    public JButton debugButton;

    private NavigationButton navButton;

    private DataManager dataManager;
    private ConnectionManager connectionManager;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private GimpTrackerConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Provides
    private GimpTrackerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GimpTrackerConfig.class);
    }

    @Override
    public void onConnectionStatusChanged(ConnectionManager.ConnectionStatus status) {
        updateConnectButton();

        switch(status)
        {
            case DISCONNECTED:
                // we need to reset packets once a client disconnects from the backend
                // so he sends full information again once he connets
                dataManager.resetPackets();
                break;
        }
    }

    @Override
    public void onConnectionErrorChanged(ConnectionManager.ConnectionError status) {
        updateConnectButton();

        switch(status)
        {
            case AUTHORIZED:
                queueFullPacket();
                break;
        }
    }

    public void queueFullPacket()
    {
        dataManager.getCurrentPacket().setGoalFlags(DataBuilder.DataFlags.ALL);

        WorldPoint point = client.getLocalPlayer().getWorldLocation();
        dataManager.getCurrentPacket().setName(client.getLocalPlayer().getName()); // NAME
        dataManager.getCurrentPacket().setWorld(client.getWorld());
        dataManager.getCurrentPacket().setPosition(point.getX(), point.getY(), point.getPlane());  // POS
        queueInventoryData(); // INVENTORY
        queueSkillData(); // SKILLS
        queueEquipmentData(); // EQUIPMENT
    }

    // queues inventory data to next packet
    public void queueInventoryData()
    {
        clientThread.invokeLater(() ->
        {
            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            Item[] items = null;
            if(inventory != null)
                items = inventory.getItems();

            dataManager.getCurrentPacket().setInventory(items);
        });
    }

    // queues skill data to next packet
    public void queueSkillData()
    {
        clientThread.invokeLater(() ->
        {
            DataSkill[] skills = new DataSkill[Skill.values().length - 1];
            for(int i = 0; i < Skill.values().length - 1; i++)
            {
                int xp = client.getSkillExperience(Skill.values()[i]);
                skills[i] = new DataSkill(i, xp);
            }

            dataManager.getCurrentPacket().setSkills(skills);
        });
    }

    // queues equipment data to next packet
    public void queueEquipmentData()
    {
        clientThread.invokeLater(() ->
        {
            ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
            Item[] items = null;
            if(equipment != null)
                items = equipment.getItems();

            dataManager.getCurrentPacket().setEquipment(items);
        });
    }

    // connect indicates if it was the clients first packet (connect)
    public void updateClient(boolean connect)
    {
        if(!dataManager.getCurrentPacket().hasReachedGoal())
            return;

        dataManager.getCurrentPacket().resetGoal(); // set packet building goal to nothing, so send any packet from now on
        DataBuilder builder = dataManager.finalizePacket();
        if(builder.wasChanged)
        {
            String evt = connect?ConnectionManager.SocketEvent.CONNECT:ConnectionManager.SocketEvent.UPDATE;
            connectionManager.sendData(evt, builder.build());
        }
    }

    private void updateConnectButton()
    {
        switch(connectionManager.getConnectionStatus())
        {
            case CONNECTING:
                connectButton.setText("Connecting...");
                connectButton.setEnabled(false);
                break;

            case DISCONNECTING:
                connectButton.setText("Disconnecting...");
                connectButton.setEnabled(false);
                break;

            case CONNECTED:
                connectButton.setEnabled(true);
                connectButton.setText("Disconnect");
                break;

            case DISCONNECTED:
                connectButton.setEnabled(client.getGameState() == GameState.LOGGED_IN);
                connectButton.setText("Connect");
                break;
            default:
                break;
        }

        connectButtonLabel.setText(ConnectionManager.connectionErrorStrings[connectionManager.getConnectionError().ordinal()]);
        switch(connectionManager.connectionError)
        {
            case NONE:
                connectButtonLabel.setForeground(Color.WHITE);
                break;
            case AUTHORIZED:
                connectButtonLabel.setForeground(Color.GREEN);
                break;
            case UNAUTHORIZED:
            case TIMED_OUT:
            case BAD_URL:
                connectButtonLabel.setForeground(Color.RED);
                break;
        }
    }

    public void actionPerformed(ActionEvent arg0)
    {
        JButton btn = (JButton)arg0.getSource();
        //String buttonName = btn.getActionCommand();

        if(btn == connectButton)
        {
            // If client presses connect
            if(!connectionManager.isConnected())
            {
                connectionManager.connect();
            }
            else
            {
                connectionManager.disconnect();
            }
        }
        else if(btn == debugButton)
        {
            //client.getMap
            System.out.println(client.getWorld());
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if(event.getContainerId() == InventoryID.INVENTORY.getId())
        {
            queueInventoryData();
        }
        else if(event.getContainerId() == InventoryID.EQUIPMENT.getId())
        {
           queueEquipmentData();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        queueSkillData();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            dataManager.getCurrentPacket().setWorld(client.getWorld());
            // LOGGED_IN is called on chunk changes aswell so, need a double check here for previous state
            if(previousGameState == GameState.LOADING && previousGameState2 == GameState.LOGGING_IN) {
                tickCountSinceLogged = 0;
                updateConnectButton();
            }
        }
        else if(event.getGameState() == GameState.CONNECTION_LOST || event.getGameState() == GameState.LOGIN_SCREEN)
        {
            // when we lose connection or log out, disconnect socket
            connectionManager.disconnect();
        }

        previousGameState2 = previousGameState;
        previousGameState = event.getGameState();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event)
    {
       // System.out.println(event.getHitsplat().getAmount());
    }

    public void postLogInTick()
    {
        // when user logged in lets connect to socket
        tickCountSinceLogged = -1;

        if(config.connectOnLogin())
            connectionManager.connect();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!CONFIG_GROUP.equals(event.getGroup()))
            return;

        // if we change password, update options builder for the socket
        switch (event.getKey())
        {
            case "password":
                connectionManager.options = IO.Options.builder().
                        setQuery("system=runelite").
                        setReconnection(true).
                        setTimeout(5_000).
                        setAuth(Collections.singletonMap("token", config.password())).
                        build();
                break;
        }

    }

    @Override
    protected void startUp() throws Exception
    {
        connectionManager = new ConnectionManager(this);
        dataManager = new DataManager();

        connectionManager.init();
        connectionManager.addConnectionListener(this);

        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);

        //debugButton = new JButton("Debug");
        //debugButton.addActionListener(this);

        connectButtonLabel = new JLabel("");

        final GimpTrackerPanel panel = injector.getInstance(GimpTrackerPanel.class);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/gimptracker.png");

        navButton = NavigationButton.builder()
                .tooltip("GIMP Tracker")
                .icon(icon)
                .priority(1)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() throws Exception
    {
        connectionManager.disconnect();
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) throws Exception{

        if(config.sendData() && connectionManager.isConnected() && connectionManager.isAuthorized()) // dataManager.shouldSendPacket() &&
        {
            this.updateClient(connectionManager.isFirstPacket());
        }

        if(tickCountSinceLogged >= 1)
            postLogInTick();

        if(tickCountSinceLogged >= 0)
            tickCountSinceLogged++;

        WorldPoint currentTile = client.getLocalPlayer().getWorldLocation();

        // tile same as last time, no need to update
        if( currentTile.getX() == previousTile.getX() &&
            currentTile.getY() == previousTile.getY() &&
            currentTile.getPlane() == previousTile.getPlane())
            return;

        dataManager.getCurrentPacket().setPosition(currentTile.getX(), currentTile.getY(), currentTile.getPlane());
        previousTile = currentTile;
    }

    private static Collection<DataItem> toGameItems(Collection<ItemStack> items)
    {
        return items.stream()
                .map(item -> new DataItem(item.getId(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    void addLoot(@NonNull String name, int combatLevel, LootRecordType type, Object metadata, Collection<ItemStack> items)
    {
        Collection<DataItem> drop_items = toGameItems(items);
        DataItem item[] = drop_items.toArray(new DataItem[drop_items.size()]);
        DataLoot loot = new DataLoot((int)metadata, combatLevel, name, type, ""+Instant.now().toEpochMilli());
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "name: " + name + "; combatLevel: " + combatLevel + ";", null);
        for(int i = 0; i < item.length; i++)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "itemID: " + item[i].id + "; quantity: " + item[i].quantity + ";", null);
        }
        loot.items = item;
        dataManager.getCurrentPacket().addLoot(loot);
    }

    @Subscribe
    public void onNpcLootReceived(final NpcLootReceived npcLootReceived)
    {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "onNpcLootDrop", null);
        final NPC npc = npcLootReceived.getNpc();
        final Collection<ItemStack> items = npcLootReceived.getItems();
        final String name = npc.getName();
        final int combat = npc.getCombatLevel();

        addLoot(name, combat, LootRecordType.NPC, npc.getId(), items);
    }

    @Subscribe
    public void onPlayerLootReceived(final PlayerLootReceived playerLootReceived)
    {

        final Player player = playerLootReceived.getPlayer();
        final Collection<ItemStack> items = playerLootReceived.getItems();
        final String name = player.getName();
        final int combat = player.getCombatLevel();

        addLoot(name, combat, LootRecordType.PLAYER, null, items);
    }
}
