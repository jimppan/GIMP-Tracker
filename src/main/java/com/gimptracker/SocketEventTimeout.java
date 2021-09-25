package com.gimptracker;

import io.socket.emitter.Emitter;

public class SocketEventTimeout implements Emitter.Listener
{
    private GimpTrackerPlugin plugin;

    SocketEventTimeout(GimpTrackerPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void call(Object... objects)
    {
        // on client timeout (5 seconds after connect by default)
        plugin.getConnectionManager().setConnectionError(ConnectionManager.ConnectionError.TIMED_OUT);
        plugin.getConnectionManager().disconnect();
    }
}
