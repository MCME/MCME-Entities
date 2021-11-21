package com.mcmiddleearth.entities.protocol.packets.simple.horses;

import com.mcmiddleearth.entities.protocol.packets.simple.SimpleEntityMetadataPacket;

public class SimpleHorseMetadataPacket extends SimpleEntityMetadataPacket {

    public SimpleHorseMetadataPacket(int entityId) {
        super(entityId);
    }

    public void setSaddled(boolean isSaddled) {
        if(isSaddled) {
            setByte(16, Byte.decode("0x04"));
        } else {
            setByte(16, Byte.decode("0x00"));
        }
    }


}
