package com.mcmiddleearth.entities.protocol.packets;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.entity.Player;

public class CompositeEntityMovePacket extends AbstractPacket {

    private final CompositeEntity entity;

    public CompositeEntityMovePacket(CompositeEntity entity) {
        this.entity = entity;
    }

    @Override
    public void send(Player recipient) {
        entity.getBones().forEach(bone -> bone.getMovePacket().send(recipient));
        entity.getBones().forEach(bone -> bone.getMetaPacket().send(recipient));
    }

    @Override
    public void update() {
        entity.getBones().forEach(bone -> bone.getMovePacket().update());
        entity.getBones().forEach(bone -> bone.getMetaPacket().update());
    }
}
