package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface PlayerProvider {

    public Collection<RealPlayer> getMcmePlayers();

    public RealPlayer getOrCreateMcmePlayer(Player player);

    public RealPlayer getMcmePlayer(UUID uniqueId);
    
    public RealPlayer getMcmePlayer(String name);

    public void removePlayer(Player player);

    Collection<? extends McmeEntity> getMcmePlayersAt(Location location, int rangeX, int rangeY, int rangeZ);
}
