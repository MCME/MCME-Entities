package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SyncEntityProvider implements EntityProvider {

    private final List<McmeEntity> entities = new ArrayList<>();

    @Override
    public Collection<McmeEntity> getEntities() {
        return entities;
    }

    @Override
    public Collection<McmeEntity> getEntitiesAt(Location location, int rangeX, int rangeY, int rangeZ) {
        return entities.stream().filter(entity -> {
            return entity.getLocation().getWorld().equals(location.getWorld())
                    && entity.getLocation().getX() < location.getX()+rangeX
                    && entity.getLocation().getY() < location.getY()+rangeY
                    && entity.getLocation().getZ() < location.getZ()+rangeZ
                    && entity.getLocation().getX() > location.getX()-rangeX
                    && entity.getLocation().getY() > location.getY()-rangeY
                    && entity.getLocation().getZ() > location.getZ()-rangeZ;
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<McmeEntity> getEntityByType(McmeEntityType type) {
        return entities.stream().filter(entity -> entity.getType().equals(type)).collect(Collectors.toList());
    }

    @Override
    public Collection<McmeEntity> geEntitiesByClass(Class<? extends McmeEntity> clazz) {
        return entities.stream().filter(clazz::isInstance).collect(Collectors.toList());
    }

    @Override
    public McmeEntity getEntityByName(String name) {
        return entities.stream().filter(entity -> entity.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public McmeEntity getEntity(UUID uniqueId) {
        return entities.stream().filter(entity -> entity.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public McmeEntity getEntity(int entityId) {
        return entities.stream().filter(entity -> entity.getEntityId() == entityId).findFirst().orElse(null);
    }

    @Override
    public void addEntity(McmeEntity entity) {
        entities.add(entity);
    }

    @Override
    public void removeEntity(McmeEntity entity) {
        entities.remove(entity);
    }
}
