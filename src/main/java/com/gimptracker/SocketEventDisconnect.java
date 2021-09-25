package com.gimptracker;

import io.socket.emitter.Emitter;

import java.sql.Connection;

public class SocketEventDisconnect implements Emitter.Listener
{
    private GimpTrackerPlugin plugin;

    SocketEventDisconnect(GimpTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void call(Object... objects)
    {
        // on client disconnect
        if(objects.length > 0)
        {
            // server disconnected the socket force fully which means unauthorized
            String reason = (String)objects[0];
            if(reason.equals("io server disconnect"))
                plugin.getConnectionManager().setConnectionError(ConnectionManager.ConnectionError.UNAUTHORIZED);
        }

        plugin.getConnectionManager().setConnectionStatus(ConnectionManager.ConnectionStatus.DISCONNECTED);
    }
}
