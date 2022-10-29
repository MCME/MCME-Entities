package com.mcmiddleearth.entities.entities.simple;

import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.simple.horses.SimpleHorseMetadataPacket;
import com.mcmiddleearth.entities.protocol.packets.simple.horses.SimpleHorseSpawnPacket;
import org.bukkit.entity.Horse;

public class SimpleHorse extends SimpleLivingEntity {

    private boolean saddled;

    private final Horse.Color color;
    private final Horse.Style style;

    public SimpleHorse(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
        saddled = factory.isSaddled();
        color = factory.getHorseColor();
        style = factory.getHorseStyle();
        spawnPacket = new SimpleHorseSpawnPacket(this);
        metadataPacket = new SimpleHorseMetadataPacket(entityId);
    }

    public void setSaddled(boolean isSaddled) {
        saddled = isSaddled;
        ((SimpleHorseMetadataPacket)metadataPacket).setSaddled(isSaddled);
        metadataPacket.update();
        spawnPacketDirty = true;
        getViewers().forEach(viewer->metadataPacket.send(viewer));
    }

    public boolean isSaddled() {
        return saddled;
    }

    public Horse.Color getColor() {
        return color;
    }

    public Horse.Style getStyle() {
        return style;
    }

    @Override
    public VirtualEntityFactory getFactory() {
        return super.getFactory().withHorseColor(color)
                                 .withHorseStyle(style)
                                 .withSaddled(saddled);
    }
}
