package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class SyncPlayerProvider implements PlayerProvider {

    public HashMap<UUID, RealPlayer> players = new HashMap<>();

    @Override
    public Collection<RealPlayer> getMcmePlayers() {
        return players.values();
    }

    @Override
    public RealPlayer getOrCreateMcmePlayer(Player player) {
        RealPlayer result = players.get(player.getUniqueId());
        if(result == null) {
            result = new RealPlayer(player);
            players.put(player.getUniqueId(),result);
        }
        return result;
    }

    @Override
    public RealPlayer getMcmePlayer(UUID uniqueId) {
        return players.get(uniqueId);
    }

    @Override
    public RealPlayer getMcmePlayer(String name) {
        return players.values().stream().filter(player -> player.getName().equals(name))
                                          .findFirst().orElse(null);
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    @Override
    public Collection<? extends McmeEntity> getMcmePlayersAt(Location location, int rangeX, int rangeY, int rangeZ) {
        return players.values().stream().filter(entity -> {
            return entity.getLocation().getWorld().equals(location.getWorld())
                    && entity.getLocation().getX() < location.getX()+rangeX
                    && entity.getLocation().getY() < location.getY()+rangeY
                    && entity.getLocation().getZ() < location.getZ()+rangeZ
                    && entity.getLocation().getX() > location.getX()-rangeX
                    && entity.getLocation().getY() > location.getY()-rangeY
                    && entity.getLocation().getZ() > location.getZ()-rangeZ;
        }).collect(Collectors.toList());
    }
}
