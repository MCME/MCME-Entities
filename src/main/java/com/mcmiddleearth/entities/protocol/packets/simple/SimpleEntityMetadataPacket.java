package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class SimpleEntityMetadataPacket extends AbstractPacket {

    PacketContainer meta;

    WrappedDataWatcher watcher = new WrappedDataWatcher();

    public SimpleEntityMetadataPacket(int entityId) {
        this.meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        this.meta.getIntegers().write(0,entityId);
    }

    public void setSprinting(boolean sprint) {
        if(sprint) {
            setByte(0, Byte.decode("0x08"));
        } else {
            setByte(0,Byte.decode("0x00"));
        }
    }

    protected void setByte(int index, Byte metadata) {
        WrappedDataWatcher.WrappedDataWatcherObject wrappedWatcher = new WrappedDataWatcher
                .WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(wrappedWatcher, metadata, false);
//Logger.getGlobal().info("set byte");
    }

    private void setBoolean(int index, boolean metadata) {
        WrappedDataWatcher.WrappedDataWatcherObject wrappedWatcher = new WrappedDataWatcher
                .WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(wrappedWatcher, metadata, false);
    }

    @Override
    public void update() {
//Logger.getGlobal().info("update");
        meta.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
    }

    @Override
    public void send(Player recipient) {
        /*WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject invisibility = new WrappedDataWatcher
                .WrappedDataWatcherObject(0,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(invisibility, Byte.decode("0x20"),false);
        WrappedDataWatcher.WrappedDataWatcherObject silent = new WrappedDataWatcher
                .WrappedDataWatcherObject(4,WrappedDataWatcher.Registry.get(Boolean.class));
        watcher.setObject(silent, true,false);
        WrappedDataWatcher.WrappedDataWatcherObject base = new WrappedDataWatcher
                .WrappedDataWatcherObject(14,WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(base, Byte.decode("0x08"),false);

        Logger.getGlobal().info("send to : "+recipient);*/
        send(meta, recipient);
    }
}
