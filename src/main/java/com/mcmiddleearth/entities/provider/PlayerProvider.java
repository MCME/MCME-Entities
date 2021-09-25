package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface PlayerProvider {

    Collection<RealPlayer> getMcmePlayers();

    RealPlayer getOrCreateMcmePlayer(Player player);

    RealPlayer getMcmePlayer(UUID uniqueId);
    
    RealPlayer getMcmePlayer(String name);

    void removePlayer(Player player);

    Collection<? extends McmeEntity> getMcmePlayersAt(Location location, int rangeX, int rangeY, int rangeZ);
}
