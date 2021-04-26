package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.Bone;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class BoneInitPacket extends BoneMetaPacket {

    WrappedDataWatcher watcher = new WrappedDataWatcher();

    public BoneInitPacket(Bone bone) {
        super(bone);

        writeInit(watcher);
        writeHeadPose(watcher);

        writeHeadItem();

        posePacket.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
    }

    @Override
    public void update() {
        if(bone.isHasHeadRotationUpdate()) {
            writeHeadPose(watcher);
            posePacket.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
        }

        if(bone.isHasItemUpdate()) {
            writeHeadItem();
        }
    }

    protected void writeInit(WrappedDataWatcher watcher) {
        WrappedDataWatcher.WrappedDataWatcherObject invisibility = new WrappedDataWatcher
                .WrappedDataWatcherObject(0,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(invisibility, Byte.decode("0x20"),false);
        WrappedDataWatcher.WrappedDataWatcherObject silent = new WrappedDataWatcher
                .WrappedDataWatcherObject(4,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(silent, true,false);
        WrappedDataWatcher.WrappedDataWatcherObject gravity = new WrappedDataWatcher
                .WrappedDataWatcherObject(5,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(gravity, false,false);
        WrappedDataWatcher.WrappedDataWatcherObject base = new WrappedDataWatcher
                .WrappedDataWatcherObject(14,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(base, Byte.decode("0x08"),false);
    }

    @Override
    public void send(Player recipient) {
        send(posePacket, recipient);
        send(equipPacket, recipient);
//Logger.getGlobal().info("send bone init to "+recipient.getName()+" with id: "+bone.getEntityId());
    }
}
