package com.mcmiddleearth.entities.server;

import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloonEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.events.events.McmeEntityEvent;
import com.mcmiddleearth.entities.events.listener.McmeEventListener;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.provider.BlockProvider;
import com.mcmiddleearth.entities.provider.PlayerProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;

public interface EntityServer {

    int NO_ENTITY_ID = 100000;

    void start();

    void stop();

    void doTick();

    McmeEntity spawnEntity(VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException;

    void removeEntity(McmeEntity entity);

    void removeEntity(Collection<? extends McmeEntity> entities);

    Collection<? extends McmeEntity> getEntities(Class<? extends McmeEntity> clazz);

    McmeEntity getEntity(UUID uniqueId);

    McmeEntity getEntity(String name);

    McmeEntity getEntity(int entityId);

    Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ);

    PlayerProvider getPlayerProvider();

    BlockProvider getBlockProvider(UUID worldUniqueId);

    void registerEvents(Plugin plugin, McmeEventListener handler);

    void unregisterEvents(Plugin plugin, McmeEventListener handler);

    void unregisterEvents(Plugin plugin);

    void handleEvent(McmeEntityEvent event);

    SpeechBalloonEntity spawnSpeechBalloon(VirtualEntity virtualEntity, Player viewer,
                                           SpeechBalloonLayout layout) throws InvalidLocationException;

    //public boolean isPassable(int x, int y, int z);
    Collection<RealPlayer> getMcmePlayers();

    RealPlayer getOrCreateMcmePlayer(Player player);

    RealPlayer getMcmePlayer(UUID uniqueId);

    void removePlayer(Player player);

    long getCurrentTick();
}
