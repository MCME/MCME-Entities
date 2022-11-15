package com.mcmiddleearth.entities.protocol.packets.simple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.protocol.packets.AbstractMovePacket;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SimpleEntityMovePacket extends AbstractMovePacket {

    private final PacketContainer move;
    private final PacketContainer moveLook;
    //private final PacketContainer look;
    //private final PacketContainer stand;
    private final PacketContainer head;

    private final McmeEntity entity;

    private boolean hasMoveUpdate = true;
    private boolean hasLookUpdate = true;
    private boolean hasHeadUpdate = true;
    private boolean shouldResendMove = false;
    private boolean shouldResendMoveLook = false;
    private boolean shouldResendHead = false;

    public SimpleEntityMovePacket(McmeEntity entity) {
        this.entity = entity;
        moveLook = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        moveLook.getIntegers().write(0,entity.getEntityId());

        move = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
        move.getIntegers().write(0, entity.getEntityId());

        /*look = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        look.getIntegers().write(0, entity.getEntityId());

        stand = new PacketContainer(PacketType.Play.Server.ENTITY);
        stand.getIntegers().write(0, entity.getEntityId());*/

        head = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        head.getIntegers().write(0, entity.getEntityId());
    }

    @Override
    public void update() {
        // Reset resend flags - previous update should be sent by now
        shouldResendMove = false;
        shouldResendMoveLook = false;
        shouldResendHead = false;

        if (hasLookUpdate) {
            Vector dir = getShift();
            byte yaw = getAngle(entity.getYaw());
            byte pitch = getAngle(entity.getHeadPitch());

            moveLook.getShorts()
                    .write(0, (short) dir.getBlockX())
                    .write(1, (short) dir.getBlockY())
                    .write(2, (short) dir.getBlockZ());
            moveLook.getBytes()
                    .write(0, yaw)
                    .write(1, pitch);
            moveLook.getBooleans().write(0, entity.onGround());

            hasLookUpdate = false;
            shouldResendMoveLook = true;
        } else if (hasMoveUpdate) {
            // moveLook already contains this data - no point updating this packet as it will require another update before being sent again
            Vector dir = getShift();

            move.getShorts()
                    .write(0, (short) dir.getBlockX())
                    .write(1, (short) dir.getBlockY())
                    .write(2, (short) dir.getBlockZ());
            move.getBooleans().write(0, entity.onGround());

            hasMoveUpdate = false;
            shouldResendMove = true;
        }

        if (hasHeadUpdate) {
            head.getBytes().write(0, getAngle(entity.getHeadYaw()));
            hasHeadUpdate = false;
            shouldResendHead = true;
        }
    }

    @Override
    public void send(Player recipient) {
        // move packet is redundant if moveLook was already sent - it contains the same data
        if (shouldResendMoveLook) {
            send(moveLook, recipient);
        } else if (shouldResendMove) {
            send(move, recipient);
        }

        if (shouldResendHead) {
            send(head, recipient);
        }
    }

    private Vector getShift() {
/*if(entity.getName().equals("bone4")) {
    Logger.getGlobal().info("Packet velo: "+entity.getVelocity());
}*/
        return entity.getVelocity().clone().multiply(32*128);
    }

    private byte getAngle(float bukkitAngle) {
        return (byte)(bukkitAngle*256/360);
    }

    @Override
    public void markMovementDirty(MoveType moveType) {
        hasMoveUpdate |= moveType.updatesMove();
        hasLookUpdate |= moveType.updatesLook();
        hasHeadUpdate |= moveType.updatesHead();
    }
}
