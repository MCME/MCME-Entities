package com.mcmiddleearth.entities;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.GsonBuilder;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.ai.movement.EntityBoundingBox;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.command.*;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.events.listener.PlayerListener;
import com.mcmiddleearth.entities.events.listener.ProjectileListener;
import com.mcmiddleearth.entities.json.*;
import com.mcmiddleearth.entities.protocol.listener.VirtualEntityUseListener;
import com.mcmiddleearth.entities.server.EntityServer;
import com.mcmiddleearth.entities.server.SyncEntityServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.logging.Logger;

public final class EntitiesPlugin extends JavaPlugin {

    private static EntitiesPlugin instance;

    private static EntityServer server;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //manager.addPacketListener(new EntityListener(this));
        Logger.getGlobal().info("Manager: "+manager);

        server = new SyncEntityServer(this);
        EntityAPI.init();

        PlayerListener playerListener = new PlayerListener();
        ProjectileListener projectileListener = new ProjectileListener();
        //server.registerEvents(this, new EntitySelectionListener());
        //server.registerEventHandler(this, new PlayerListener());

        //Bukkit.getServer().getPluginCommand("npc").setExecutor(new NPCCommand());
        //Bukkit.getServer().getPluginCommand("mob").setExecutor(new MobCommand());
        //Bukkit.getServer().getPluginCommand("entity").setExecutor(new EntityCommand());
        //Bukkit.getServer().getPluginCommand("animation").setExecutor(new AnimationCommand());

        //setExecutor("virtual", new VirtualCommand("virtual"));
        setExecutor("vserver", new EntitiesCommand("vserver"));
        setExecutor("vspawn", new SpawnCommand("vspawn"));
        setExecutor("varmy", new ArmyCommand("varmy"));
        setExecutor("vremove", new RemoveCommand("vremove"));
        setExecutor("vsay", new SayCommand("vsay"));
        setExecutor("vselect", new SelectCommand("vselect"));
        setExecutor("vfactory", new FactoryCommand("vfactory"));
        setExecutor("vset", new SetCommand("vset"));
        setExecutor("vexplode", new ExplosionCommand("vexplode"));
        setExecutor("vanimate", new AnimateCommand("vanimate"));
        setExecutor("vtag", new TagCommand("vtag"));
        setExecutor("vload", new LoadCommand("vload"));
        setExecutor("vsave", new SaveCommand("vsave"));
        setExecutor("vtest", new TestCommand("vtest"));

        Bukkit.getPluginManager().registerEvents(playerListener,this);
        Bukkit.getPluginManager().registerEvents(projectileListener,this);

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
                .setPrettyPrinting()
                .setLenient()
                .registerTypeAdapter(VirtualEntityAttributeInstance.class, new AttributeInstanceAdapter().nullSafe())
                .registerTypeAdapter(EntityBoundingBox.class, new EntityBoundingBoxAdapter().nullSafe())
                .registerTypeAdapter(Location.class, new LocationAdapter().nullSafe())
                .registerTypeAdapter(SpeechBalloonLayout.class, new SpeechBalloonLayoutAdapter().nullSafe())
                .registerTypeAdapter(Vector.class, new VectorAdapter().nullSafe())
                .registerTypeAdapter(VirtualEntityFactory.class, new VirtualEntityFactoryAdapter().nullSafe())
                .registerTypeAdapter(VirtualEntityGoalFactory.class, new VirtualEntityGoalFactoryAdapter().nullSafe());
    }

    public static File getEntitiesFolder() {
        File file = new File(instance.getDataFolder(),"entity");
        if(!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static File getAnimationFolder() {
        File file = new File(instance.getDataFolder(), "animation");
        if(!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public NamespacedKey getPersistentDataKey(PersistentDataKey key) {
        return new NamespacedKey(this, key.name());
    }

}
