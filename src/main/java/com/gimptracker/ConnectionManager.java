package com.gimptracker;

import com.google.gson.JsonObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.client.config.Config;
import net.runelite.client.plugins.Plugin;

import java.awt.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

@Getter
public class ConnectionManager {

    public class SocketEvent
    {
        public final static String CONNECT = "RL_CONNECT_STATE";
        public final static String UPDATE = "RL_UPDATE_STATE";
    }

    public enum ConnectionStatus
    {
        CONNECTING,
        DISCONNECTING,
        CONNECTED,
        DISCONNECTED,

        MAX
    }

    public enum ConnectionError
    {
        NONE,
        AUTHORIZED,
        UNAUTHORIZED,
        TIMED_OUT,
        BAD_URL,

        MAX,
    }

    public static final String[] connectionStatusStrings =
    {
            "Connecting",
            "Disconnecting",
            "Connected",
            "Disconnected",
    };

    public static final String[] connectionErrorStrings =
    {
            "",
            "Authorized",
            "Unauthorized",
            "Could not connect to URL",
            "Invalid URL"
    };

    public interface ConnectionListener
    {
        public void onConnectionStatusChanged(ConnectionStatus status);
        public void onConnectionErrorChanged(ConnectionError status);
    }

    private URI uri = null;
    public IO.Options options = null;
    public Socket socket = null;
    private SocketEventConnect socketConnectEvent;
    private SocketEventDisconnect socketDisconnectEvent;
    private SocketEventTimeout socketTimeoutEvent;
    private SocketEventAuth socketTimeoutAuth;

    public ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    public ConnectionError connectionError = ConnectionError.NONE;

    public ArrayList<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    public boolean firstPacket = true;
    public boolean authorized = false;

    private GimpTrackerPlugin plugin = null;

    public ConnectionManager(GimpTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    public void setConnectionStatus(ConnectionStatus status)
    {
        if(status == ConnectionStatus.DISCONNECTED)
        {
            firstPacket = true;
            authorized = false;
        }
        connectionStatus = status;
        for(int i = 0; i < listeners.size(); i++)
            listeners.get(i).onConnectionStatusChanged(status);
    }

    public void setConnectionError(ConnectionError error)
    {
        if(error == ConnectionError.AUTHORIZED)
            authorized = true;

        connectionError = error;
        for(int i = 0; i < listeners.size(); i++)
            listeners.get(i).onConnectionErrorChanged(error);
    }

    public void addConnectionListener(ConnectionListener listener)
    {
        listeners.add(listener);
    }

    public void init()
    {
        socketConnectEvent = new SocketEventConnect(this.plugin);
        socketDisconnectEvent = new SocketEventDisconnect(this.plugin);
        socketTimeoutEvent = new SocketEventTimeout(this.plugin);
        socketTimeoutAuth = new SocketEventAuth(this.plugin);

        options = IO.Options.builder().
                setQuery("system=runelite").
                setReconnection(true).
                setTimeout(5_000).
                setAuth(Collections.singletonMap("token", this.plugin.getConfig().password())).
                build();
    }

    public boolean isConnected()
    {
        return socket != null && socket.connected();
    }

    public boolean connect()
    {
        disconnect();

        // check if URL is not complete gibberish
        try {
            uri = URI.create(plugin.getConfig().url());
        }catch (IllegalArgumentException e) {
            setConnectionError(ConnectionError.BAD_URL);
            return false;
        }

        // check if we can connect to given URL
        try {
            socket = IO.socket(uri, options);
        }catch(RuntimeException e) {
            setConnectionError(ConnectionError.BAD_URL);
            return false;
        }

        setConnectionError(ConnectionError.NONE);

        socket.on(Socket.EVENT_CONNECT, socketConnectEvent);
        socket.on(Socket.EVENT_DISCONNECT, socketDisconnectEvent);
        socket.on(Socket.EVENT_CONNECT_ERROR, socketTimeoutEvent);
        socket.on("authorize", socketTimeoutAuth);

        socket.connect();

        setConnectionStatus(ConnectionStatus.CONNECTING);
        return true;
    }

    public void disconnect()
    {
        if(socket != null)
        {
            if(socket.connected())
            {
                setConnectionStatus(ConnectionStatus.DISCONNECTING);

                socket.disconnect();
                socket.close();
                socket = null;
                return;
            }
            else
            {
                socket.close();
            }
        }

        socket = null;
        setConnectionStatus(ConnectionStatus.DISCONNECTED);
    }

    public void sendData(String event, JsonObject json)
    {
        socket.emit(event, json.toString());
        firstPacket = false;
    }
}
