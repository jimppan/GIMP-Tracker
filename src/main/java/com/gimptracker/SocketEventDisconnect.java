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
            String reason = (String)objects[0];
            if(reason.equals("io server disconnect")) // server disconnected the socket force fully which means unauthorized
                plugin.getConnectionManager().setConnectionError(ConnectionManager.ConnectionError.UNAUTHORIZED);
            else if(reason.equals("transport error"))  // server crashed or lost connection to the server
                plugin.getConnectionManager().setConnectionError(ConnectionManager.ConnectionError.LOST_CONNECTION);
        }

        plugin.getConnectionManager().setConnectionStatus(ConnectionManager.ConnectionStatus.DISCONNECTED);
    }
}
