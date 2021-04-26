package com.mcmiddleearth.entities;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.provider.PlayerProvider;
import com.mcmiddleearth.entities.server.EntityServer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class EntityAPI {

    private static EntityServer entityServer;
    private static PlayerProvider playerProvider;
    //private static EntityProvider entityProvider;

    private static boolean init = false;

    public static void init() {
        if(!init) {
            entityServer = EntitiesPlugin.getEntityServer();
            //EntityProvider entityProvider = entityServer.getEntityProvider();
            playerProvider = entityServer.getPlayerProvider();
        }
        init = true;
    }

    public static Collection<RealPlayer> getMcmePlayers() {
        return playerProvider.getMcmePlayers();
    }

    public static RealPlayer getOrCreateMcmePlayer(Player player) {
        return playerProvider.getOrCreateMcmePlayer(player);
    }

    public static RealPlayer getMcmePlayer(UUID uniqueId) {
        return playerProvider.getMcmePlayer(uniqueId);
    }

    public static McmeCommandSender wrapCommandSender(CommandSender sender) {
        if(sender instanceof Player) {
            return EntityAPI.getOrCreateMcmePlayer((Player) sender);
        } else if(sender instanceof ConsoleCommandSender) {
            return new BukkitCommandSender((ConsoleCommandSender)sender);
        }
        return null;
    }

    public static McmeEntity spawnEntity(VirtualEntityFactory factory) {
        return entityServer.spawnEntity(factory);
    }


    public static void removeEntity(Set<McmeEntity> entities) {
        entityServer.removeEntity(entities);
    }

    public static McmeEntity getEntity(String name) {
        return entityServer.getEntity(name);
    }
}
