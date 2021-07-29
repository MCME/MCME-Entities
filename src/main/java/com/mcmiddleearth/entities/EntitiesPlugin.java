package com.mcmiddleearth.entities;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.entities._research.*;
import com.mcmiddleearth.entities.command.VirtualCommand;
import com.mcmiddleearth.entities.events.listener.EntitySelectionListener;
import com.mcmiddleearth.entities.events.listener.PlayerListener;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import com.mcmiddleearth.entities.server.EntityServer;
import com.mcmiddleearth.entities.server.SyncEntityServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class EntitiesPlugin extends JavaPlugin {

    private static EntitiesPlugin instance;

    private static EntityServer server;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        server = new SyncEntityServer(this);
        EntityAPI.init();

        PlayerListener playerListener = new PlayerListener();
        //server.registerEvents(this, new EntitySelectionListener());
        //server.registerEventHandler(this, new PlayerListener());

        Bukkit.getServer().getPluginCommand("npc").setExecutor(new NPCCommand());
        Bukkit.getServer().getPluginCommand("mob").setExecutor(new MobCommand());
        Bukkit.getServer().getPluginCommand("entity").setExecutor(new EntityCommand());
        Bukkit.getServer().getPluginCommand("animation").setExecutor(new AnimationCommand());

        setExecutor("virtual", new VirtualCommand("virtual"));
        setExecutor("entities", new VirtualCommand("entities"));

        Bukkit.getPluginManager().registerEvents(playerListener,this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //manager.addPacketListener(new EntityListener(this));
        Logger.getGlobal().info("Manager: "+manager);
        manager.addPacketListener(new VirtualEntityUseListener(this, server));

        server.start();

        instance = this;

    }

    @Override
    public void onDisable() {
        server.stop();
    }

    public static EntitiesPlugin getInstance() {
        return instance;
    }

    @NotNull
    public static EntityServer getEntityServer() {
        return server;
    }

    public void reloadConfiguration() {
        this.reloadConfig();
    }

    public void restartServer() {
        server.stop();
        reloadConfiguration();
        server.start();
    }

    private void setExecutor(String command, CommandExecutor executor) {
        PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(command);
        if(pluginCommand!=null) {
            pluginCommand.setExecutor(executor);
            if (executor instanceof TabCompleter)
                pluginCommand.setTabCompleter((TabCompleter) executor);
        }

    }
}
