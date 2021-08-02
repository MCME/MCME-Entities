package com.mcmiddleearth.entities.entities.composite;

import com.google.common.base.Joiner;
import com.mcmiddleearth.entities.ai.movement.MovementType;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class SpeechBalloon extends CompositeEntity {

    private final McmeEntity speaker;

    private final Player viewer;

    private final static double armorStandNameHeight = 2.3;

    public SpeechBalloon(int entityId, McmeEntity speaker, Player viewer, SpeechBalloonLayout layout) {
        super(entityId, new McmeEntityType(McmeEntityType.CustomEntityType.SPEECH_BALLOON),speaker.getLocation());
        this.speaker = speaker;
        layout.layout();
        this.viewer = viewer;
Logger.getGlobal().info("Create Speech Balloon, viewer: "+viewer.getName()+" message: "+ Joiner.on("-").join(layout.getLines()));
        getWhiteList().add(viewer);
        //create bones
        if(layout.hasBalloon()) {
            ItemStack item = new ItemStack(layout.getBalloonMaterial()/*Material.LIME_STAINED_GLASS*/);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(layout.getBalloonModelData()/*1*/);
Logger.getGlobal().info("BAlloon data: "+layout.getBalloonModelData());
            item.setItemMeta(meta);
            Bone bone = new Balloon("balloon", this, new EulerAngle(0,
                                                                   (layout.getPosition().equals(SpeechBalloonLayout.Position.LEFT)?180:0),
                                                                 0),
                    new Vector(speaker.getMouth().getX()+layout.getLayoutOffset().getX(),
                               speaker.getMouth().getY()+layout.getLayoutOffset().getY()- armorStandNameHeight,
                               speaker.getMouth().getZ()+layout.getLayoutOffset().getZ()-0.125), item,viewer);
            getBones().add(bone);
        }
        for(int i = 0; i < layout.getLines().length; i++) {
            String line = layout.getLines()[i];
            Bone bone = new Bone("line"+i, this, new EulerAngle(0, 0, 0),
                    new Vector(speaker.getMouth().getX()+layout.getLayoutOffset().getX()/*1*/,
                               speaker.getMouth().getY()+layout.getLayoutOffset().getY()- armorStandNameHeight/*0.2*/
                                       - layout.getLinePitch()/*0.26*/ * i,
                               speaker.getMouth().getZ()+layout.getLayoutOffset().getZ()/*0*/), null, false);
            this.getBones().add(bone);
Logger.getGlobal().info("Bone display name: "+line);
            bone.setDisplayName(line);//"{\"text\": \"foo\",\"bold\": \"true\"}");
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
