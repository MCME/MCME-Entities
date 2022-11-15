package com.mcmiddleearth.entities.protocol.packets.composite;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.bones.Bone;
import com.mcmiddleearth.entities.protocol.EntityMeta;
import org.bukkit.entity.Player;

public class BoneInitPacket extends BoneMetaPacket {

    public BoneInitPacket(Bone bone) {
        super(bone);

//long start = System.currentTimeMillis();

        writeHeadItem();
//Logger.getGlobal().info("item: "+(System.currentTimeMillis()-start));

//Logger.getGlobal().info("Bone creation: "+(System.currentTimeMillis()-start));
    }

    @Override
    protected PacketContainer createHeadPosePacket(WrappedDataWatcher dataWatcher) {
        PacketContainer packet = super.createHeadPosePacket(dataWatcher);

        WrappedDataWatcher.WrappedDataWatcherObject invisibility = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.ENTITY_STATUS,WrappedDataWatcher.Registry.get(Byte.class));
        dataWatcher.setObject(invisibility, Byte.decode("0x20"),false);
        WrappedDataWatcher.WrappedDataWatcherObject silent = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.ENTITY_SILENT,WrappedDataWatcher.Registry.get(Boolean.class));
        dataWatcher.setObject(silent, true,false);
        WrappedDataWatcher.WrappedDataWatcherObject gravity = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.ENTITY_NO_GRAVITY,WrappedDataWatcher.Registry.get(Boolean.class));
        dataWatcher.setObject(gravity, false,false);
        WrappedDataWatcher.WrappedDataWatcherObject base = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.ARMOR_STAND_STATUS,WrappedDataWatcher.Registry.get(Byte.class));
        dataWatcher.setObject(base, Byte.decode("0x08"),false);

        return packet;
    }

    @Override
    public void send(Player recipient) {
        // Override super to unconditionally send out packets - these are used to spawn the entity in
        send(posePacket, recipient);
        send(equipPacket, recipient);
//Logger.getGlobal().info("send bone init to "+recipient.getName()+" with id: "+bone.getEntityId());
    }
}
