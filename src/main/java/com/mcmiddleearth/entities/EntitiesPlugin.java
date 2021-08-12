package com.mcmiddleearth.entities;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import com.mcmiddleearth.entities.command.EntitiesCommand;
import com.mcmiddleearth.entities.command.VirtualCommand;
import com.mcmiddleearth.entities.events.listener.PlayerListener;
import com.mcmiddleearth.entities.json.*;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import com.mcmiddleearth.entities.server.EntityServer;
import com.mcmiddleearth.entities.server.SyncEntityServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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

        //Bukkit.getServer().getPluginCommand("npc").setExecutor(new NPCCommand());
        //Bukkit.getServer().getPluginCommand("mob").setExecutor(new MobCommand());
        //Bukkit.getServer().getPluginCommand("entity").setExecutor(new EntityCommand());
        //Bukkit.getServer().getPluginCommand("animation").setExecutor(new AnimationCommand());

        setExecutor("virtual", new VirtualCommand("virtual"));
        setExecutor("entities", new EntitiesCommand("entities"));

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

    public static McmeCommandSender wrapCommandSender(CommandSender sender) {
        if(sender instanceof Player) {
            return getEntityServer().getPlayerProvider().getOrCreateMcmePlayer((Player) sender);
        } else if(sender instanceof ConsoleCommandSender) {
            return new BukkitCommandSender((ConsoleCommandSender)sender);
        }
        return null;
    }

    public static GsonBuilder getEntitiesGsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationAdapter().nullSafe())
                .registerTypeAdapter(Vector.class, new VectorAdapter().nullSafe())
                .registerTypeAdapter(VirtualEntityFactory.class, new VirtualEntityFactoryAdapter().nullSafe())
                .registerTypeAdapter(VirtualEntityGoalFactory.class, new VirtualEntityGoalFactoryAdapter().nullSafe())
                .registerTypeAdapter(EntityBoundingBox.class, new EntityBoundingBoxAdapter().nullSafe());
    }

    public static File getEntitiesFolder() {
        return new File(instance.getDataFolder(),"entity");
    }

    public static File getAnimationFolder() {
        return new File(instance.getDataFolder(), "animation");
    }

}
