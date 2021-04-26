package com.mcmiddleearth.entities.protocol.packets;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.entity.Player;

public class CompositeEntityTeleportPacket extends AbstractPacket {

    private final CompositeEntity entity;

    public CompositeEntityTeleportPacket(CompositeEntity entity) {
        this.entity = entity;
    }

    @Override
    public void send(Player recipient) {
        entity.getBones().forEach(bone -> bone.getTeleportPacket().send(recipient));
    }
}
