package com.gimptracker;

import com.google.inject.ProvisionException;
import java.awt.GridLayout;
import java.awt.TrayIcon;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
class GimpTrackerPanel extends PluginPanel
{
    private final Client client;
    private final GimpTrackerPlugin plugin;

    @Inject
    private GimpTrackerPanel(
            Client client,
            GimpTrackerPlugin plugin)
    {
        super();
        this.client = client;
        this.plugin = plugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(createOptionsPanel());
    }

    private JPanel createOptionsPanel()
    {
        final JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARK_GRAY_COLOR);
        container.setLayout(new GridLayout(0, 1, 3, 3));

        container.add(plugin.getConnectButtonLabel());
        container.add(plugin.getConnectButton());
        container.add(plugin.getDebugButton());

        return container;
    }
}
