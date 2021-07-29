package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.logging.Logger;

public class SpeechBalloon extends CompositeEntity {

    private McmeEntity speaker;

    private String[] lines;

    private Player viewer;

    public SpeechBalloon(int entityId, McmeEntity speaker, String[] lines, Player viewer) throws InvalidLocationException {
        super(entityId, new McmeEntityType(McmeEntityType.CustomEntityType.SPEECH_BALLOON),speaker.getLocation());
        this.speaker = speaker;
        this.lines = lines;
        this.viewer = viewer;
Logger.getGlobal().info("Create Speech Balloon, viewer: "+viewer.getName()+" message: "+ Arrays.toString(lines));
        getWhiteList().add(viewer);
        //create bones
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Bone bone = new Bone("line"+i, this, new EulerAngle(0, 0, 0),
                    new Vector(1, 0.2 - 0.26 * i, 0), null);
            this.getBones().add(bone);
            bone.setDisplayName("{\"text\": \"foo\",\"bold\": \"true\"}");
        }
        createPackets();
    }

    @Override
    public void doTick() {
//Logger.getGlobal().info("Set balloon loc: "+speaker.getLocation());
        setLocation(speaker.getLocation());
        setRotation(speaker.getLocation().clone().setDirection(viewer.getLocation().toVector()
                                         .subtract(speaker.getLocation().toVector())).getYaw());
        super.doTick();
    }

    @Override
    public void setMovementType(MovementType movementType) {
    }



}
