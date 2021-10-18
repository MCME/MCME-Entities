package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoalStare;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.util.RotationMatrix;
import org.bukkit.util.Vector;

public class GoalJockey extends GoalVirtualEntity {

    private McmeEntity steed;

    private Vector relativePosition;

    private Vector velocity;

    public GoalJockey(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.steed = factory.getTargetEntity();
        this.relativePosition = factory.getRelativePosition();
    }

    @Override
    public Vector getDirection() {
        return new Vector(1,0,0);
    }

    @Override
    public void doTick() {
        super.doTick();
        movementSpeed = steed.getMovementSpeed();
        //getEntity().setLocation(steed.getLocation()
          //      .clone().add(RotationMatrix.fastRotateY(relativePosition,steed.getYaw())));
        steed.getYaw();
        velocity = steed.getLocation().clone().add(RotationMatrix.fastRotateY(relativePosition,steed.getYaw()))
                        .subtract(getEntity().getLocation()).toVector();
    }

    public boolean isDirectMovementControl() {
        return true;
    }

    @Override
    public void setDefaultHeadGoal() {
        clearHeadGoals();
        addHeadGoal(new HeadGoalStare(0,0));
    }

    @Override
    public void activate() {
        getEntity().setMovementType(MovementType.RIDING);
    }

    @Override
    public void deactivate() {
        getEntity().setMovementType(MovementType.UPRIGHT);
    }

    @Override
    public VirtualEntityGoalFactory getFactory() {
        return super.getFactory()
                    .withTargetEntity(steed)
                    .withRelativePosition(relativePosition);
    }

    @Override
    public Vector getVelocity() {
        return velocity;
    }

}
