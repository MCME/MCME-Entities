package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

public interface EntityProvider {

    public Collection<McmeEntity> getEntities();

    public Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ);

    public Collection<McmeEntity> getEntityByType(McmeEntityType type);

    public Collection<McmeEntity> geEntitiesByClass(Class<? extends McmeEntity> clazz);

    public McmeEntity getEntityByName(String name);

    public McmeEntity getEntity(UUID uniqueId);

    public McmeEntity getEntity(int entityId);

    public void addEntity(McmeEntity entity);

    public void removeEntity(McmeEntity entity);
}
