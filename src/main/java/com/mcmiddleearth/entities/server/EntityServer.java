package com.mcmiddleearth.entities.server;

import com.mcmiddleearth.entities.api.Entity;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
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

    public void start();

    public void stop();

    public void doTick();

    public McmeEntity spawnEntity(VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException;

    public void removeEntity(Entity entity);

    public void removeEntity(Collection<? extends Entity> entities);

    public Collection<McmeEntity> getEntities(Class<? extends Entity> clazz);

    public McmeEntity getEntity(UUID uniqueId);

    public McmeEntity getEntity(String name);

    public McmeEntity getEntity(int entityId);

    public Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ);

    public PlayerProvider getPlayerProvider();

    public BlockProvider getBlockProvider(UUID worldUniqueId);

    public void registerEvents(Plugin plugin, McmeEventListener handler);

    public void unregisterEvents(Plugin plugin, McmeEventListener handler);

    public void unregisterEvents(Plugin plugin);

    public void handleEvent(McmeEntityEvent event);

    SpeechBalloonEntity spawnSpeechBalloon(VirtualEntity virtualEntity, Player viewer,
                                           SpeechBalloonLayout layout) throws InvalidLocationException;

    //public boolean isPassable(int x, int y, int z);
    public Collection<RealPlayer> getMcmePlayers();

    public RealPlayer getOrCreateMcmePlayer(Player player);

    public RealPlayer getMcmePlayer(UUID uniqueId);

}
