package com.mcmiddleearth.entities.provider;

import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

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
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

}
