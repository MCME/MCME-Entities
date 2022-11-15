package com.mcmiddleearth.entities.protocol.packets;

import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.util.Vector;

public abstract class AbstractMovePacket extends AbstractPacket {
    /**
     * Notifies the packet of a certain part of their movement state being dirty, meaning it should be updated in a future update.
     */
    public abstract void markMovementDirty(MoveType moveType);

    public enum MoveType {
        STAND(false, false, false),
        MOVE(true, false, false),
        MOVE_LOOK(true, true, false),
        LOOK(false, true, false),
        HEAD(false, false, true),
        ;

        private final boolean updatesMove;
        private final boolean updatesLook;
        private final boolean updatesHead;

        private MoveType(boolean updatesMove, boolean updatesLook, boolean updatesHead) {
            this.updatesMove = updatesMove;
            this.updatesLook = updatesLook;
            this.updatesHead = updatesHead;
        }

        public boolean updatesMove() {
            return updatesMove;
        }

        public boolean updatesLook() {
            return updatesLook;
        }

        public boolean updatesHead() {
            return updatesHead;
        }

        public static MoveType getEntityMoveType(McmeEntity entity) {
            Vector velocity = entity.getVelocity();
            if(velocity.getX() == 0 && velocity.getY() == 0 && velocity.getZ() == 0) {
                if(entity.hasLookUpdate() || entity.hasRotationUpdate()) {
                    return MoveType.LOOK;
                } else {
                    return MoveType.STAND;
                }
            } else {
                if(entity.hasLookUpdate() || entity.hasRotationUpdate()) {
                    return MoveType.MOVE_LOOK;
                } else {
                    return MoveType.MOVE;
                }
            }
        }
    }
}
