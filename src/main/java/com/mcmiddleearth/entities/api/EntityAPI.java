package com.mcmiddleearth.entities.api;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.command.BukkitCommandSender;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.provider.PlayerProvider;
import com.mcmiddleearth.entities.server.EntityServer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Main Entity entry point. Provides access to entities and events.
 */
public class EntityAPI {

    private static EntityServer entityServer;
    private static PlayerProvider playerProvider;
    //private static EntityProvider entityProvider;

    private static boolean init = false;

    /**
     * Internal use by EntitiesPlugin class only!
     */
    public static void init() {
        if(!init) {
            entityServer = EntitiesPlugin.getEntityServer();
            //EntityProvider entityProvider = entityServer.getEntityProvider();
            playerProvider = entityServer.getPlayerProvider();
        }
        init = true;
    }

    /*public static Collection<RealPlayer> getMcmePlayers() {
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
    }*/

    /**
     * spawn an entity.
     * @param factory provides information about the entity
     * @return the entity
     * @throws InvalidLocationException Is thrown if provided locations are not in the same world.
     */
    public static McmeEntity spawnEntity(VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        return entityServer.spawnEntity(factory);
    }

    /**
     * Removes a collection of entity.
     * @param entities entities to be removed
     */
    public static void removeEntity(Collection<? extends Entity> entities) {
        entityServer.removeEntity(entities);
    }

    /**
     * Removes an entiy.
     * @param entity Entity to be removed
     */
    public static void removeEntity(Entity entity) {
        if(entity instanceof McmeEntity)
            entityServer.removeEntity((McmeEntity) entity);
    }

    /**
     * Get all entities by their class.
     * @param clazz requested entity class
     * @return collection of found entities
     */
    public Collection<McmeEntity> getEntities(Class<? extends Entity> clazz) {
        return entityServer.getEntities(clazz);
    }

    /**
     * Get an entity by it's name. There may be entities without names, there may also be several entities with same
     * names.
     * @param name name of the entity to get
     * @return First found entity with the provided name or null if no entity could be found.
     */
    public static Entity getEntity(String name) {
        return entityServer.getEntity(name);
    }

    /**
     * Get an entity by it's ID. Each entity managed by EntitiesPlugin has a unique ID but ID's are not persistent
     * after server restart.
     * @param entityId id of desired entity.
     * @return Found entity or null
     */
    public static Entity getEntity(int entityId) {
        return entityServer.getEntity(entityId);
    }

    /**
     * Retrieves all entities within a cuboid area around specified location. Cuboid size is 2 * range in each direction.
     * @param location center location
     * @param rangeX range along x-axis
     * @param rangeY range along y-axis
     * @param rangeZ range along z-axis
     * @return Collection of entities in the cuboid.
     */
    public static Collection<? extends Entity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ) {
        return entityServer.getEntitiesAt(location,rangeX,rangeY,rangeZ);
    }

    /**
     * Registers all methods annotated as McmeEventHandler of handler in the entities event handler queue.
     * @param plugin plugin that is responsible for this event listener
     * @param handler listener containing the event handler methods.
     */
    public static void registerEvents(Plugin plugin, McmeEventListener handler) {
        entityServer.registerEvents(plugin,handler);
    }

    /**
     * Removes all event handler methods in handler from the entities event handler queue.
     * @param plugin Plugin that is responsible for this listener
     * @param handler Listener containing the event handler methods.
     */
    public static void unregisterEvents(Plugin plugin, McmeEventListener handler) {
        entityServer.unregisterEvents(plugin, handler);
    }

    /**
     * Removes all event listeners the plugin is responsible for.
     * @param plugin plugin whose event listeners should be removed
     */
    public static void unregisterEvents(Plugin plugin) {
        entityServer.unregisterEvents(plugin);
    }


}
