package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.composite.Bone;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BoneMetaPacket extends AbstractPacket {

    protected PacketContainer posePacket;
    protected PacketContainer equipPacket;

    protected final Bone bone;

    private boolean hasPoseUpdate, hasItemUpdate;

    public BoneMetaPacket(Bone bone) {
        this.bone = bone;
        posePacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        posePacket.getIntegers().write(0,bone.getEntityId());

        equipPacket = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipPacket.getIntegers().write(0,bone.getEntityId());

        update();
    }

    @Override
    public void update() {
        if(bone.isHasHeadRotationUpdate()) {
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            writeHeadPose(watcher);
            posePacket.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
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

    protected void writeHeadPose(WrappedDataWatcher watcher) {
        WrappedDataWatcher.WrappedDataWatcherObject state = new WrappedDataWatcher
                .WrappedDataWatcherObject(15, WrappedDataWatcher.Registry.getVectorSerializer());
        watcher.setObject(state, new Vector3F((float)bone.getHeadPose().getX(),
                                              (float)bone.getHeadPose().getY(),
                                              (float)bone.getHeadPose().getZ()), false);
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
