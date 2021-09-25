package com.gimptracker;

import io.socket.emitter.Emitter;

import java.sql.Connection;

public class SocketEventAuth implements Emitter.Listener
{
    private GimpTrackerPlugin plugin;

    SocketEventAuth(GimpTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void call(Object... objects)
    {
        // received after backend has authorized client
        if(objects.length != 1)
            return;

        boolean authorized = objects[0].equals("success");

        if(authorized)
        {

            plugin.getConnectionManager().setConnectionError(ConnectionManager.ConnectionError.AUTHORIZED);
        }
    }
}
