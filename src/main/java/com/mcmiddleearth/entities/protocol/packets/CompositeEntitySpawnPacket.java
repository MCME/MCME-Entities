package com.mcmiddleearth.entities.protocol.packets;

import com.mcmiddleearth.entities.entities.composite.CompositeEntity;
import org.bukkit.entity.Player;

public class CompositeEntitySpawnPacket extends AbstractPacket {

    private final CompositeEntity entity;

    public CompositeEntitySpawnPacket(CompositeEntity entity) {
        this.entity = entity;
    }

    @Override
    public void send(Player recipient) {
//Logger.getGlobal().info("Sending CompositeSpawn packet to : "+recipient.getName()+" "+entity.getBones().size());
        entity.getBones().forEach(bone -> bone.getSpawnPacket().send(recipient));
        entity.getBones().stream().filter(bone->bone.getDisplayName()!=null).forEach(bone->bone.getNamePacket().send(recipient));
        entity.getBones().forEach(bone -> bone.getInitPacket().send(recipient));
    }

    public void update() {
        entity.getBones().forEach(bone -> bone.getSpawnPacket().update());
        entity.getBones().forEach(bone -> bone.getInitPacket().update());
   }

}
