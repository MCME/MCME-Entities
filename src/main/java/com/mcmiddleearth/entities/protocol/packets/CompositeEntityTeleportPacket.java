package com.mcmiddleearth.entities.protocol.packets;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class CompositeEntityTeleportPacket extends AbstractPacket {

    private final CompositeEntity entity;

    public CompositeEntityTeleportPacket(CompositeEntity entity) {
        this.entity = entity;
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("Teleport composite");
        entity.getBones().forEach(bone -> bone.getTeleportPacket().send(recipient));
        entity.getBones().forEach(bone -> bone.getMetaPacket().send(recipient));
    }

    @Override
    public void update() {
        entity.getBones().forEach(bone -> bone.getTeleportPacket().update());
        entity.getBones().forEach(bone -> bone.getMetaPacket().update());

    }
}
