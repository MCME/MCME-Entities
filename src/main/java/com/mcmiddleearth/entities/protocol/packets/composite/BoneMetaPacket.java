package com.mcmiddleearth.entities.protocol.packets.composite;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.bones.Bone;
import com.mcmiddleearth.entities.protocol.EntityMeta;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoneMetaPacket extends AbstractPacket {

    protected PacketContainer posePacket;
    protected PacketContainer equipPacket;

    protected final Bone bone;

    private boolean hasPoseUpdate, hasItemUpdate;

    private final List<WrappedDataWatcher> headPoseQueue = new ArrayList<>();

    public BoneMetaPacket(Bone bone, int headPoseDelay) {
        this.bone = bone;
        posePacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        posePacket.getIntegers().write(0,bone.getEntityId());

        equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0,bone.getEntityId());

        for(int i = 0; i < headPoseDelay; i++) {
            headPoseQueue.add(null);
        }
        //headPoseQueue.add(null);
        update();
    }

    @Override
    public void update() {
        if(bone.isHasHeadPoseUpdate()) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            writeHeadPose(watcher);
            headPoseQueue.add(watcher);
        } else {
            headPoseQueue.add(null);
        }
        if(headPoseQueue.get(0)!=null) {
            posePacket.getWatchableCollectionModifier().write(0,headPoseQueue.get(0).getWatchableObjects());
            hasPoseUpdate = true;
        } else {
            hasPoseUpdate = false;
        }
        headPoseQueue.remove(0);
        if(bone.isHasItemUpdate()) {
            writeHeadItem();
            hasItemUpdate = true;
        } else {
            hasItemUpdate = false;
        }
    }

    protected void writeHeadPose(WrappedDataWatcher watcher) {
        WrappedDataWatcher.WrappedDataWatcherObject state = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.ARMOR_STAND_HEAD_POSE, WrappedDataWatcher.Registry.getVectorSerializer());
        watcher.setObject(state, new Vector3F((float)bone.getRotatedHeadPose().getX(),
                                              (float)bone.getRotatedHeadPose().getY(),
                                              (float)bone.getRotatedHeadPose().getZ()), false);
    }

    protected void writeHeadItem() {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, bone.getHeadItem()));
        equipPacket.getSlotStackPairLists().write(0, equipment);
    }

    @Override
    public void send(Player recipient) {
        if(hasPoseUpdate) {
            send(posePacket, recipient);
//Logger.getGlobal().info("send bone pose");
        }
        if(hasItemUpdate) {
            send(equipPacket, recipient);
//Logger.getGlobal().info("send bone item");
        }
    }
}
