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

    private final WrappedDataWatcher headPoseDataWatcher = new WrappedDataWatcher();
    private final WrappedDataWatcher.WrappedDataWatcherObject headPoseWrappedObject = new WrappedDataWatcher.WrappedDataWatcherObject(EntityMeta.ARMOR_STAND_HEAD_POSE, WrappedDataWatcher.Registry.getVectorSerializer());
    private final List<Vector3F> headPoseQueue = new ArrayList<>();

    public BoneMetaPacket(Bone bone, int headPoseDelay) {
        this.bone = bone;
        posePacket = createHeadPosePacket(headPoseDataWatcher);
        posePacket.getWatchableCollectionModifier().write(0, headPoseDataWatcher.getWatchableObjects());

        equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0,bone.getEntityId());

        for(int i = 0; i < headPoseDelay; i++) {
            headPoseQueue.add(null);
        }
        //headPoseQueue.add(null);
        update();
    }

    protected PacketContainer createHeadPosePacket(WrappedDataWatcher dataWatcher) {
        PacketContainer posePacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        posePacket.getIntegers().write(0, bone.getEntityId());

        updateHeadPose(getHeadPose());

        return posePacket;
    }

    @Override
    public void update() {
        Vector3F nextHeadPose = updateHeadPoseQueueAndPopNext();
        if(nextHeadPose != null) {
            headPoseDataWatcher.setObject(headPoseWrappedObject, nextHeadPose, false);
            hasPoseUpdate = true;
        } else {
            hasPoseUpdate = false;
        }
        if(bone.isHasItemUpdate()) {
            writeHeadItem();
            hasItemUpdate = true;
        } else {
            hasItemUpdate = false;
        }
    }

    private Vector3F updateHeadPoseQueueAndPopNext() {
        boolean hasHeadPoseUpdate = bone.isHasHeadPoseUpdate();

        if (headPoseQueue.size() == 0) {
            // No delay is enabled - skip the queue
            return hasHeadPoseUpdate ? getHeadPose() : null;
        }

        if (hasHeadPoseUpdate) {
            headPoseQueue.add(getHeadPose());
        } else {
            headPoseQueue.add(null);
        }

        return headPoseQueue.remove(0);
    }

    protected Vector3F getHeadPose() {
        return new Vector3F((float)bone.getRotatedHeadPose().getX(),
                (float)bone.getRotatedHeadPose().getY(),
                (float)bone.getRotatedHeadPose().getZ());
    }

    protected void updateHeadPose(Vector3F headPose) {
        headPoseDataWatcher.setObject(headPoseWrappedObject, headPose, false);
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
