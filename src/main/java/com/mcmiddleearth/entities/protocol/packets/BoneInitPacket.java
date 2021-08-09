package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.bones.Bone;
import org.bukkit.entity.Player;

public class BoneInitPacket extends BoneMetaPacket {

    WrappedDataWatcher watcher = new WrappedDataWatcher();

    public BoneInitPacket(Bone bone) {
        super(bone,0);

//long start = System.currentTimeMillis();
        writeInit(watcher);
//Logger.getGlobal().info("Init: "+(System.currentTimeMillis()-start));

        writeHeadPose(watcher);
//Logger.getGlobal().info("Pose: "+(System.currentTimeMillis()-start));

        writeHeadItem();
//Logger.getGlobal().info("item: "+(System.currentTimeMillis()-start));

        posePacket.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
//Logger.getGlobal().info("Bone creation: "+(System.currentTimeMillis()-start));
    }

    @Override
    public void update() {
        if(bone.isHasHeadPoseUpdate()) {
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
