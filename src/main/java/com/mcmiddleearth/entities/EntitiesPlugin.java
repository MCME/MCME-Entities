package com.mcmiddleearth.entities;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mcmiddleearth.entities._research.*;
import com.mcmiddleearth.entities.command.VirtualCommand;
import com.mcmiddleearth.entities.events.listener.EntitySelectionListener;
import com.mcmiddleearth.entities.events.listener.PlayerListener;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import com.mcmiddleearth.entities.server.EntityServer;
import com.mcmiddleearth.entities.server.SyncEntityServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class EntitiesPlugin extends JavaPlugin {

    private static EntitiesPlugin instance;

    private static EntityServer server;

    @Override
    public void onEnable() {
        server = new SyncEntityServer(this);
        EntityAPI.init();

        PlayerListener playerListener = new PlayerListener();
        //server.registerEvents(this, new EntitySelectionListener());
        //server.registerEventHandler(this, new PlayerListener());

        Bukkit.getServer().getPluginCommand("npc").setExecutor(new NPCCommand());
        Bukkit.getServer().getPluginCommand("mob").setExecutor(new MobCommand());
        Bukkit.getServer().getPluginCommand("entity").setExecutor(new EntityCommand());
        Bukkit.getServer().getPluginCommand("animation").setExecutor(new AnimationCommand());
        VirtualCommand virtual = new VirtualCommand("virtual");
        Bukkit.getServer().getPluginCommand("virtual").setExecutor(virtual);
        Bukkit.getServer().getPluginCommand("virtual").setTabCompleter(virtual);

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
}
