package com.mcmiddleearth.entities.protocol.packets.composite;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import com.mcmiddleearth.entities.protocol.packets.AbstractMovePacket;
import org.bukkit.entity.Player;

public class CompositeEntityMovePacket extends AbstractMovePacket {

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

    @Override
    public void markMovementDirty(MoveType moveType) {
        // NOOP: composite entities know better when their packets need to be updates
    }
}
