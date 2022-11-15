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
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class BoneMetaPacket extends AbstractPacket {

    protected PacketContainer posePacket;
    protected PacketContainer equipPacket;

    protected final Bone bone;

    private boolean hasPoseUpdate = true;
    private boolean hasItemUpdate = true;
    private boolean shouldResendPose = false;
    private boolean shouldResendItem = false;

    private final WrappedDataWatcher headPoseDataWatcher = new WrappedDataWatcher();
    private final WrappedDataWatcher.WrappedDataWatcherObject headPoseWrappedObject = new WrappedDataWatcher.WrappedDataWatcherObject(EntityMeta.ARMOR_STAND_HEAD_POSE, WrappedDataWatcher.Registry.getVectorSerializer());

    public BoneMetaPacket(Bone bone) {
        this.bone = bone;
        posePacket = createHeadPosePacket(headPoseDataWatcher);
        posePacket.getWatchableCollectionModifier().write(0, headPoseDataWatcher.getWatchableObjects());

        equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0,bone.getEntityId());
    }

    protected PacketContainer createHeadPosePacket(WrappedDataWatcher dataWatcher) {
        PacketContainer posePacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        posePacket.getIntegers().write(0, bone.getEntityId());

        updateHeadPose(getDelayedHeadPoseAsVector());

        return posePacket;
    }

    @Override
    public void update() {
        // Reset resend flags before an update - all relevant updates for the previous update should be sent by now
        shouldResendPose = false;
        shouldResendItem = false;

        if (hasPoseUpdate) {
            updateHeadPose(getDelayedHeadPoseAsVector());
            hasPoseUpdate = false;
            shouldResendPose = true;
        }
        if (hasItemUpdate) {
            writeHeadItem();
            hasItemUpdate = false;
            shouldResendItem = true;
        }
    }

    protected Vector3F getDelayedHeadPoseAsVector() {
        EulerAngle rotatedHeadPose = bone.getDelayedHeadPose();

        return new Vector3F(
                (float) rotatedHeadPose.getX(),
                (float) rotatedHeadPose.getY(),
                (float) rotatedHeadPose.getZ()
        );
    }

    protected void updateHeadPose(Vector3F headPose) {
        headPoseDataWatcher.setObject(headPoseWrappedObject, headPose, false);
    }

    public void markHeadPoseDirty() {
        hasPoseUpdate = true;
    }

    public void markHeadItemDirty() {
        hasItemUpdate = true;
    }

    protected void writeHeadItem() {
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();
        equipment.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, bone.getHeadItem()));
        equipPacket.getSlotStackPairLists().write(0, equipment);
    }

    @Override
    public void send(Player recipient) {
        if(shouldResendPose) {
            send(posePacket, recipient);
//Logger.getGlobal().info("send bone pose");
        }
        if(shouldResendItem) {
            send(equipPacket, recipient);
//Logger.getGlobal().info("send bone item");
        }
    }
}
