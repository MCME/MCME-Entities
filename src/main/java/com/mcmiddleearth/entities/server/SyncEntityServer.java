package com.mcmiddleearth.entities.server;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.events.McmeEntityRemoveEvent;
import com.mcmiddleearth.entities.events.handler.EntityEventHandler;
import com.mcmiddleearth.entities.events.handler.McmeEntityEventHandler;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;
import com.mcmiddleearth.entities.provider.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncEntityServer implements EntityServer {

    private final EntitiesPlugin plugin;

    private final PlayerProvider playerProvider;
    private final EntityProvider entityProvider;

    private final Map<UUID, BlockProvider> blockProviders;

    private BukkitTask serverTask;

    private int lastEntityId = 100000;

    private int viewDistance = 20;

    private final Map<Class<? extends McmeEntityEvent>, List<McmeEntityEventHandler>> eventHandlers = new HashMap<>();

    public SyncEntityServer(EntitiesPlugin plugin) {
        this.plugin = plugin;
        playerProvider = new SyncPlayerProvider();
        entityProvider = new SyncEntityProvider();
        blockProviders = new HashMap<>();
    }

    @Override
    public void start() {
        serverTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    doTick();
                } catch(Exception ex) {
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,"Ticking error!",ex);
                }
            }
        }.runTaskTimer(plugin,1,1);
    }

    @Override
    public void stop() {
        if(serverTask!=null && !serverTask.isCancelled()) {
            serverTask.cancel();
        }
    }

    @Override
    public void doTick() {
        entityProvider.getEntities().forEach(entity-> {
            if(entity instanceof VirtualEntity) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getLocation().getWorld().equals(entity.getLocation().getWorld())
                            && player.getLocation().getX() < entity.getLocation().getX() + viewDistance
                            && player.getLocation().getY() < entity.getLocation().getY() + viewDistance
                            && player.getLocation().getZ() < entity.getLocation().getZ() + viewDistance
                            && player.getLocation().getX() > entity.getLocation().getX() - viewDistance
                            && player.getLocation().getY() > entity.getLocation().getY() - viewDistance
                            && player.getLocation().getZ() > entity.getLocation().getZ() - viewDistance) {
                        if(!((VirtualEntity)entity).isViewer(player)) {
                            ((VirtualEntity) entity).addViewer(player);
                        }
                    } else {
                        if(((VirtualEntity)entity).isViewer(player)) {
                            ((VirtualEntity) entity).removeViewer(player);
                        }
                    }
                });
            }
            entity.doTick();
        });
    }

    @Override
    public McmeEntity spawnEntity(VirtualEntityFactory factory) {
        McmeEntity result = factory.build(lastEntityId+1);
        lastEntityId += result.getEntityQuantity();
        entityProvider.addEntity(result);
        return result;
    }

    @Override
    public void removeEntity(McmeEntity entity) {
        handleEvent(new McmeEntityRemoveEvent(entity));
        if(entity instanceof VirtualEntity) {
            ((VirtualEntity)entity).removeAllViewers();
        }
        entityProvider.removeEntity(entity);
    }

    @Override
    public void removeEntity(Collection<McmeEntity> entities) {
        entities.forEach(this::removeEntity);
    }

    @Override
    public McmeEntity getEntity(UUID uniqueId) {
        return entityProvider.getEntity(uniqueId);
    }

    @Override
    public McmeEntity getEntity(String name) {
        return entityProvider.getEntityByName(name);
    }

    @Override
    public McmeEntity getEntity(int entityId) {
        return entityProvider.getEntity(entityId);
    }

    @Override
    public Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ) {
        return entityProvider.getEntitiesAt(location, rangeX,rangeY,rangeZ);
    }

    @Override
    public PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    @Override
    public BlockProvider getBlockProvider(UUID worldUniqueId) {
        BlockProvider result = blockProviders.get(worldUniqueId);
        if (result == null) {
            result = new SyncBlockProvider(Bukkit.getWorld(worldUniqueId));
            blockProviders.put(worldUniqueId, result);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerEventHandler(McmeEventListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
              .filter(method -> {
//Logger.getGlobal().info(method.toString());
//Logger.getGlobal().info( "annotation: "+(method.getDeclaredAnnotation(EntityEventHandler.class)!=null));
//Logger.getGlobal().info( "parameters: "+(method.getParameterCount()));
//Logger.getGlobal().info( "assignable: "+McmeEntityEvent.class.isAssignableFrom(method.getParameterTypes()[0]));
                  return method.getDeclaredAnnotation(EntityEventHandler.class)!=null
                          && method.getParameterCount()==1
                          && McmeEntityEvent.class.isAssignableFrom(method.getParameterTypes()[0]);
              })
              .forEach(method -> {
//Logger.getGlobal().info("Matching: "+method.toString());
                  Class<? extends McmeEntityEvent> parameterType = (Class<? extends McmeEntityEvent>) method.getParameterTypes()[0];
                  List<McmeEntityEventHandler> handlerList = eventHandlers.computeIfAbsent(parameterType, k -> new ArrayList<>());
                  handlerList.add(new McmeEntityEventHandler(method, listener));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void UnregisterEventHandler(McmeEventListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(EntityEventHandler.class)!=null
                        && method.getParameterCount()==1
                        && McmeEventListener.class.isAssignableFrom(method.getParameterTypes()[0]))
                .forEach(method -> {
                    Class<? extends McmeEntityEvent> parameterType = (Class<? extends McmeEntityEvent>) method.getParameterTypes()[0];
                    List<McmeEntityEventHandler> handlerList = eventHandlers.get(parameterType);
                    if(handlerList != null) {
                        handlerList.remove(new McmeEntityEventHandler(method, listener));
                    }
                });
    }

    @Override
    public void handleEvent(McmeEntityEvent event) {
//eventHandlers.forEach((key,value)-> Logger.getGlobal().info(key.toString() +"\n----\n"+value.toString()+"\n***********************\n"));
        List<McmeEntityEventHandler> handlerList = eventHandlers.get(event.getClass());
        if(handlerList != null) {
            handlerList.forEach(handler -> handler.handle(event));
        }
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    /*public boolean isPassable(World world, int x, int y, int z) {
        Block block = world.getBlockAt(x,y,z);
        block.getBoundingBox().co
        if(block.isPassable()) {
            return true;
        } else {
            BlockData data = block.getBlockData();
            return (data instanceof Door && ((Door) data).isOpen())
                    || (data instanceof Gate && ((Gate) data).isOpen())
                    || (data instanceof TrapDoor)

        }
    }*/

}
