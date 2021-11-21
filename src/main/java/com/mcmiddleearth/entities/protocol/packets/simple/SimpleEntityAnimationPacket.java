package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.protocol.packets.AbstractPacket;
import org.bukkit.entity.Player;

public class SimpleEntityAnimationPacket extends AbstractPacket {

    PacketContainer animation;

    public SimpleEntityAnimationPacket(int entityId) {
        animation = new PacketContainer(PacketType.Play.Server.ANIMATION);
        animation.getIntegers().write(0,entityId);
    }

    public void setAnimation(SimpleEntityAnimationPacket.AnimationType type) {
        animation.getIntegers().write(1,type.animationId);
    }

    @Override
    public void send(Player recipient) {
        send(animation,recipient);
//Logger.getGlobal().info("Recipient: "+recipient.getName()+" "+animation.getIntegers().read(1));
    }

    public enum AnimationType {
        SWING_MAIN_ARM     (0),
        TAKE_DAMAGE         (1),
        LEAVE_BED           (2),
        SWING_OFF_ARM       (3),
        CRITICAL            (4),
        CRITICAL_MAGIC      (5);

        int animationId;

        AnimationType(int animationId) {
            this.animationId = animationId;
        }

        public int getAnimationId() {
            return animationId;
        }
    }
}
