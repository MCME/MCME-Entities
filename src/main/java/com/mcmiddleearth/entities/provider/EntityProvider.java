package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

public interface EntityProvider {

    Collection<McmeEntity> getEntities();

    Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ);

    Collection<McmeEntity> getEntityByType(McmeEntityType type);

    Collection<McmeEntity> geEntitiesByClass(Class<? extends McmeEntity> clazz);

    McmeEntity getEntityByName(String name);

    McmeEntity getEntity(UUID uniqueId);

    McmeEntity getEntity(int entityId);

    void addEntity(McmeEntity entity);

    void removeEntity(McmeEntity entity);
}
