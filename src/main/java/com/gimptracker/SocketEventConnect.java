package com.gimptracker;

import io.socket.emitter.Emitter;

public class SocketEventConnect implements Emitter.Listener
{
    private GimpTrackerPlugin plugin;

    SocketEventConnect(GimpTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void call(Object... objects)
    {
        // on client connected
        plugin.getConnectionManager().setConnectionStatus(ConnectionManager.ConnectionStatus.CONNECTED);

        // after we're connected, we will get a callback from the server wether or not we
        // were authorized, and then instantly disconnect us, so wait for this response before
        // we do anything
    }
}
