package com.mcmiddleearth.entities.protocol.packets.simple.horses;

import com.mcmiddleearth.entities.protocol.EntityMeta;
import com.mcmiddleearth.entities.protocol.packets.simple.SimpleEntityMetadataPacket;

public class SimpleHorseMetadataPacket extends SimpleEntityMetadataPacket {

    public SimpleHorseMetadataPacket(int entityId) {
        super(entityId);
    }

    public void setSaddled(boolean isSaddled) {
        if(isSaddled) {
            setByte(EntityMeta.HORSE_STATUS, Byte.decode("0x04"));
        } else {
            setByte(EntityMeta.HORSE_STATUS, Byte.decode("0x00"));
        }
    }


}
