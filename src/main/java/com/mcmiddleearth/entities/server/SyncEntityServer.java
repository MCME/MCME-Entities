package com.mcmiddleearth.entities.server;

import com.google.common.base.Joiner;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.PersistentDataKey;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloonEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.events.Cancelable;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.events.McmeEntityRemoveEvent;
import com.mcmiddleearth.entities.events.events.McmeEntitySpawnEvent;
import com.mcmiddleearth.entities.events.handler.EntityEventHandler;
import com.mcmiddleearth.entities.events.handler.McmeEntityEventHandler;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.provider.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SyncEntityServer implements EntityServer {

    private final EntitiesPlugin plugin;

    private final PlayerProvider playerProvider;
    private final EntityProvider entityProvider;

    private final Map<UUID, BlockProvider> blockProviders;

    private ServerTask serverTask;

    private int lastEntityId = NO_ENTITY_ID;

    //private int viewDistance = 20;

    private final Map<Class<? extends McmeEntityEvent>, List<McmeEntityEventHandler>> eventHandlers = new HashMap<>();


    public SyncEntityServer(EntitiesPlugin plugin) {
        this.plugin = plugin;
        playerProvider = new SyncPlayerProvider();
        entityProvider = new SyncEntityProvider();
        blockProviders = new HashMap<>();
    }

    @Override
    public void start() {
        ServerTask newTask = new ServerTask(this);
        new BukkitRunnable() {
            int counter = 100;
            @Override
            public void run() {
                if(serverTask == null || serverTask.isShutdownComplete()) {
                    serverTask = newTask;
//Logger.getGlobal().info("Start new server task");
                    serverTask.start();
                    cancel();
                } else {
                    counter--;
                    if(counter == 0) {
                        Logger.getLogger(SyncEntityServer.class.getSimpleName()).warning("Can't start server as another ServerTask is still running.");
                    }
                }
            }
        }.runTaskTimer(plugin,1,1);
    }

    @Override
    public void stop() {
        if(serverTask!=null && !serverTask.isCancelled()) {
            serverTask.requestShutdown();//shutdownRequest = true;
        }
    }

    @Override
    public void doTick() {
//Logger.getGlobal().info("Server: tick "+entityProvider.getEntities().isEmpty());
        //Logger.getGlobal().info("remove: "+entity);
        entityProvider.getEntities().stream()
                      .filter(McmeEntity::isTerminated).collect(Collectors.toList())
                      .forEach(this::removeEntity);
//Logger.getGlobal().info("Server: tick 2 "+entityProvider.getEntities().isEmpty());
        new ArrayList<>(entityProvider.getEntities()).forEach(entity-> {
            if(entity instanceof VirtualEntity) {
                VirtualEntity virtual = (VirtualEntity) entity;
                Location loc = virtual.getLocation();
                int viewDistance = virtual.getViewDistance();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getLocation().getWorld().equals(entity.getLocation().getWorld())
                            && player.getLocation().getX() < loc.getX() + viewDistance
                            && player.getLocation().getY() < loc.getY() + viewDistance
                            && player.getLocation().getZ() < loc.getZ() + viewDistance
                            && player.getLocation().getX() > loc.getX() - viewDistance
                            && player.getLocation().getY() > loc.getY() - viewDistance
                            && player.getLocation().getZ() > loc.getZ() - viewDistance) {
                        if(!virtual.isViewer(player)) {
                            virtual.addViewer(player);
                        }
                    } else {
                        if(virtual.isViewer(player)) {
                            virtual.removeViewer(player);
                        }
                    }
                });
            }
//Logger.getGlobal().info("Server: tick entity "+entity);
            entity.doTick();
        });
        for(RealPlayer p : playerProvider.getMcmePlayers()) {
            p.doTick();
            if(getCurrentTick()%40==p.getUpdateRandom()) {
                for(int i=0; i< p.getBukkitPlayer().getInventory().getSize(); i++) {
                    ItemStack item = p.getBukkitPlayer().getInventory().getItem(i);
                    if (item != null) {
                        Long removalTime = item.getItemMeta().getPersistentDataContainer().get(EntitiesPlugin.getInstance()
                                        .getPersistentDataKey(PersistentDataKey.ITEM_REMOVAL_TIME),
                                PersistentDataType.PrimitivePersistentDataType.LONG);
                        if (removalTime != null && removalTime < getCurrentTick()) {
                            p.getBukkitPlayer().getInventory().setItem(i, null);
                        }
                    }
                }
            }
        }
    }

    @Override
    public McmeEntity spawnEntity(VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        McmeEntity result = factory.build(lastEntityId+1);
        lastEntityId += result.getEntityQuantity();
        entityProvider.addEntity(result);
        handleEvent(new McmeEntitySpawnEvent(result));
        return result;
    }

    @Override
    public void removeEntity(McmeEntity entity) {
//Logger.getGlobal().info("Server: remove Entity");
        if(!(entity instanceof SpeechBalloonEntity)) {
            handleEvent(new McmeEntityRemoveEvent((McmeEntity) entity));
        }
        if(entity instanceof VirtualEntity) {
            ((VirtualEntity)entity).removeAllViewers();
        }
        ((McmeEntity)entity).finalise();
        entityProvider.removeEntity((McmeEntity)entity);
        playerProvider.getMcmePlayers().forEach(player -> player.removeFromSelectedEntities((McmeEntity)entity));
    }

    @Override
    public void removePlayer(Player player) {
        RealPlayer realPlayer = getMcmePlayer(player.getUniqueId());
        entityProvider.getEntities().forEach(entity -> {
            if(entity instanceof VirtualEntity
                    && ((VirtualEntity)entity).isViewer(player)) {
                ((VirtualEntity)entity).removeViewer(player);
            }
        });
        if(realPlayer != null) {
            playerProvider.removePlayer(player);
        }
    }

    @Override
    public SpeechBalloonEntity spawnSpeechBalloon(VirtualEntity speaker, Player viewer,
                                                  SpeechBalloonLayout layout) throws InvalidLocationException {
        SpeechBalloonEntity balloon =  new SpeechBalloonEntity(lastEntityId+1, speaker, viewer, layout);
        lastEntityId += balloon.getEntityQuantity();
        entityProvider.addEntity(balloon);
        return balloon;
    }

    @Override
    public void removeEntity(Collection<? extends McmeEntity> entities) {
        entities.forEach(this::removeEntity);
    }

    @Override
    public Collection<? extends McmeEntity> getEntities(Class<? extends McmeEntity> clazz) {
        if(clazz.isAssignableFrom(RealPlayer.class)) {
            return playerProvider.getMcmePlayers();
        } else {
            return entityProvider.getEntities().stream().filter(clazz::isInstance).collect(Collectors.toList());
        }
    }

    @Override
    public McmeEntity getEntity(UUID uniqueId) {
        if(uniqueId.version()==4) {
            McmeEntity entity = playerProvider.getMcmePlayer(uniqueId);
            if(entity != null) {
                return entity;
            } else {
                return entityProvider.getEntity(uniqueId);
            }
        } else {
            McmeEntity entity = entityProvider.getEntity(uniqueId);
            if(entity != null) {
                return entity;
            } else {
                return playerProvider.getMcmePlayer(uniqueId);
            }
        }
    }

    @Override
    public McmeEntity getEntity(String name) {
        McmeEntity entity = entityProvider.getEntityByName(name);
        if(entity != null) {
            return entity;
        } else {
            return playerProvider.getMcmePlayer(name);
        }
    }

    @Override
    public McmeEntity getEntity(int entityId) {
        return entityProvider.getEntity(entityId);
    }

    @Override
    public Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ) {
        Collection<McmeEntity> entities = new HashSet<>();
        entities.addAll(entityProvider.getEntitiesAt(location, rangeX,rangeY,rangeZ));
        entities.addAll(playerProvider.getMcmePlayersAt(location,rangeX, rangeY, rangeZ));
        return entities;
    }

    @Override
    public PlayerProvider getPlayerProvider() {
        return playerProvider;
    }

    private EntityProvider getEntityProvider() {
        return entityProvider;
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
    public void registerEvents(Plugin plugin, McmeEventListener listener) {
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
                  EntityEventHandler annotation = method.getDeclaredAnnotation(EntityEventHandler.class);
                  Class<? extends McmeEntityEvent> parameterType = (Class<? extends McmeEntityEvent>) method.getParameterTypes()[0];
                  List<McmeEntityEventHandler> handlerList = eventHandlers.computeIfAbsent(parameterType, k -> new ArrayList<>());
                  handlerList.add(new McmeEntityEventHandler(plugin, method, listener, annotation.priority, annotation.ignoreCancelled));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unregisterEvents(Plugin plugin, McmeEventListener listener) {
        Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(EntityEventHandler.class)!=null
                        && method.getParameterCount()==1
                        && McmeEventListener.class.isAssignableFrom(method.getParameterTypes()[0]))
                .forEach(method -> {
                    Class<? extends McmeEntityEvent> parameterType = (Class<? extends McmeEntityEvent>) method.getParameterTypes()[0];
                    List<McmeEntityEventHandler> handlerList = eventHandlers.get(parameterType);
                    if(handlerList != null) {
                        handlerList.remove(new McmeEntityEventHandler(plugin, method, listener, EventPriority.NORMAL, false));
                    }
                });
    }

    @Override
    public void unregisterEvents(Plugin plugin) {
        eventHandlers.values().forEach(handlerList-> handlerList.stream().filter(handler->handler.getPlugin().equals(plugin))
                   .collect(Collectors.toSet()).forEach(handlerList::remove));
    }

    @Override
    public void handleEvent(McmeEntityEvent event) {
//eventHandlers.forEach((key,value)-> Logger.getGlobal().info(key.toString() +"\n----\n"+value.toString()+"\n***********************\n"));
        List<McmeEntityEventHandler> handlerList = eventHandlers.get(event.getClass());
        if(handlerList != null) {
            handlerList.stream().filter(handler->handler.isIgnoreCancelled() || !(event instanceof Cancelable) || !((Cancelable)event).isCancelled())
                    .sorted(Comparator.comparingInt(one -> getValue(one.getPriority())))
                    .forEachOrdered(handler -> handler.handle(event));
        }
    }

    private int getValue(EventPriority priority) {
        switch(priority) {
            case LOWEST: return 0;
            case LOW: return 1;
            case NORMAL: return 2;
            case HIGH: return 3;
            case HIGHEST: return 4;
            case MONITOR:
            default: return 5;
        }
    }

    /*public int getViewDistance() {
        return viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }*/

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

    public static class ServerTask {

        private boolean shutdownRequest = false;
        private boolean shutdownComplete = false;
        private final SyncEntityServer server;
        private BukkitTask task;

        public ServerTask(SyncEntityServer server) {
            this.server = server;
        }

        public void requestShutdown() {
            shutdownRequest = true;
        }

        public boolean isCancelled() {
            return task.isCancelled();
        }

        public boolean isShutdownComplete() {
            return shutdownComplete;
        }

        public void start() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (shutdownRequest) {
                            new ArrayList<>(server.getEntityProvider().getEntities()).forEach(server::removeEntity);
                            cancel();
                            shutdownComplete = true;
                        } else {
                            server.doTick();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getSimpleName())
                                .log(Level.WARNING, "Ticking error!\n"+ ex.getLocalizedMessage()
                                        + "\n" + Joiner.on("\n").join(ex.getStackTrace()));
                    }
                }
            }.runTaskTimer(EntitiesPlugin.getInstance(),1,1);
        }
    }

    public Collection<RealPlayer> getMcmePlayers() {
        return playerProvider.getMcmePlayers();
    }

    public RealPlayer getOrCreateMcmePlayer(Player player) {
        return playerProvider.getOrCreateMcmePlayer(player);
    }

    public RealPlayer getMcmePlayer(UUID uniqueId) {
        return playerProvider.getMcmePlayer(uniqueId);
    }

    public long getCurrentTick() {
        return System.currentTimeMillis()/50;// Bukkit.getCurrentTick();
    }
}
