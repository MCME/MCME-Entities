package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.goal.head.HeadGoalStare;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.util.RotationMatrix;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class GoalJockey extends GoalVirtualEntity {

    private McmeEntity steed;

    private final Vector factoryRelativePosition;
    private Vector relativePosition;

    private Vector velocity;

    public GoalJockey(VirtualEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.factoryRelativePosition = factory.getRelativePosition();
        setSteed(factory.getTargetEntity());
    }

    @Override
    public Vector getDirection() {
        return new Vector(1,0,0);
    }

    @Override
    public void doTick() {
        super.doTick();
        movementSpeed = MovementSpeed.STAND;//steed.getMovementSpeed();
        //getEntity().setLocation(steed.getLocation()
          //      .clone().add(RotationMatrix.fastRotateY(relativePosition,steed.getYaw())));
        setYaw(steed.getYaw());
        velocity = steed.getLocation().clone().add(RotationMatrix.fastRotateY(relativePosition,steed.getYaw()))
                        .subtract(getEntity().getLocation()).toVector();
    }

    public boolean isDirectMovementControl() {
        return true;
    }

    @Override
    public void setDefaultHeadGoal() {
        //clearHeadGoals();
        //addHeadGoal(new HeadGoalStare(0,0));
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

    public McmeEntity getSteed() {
        return steed;
    }

    public void setSteed(McmeEntity steed) {
        this.steed = steed;
        calculateRelativePosition();
        clearHeadGoals();
        Goal steedGoal = steed.getGoal();
        if(steedGoal instanceof GoalVirtualEntity) {
            ((GoalVirtualEntity)steedGoal).getHeadGoals().forEach(this::addHeadGoal);
        }
    }

    private void calculateRelativePosition() {
        Vector  saddle;
        Vector shift;
        if(steed instanceof VirtualEntity && ((VirtualEntity)steed).getSaddle() != null) {
            saddle = ((VirtualEntity)steed).getSaddle().clone();
        } else {
            saddle = new Vector(0,1.8,0);
        }
        shift = saddle.clone();
        if(getEntity().getSitPoint()!=null) {
            shift.subtract(getEntity().getSitPoint());
        }
        if(factoryRelativePosition!=null) {
            shift.add(factoryRelativePosition);
        }
        this.relativePosition = shift;
        Logger.getGlobal().info("\nrelative factory: "+(factoryRelativePosition==null?"null":factoryRelativePosition)
                + "\nsaddle: "+saddle
                +"\nsit point: "+(getEntity().getSitPoint()==null?"null":getEntity().getSitPoint())
                + "\nrelative position: "+relativePosition);
    }

}
