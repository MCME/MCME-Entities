package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.EntitiesPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class SpeechBalloonLayout {

    private boolean hasBalloon;
    private Material balloonMaterial;

    private Vector offset;

    private String linePrefix;
    private double linePitch;

    private Integer[] modelData;

    private Width width;

    public SpeechBalloonLayout() {
        this(Position.RIGHT, Width.OPTIMAL);
    }

    public SpeechBalloonLayout(Position position, Width width) {
        hasBalloon = getConfig().getBoolean("hasBalloon", false);
        try {
            balloonMaterial = Material.valueOf(getConfig().getString("balloonMaterial", "WHITE_STAINED_GLASS").toUpperCase());
        } catch (IllegalArgumentException ex) {
            balloonMaterial = Material.WHITE_STAINED_GLASS;
        }
        linePrefix = getConfig().getString("linePrefix", "");
        linePitch = getConfig().getDouble("linePitch",0.3);
        modelData = getConfig().getIntegerList("customModelData").toArray(new Integer[0]);
        if(modelData.length==0) {
            modelData = new Integer[]{1, 2, 3, 4, 5};
        }
        this.width = width;
    };

    public String[] layout(String[] lines) {


        getLinePrefix();
    }

    public Integer getBalloonModelData(int lines) {
        lines = Math.max(0,Math.min(modelData.length,line));
        return modelData[lines];
    }

    public SpeechBalloonLayout withBalloonModelData(int[] dataValues) {
        if(dataValues.length>0) {
            modelData = dataValues;
        }
        return this;
    }

    public boolean hasBalloon() {
        return hasBalloon;
    }

    public SpeechBalloonLayout withBalloon(boolean hasBalloon) {
        this.hasBalloon = hasBalloon;
        return this;
    }

    public Material getBalloonMaterial() {
        return balloonMaterial;
    }

    public SpeechBalloonLayout withBalloonMaterial(Material balloonMaterial) {
        this.balloonMaterial = balloonMaterial;
        return this;
    }

    public Vector getOffset() {
        return offset;
    }

    public SpeechBalloonLayout withOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    public String getLinePrefix() {
        return linePrefix;
    }

    public SpeechBalloonLayout withLinePrefix(String linePrefix) {
        this.linePrefix = linePrefix;
        return this;
    }

    public double getLinePitch() {
        return linePitch;
    }

    public SpeechBalloonLayout withLinePitch(float linePitch) {
        this.linePitch = linePitch;
        return this;
    }

    public enum Position {
        LEFT, RIGHT, CENTER;
    }

    public enum Width {
        NARROW, WIDE, OPTIMAL;
    }

    private ConfigurationSection getConfig() {
        return EntitiesPlugin.getInstance().getConfig().getConfigurationSection("SpeechBalloonLayout");
    }
}
