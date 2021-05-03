/*
MIT License

Copyright (c) 2021 LenderSchlender

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package at.lenderschlender.themisbending;


import at.lenderschlender.themisbending.commands.ReloadCmd;
import at.lenderschlender.themisbending.commands.ThemisGuiCmd;
import at.lenderschlender.themisbending.listener.PingListener;
import at.lenderschlender.themisbending.listener.ProjectKorraListener;
import at.lenderschlender.themisbending.util.LagUtils;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public class ThemisBending extends JavaPlugin {
    private static ThemisBending INSTANCE;
    public static final String NO_PERMISSION_MESSAGE = ChatColor.RED + "You do not have the permission to do this!";
    public final PingListener pingListener = new PingListener(this);
    public final LagUtils lagUtils = new LagUtils();
    public ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        //Config
        this.saveDefaultConfig();
        FileConfiguration config = getConfig();

        //Protocol stuff
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(pingListener);

        //TPS measurement
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, lagUtils, 10L, 1L);

        INSTANCE = this;
        //Commands
        this.getCommand("themisbending-reload").setExecutor(new ReloadCmd(this));
        this.getCommand("tgui").setExecutor(new ThemisGuiCmd(this));

        //Side plugins
        if (pluginManager.getPlugin("ProjectKorra") != null && config.getBoolean("side_plugins.projectkorra.enable", true)) {
            this.getServer().getPluginManager().registerEvents(new ProjectKorraListener(this), this);
        }
        //bStats metrics
        new Metrics(this, 11117);
    }

    @Override
    public void onDisable() {
        // Now it isn't empty anymore aajjiikk
    }

    public static ThemisBending getInstance() {
        if (INSTANCE == null)
            throw new IllegalStateException("ThemisBending isn't enabled yet!");
        return INSTANCE;
    }

    public long getPing(UUID uuid) {
        return pingListener.getPing(uuid);
    }
}
