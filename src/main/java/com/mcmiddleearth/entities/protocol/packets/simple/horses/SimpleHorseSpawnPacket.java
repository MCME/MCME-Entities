package com.mcmiddleearth.entities.protocol.packets.simple.horses;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.mcmiddleearth.entities.entities.simple.SimpleHorse;
import com.mcmiddleearth.entities.protocol.EntityMeta;
import com.mcmiddleearth.entities.protocol.packets.simple.SimpleLivingEntitySpawnPacket;
import org.bukkit.entity.Player;

public class SimpleHorseSpawnPacket extends SimpleLivingEntitySpawnPacket {

    private final PacketContainer meta;

    private final SimpleHorse horse;


    public SimpleHorseSpawnPacket(SimpleHorse horse) {
        super(horse);
        this.horse = horse;
        meta = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        this.meta.getIntegers().write(0,horse.getEntityId());
        updateMeta();
    }

    private void updateMeta() {
        Byte saddled;
        if(horse.isSaddled()) {
            saddled = Byte.decode("0x04");
        } else {
            saddled = Byte.decode("0x00");
        }
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.WrappedDataWatcherObject wrappedWatcher = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.HORSE_STATUS, WrappedDataWatcher.Registry.get(Byte.class));
        watcher.setObject(wrappedWatcher, saddled, false);
        wrappedWatcher = new WrappedDataWatcher
                .WrappedDataWatcherObject(EntityMeta.HORSE_VARIANT, WrappedDataWatcher.Registry.get(Integer.class));
        watcher.setObject(wrappedWatcher, getStyleAndColorInt(), false);
        meta.getWatchableCollectionModifier().write(0,watcher.getWatchableObjects());
    }

    private Integer getStyleAndColorInt() {
        int color=0,style=0;
        switch(horse.getColor()) {
            case WHITE:
                color = 0;
                break;
            case CREAMY:
                color = 1;
                break;
            case GRAY:
                color = 5;//2
                break;
            case CHESTNUT:
                color = 2;//3
                break;
            case BROWN:
                color = 3;//4
                break;
            case DARK_BROWN:
                color = 6;//5
                break;
            case BLACK:
                color = 4;//6
                break;
        }
        // doesn't work
        switch(horse.getStyle()) {
            case NONE:
                style = 0;
                break;
            case WHITE:
                style = 8;
                break;
            case WHITE_DOTS:
                style = 16;
                break;
            case WHITEFIELD:
                style = 24;
                break;
            case BLACK_DOTS:
                style = 32;
                break;
        }
        return style+color;
    }

    @Override
    public void update() {
        super.update();
        if(horse != null) updateMeta();
    }

    @Override
    public void send(Player recipient) {
        super.send(recipient);
        send(meta,recipient);
    }
}
