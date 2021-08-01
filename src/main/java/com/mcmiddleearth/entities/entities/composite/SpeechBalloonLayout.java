package com.mcmiddleearth.entities.entities.composite;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.util.ConfigurationUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SpeechBalloonLayout {

    private boolean hasBalloon;
    private Material balloonMaterial;

    private Vector baseOffset, layoutOffset;

    private String linePrefix;
    private double linePitch;

    private Integer[] modelDataWide, modelDataNarrow;

    private final Width width;
    private boolean isWide;
    private final int lineLengthNarrow, lineLengthWide;
    private final double widthInBlocksNarrow, widthInBlocksWide;

    private final Position position;

    private int duration;

    private String[] lines;
    private boolean isJson;

    public SpeechBalloonLayout() {
        this(Position.RIGHT, Width.OPTIMAL);
    }

    public SpeechBalloonLayout(Position position, Width width) {
        this.position = position;
        lineLengthNarrow = getConfig().getInt("LineLengthNarrow",15);
        lineLengthWide = getConfig().getInt("LineLengthWide",30);
        widthInBlocksNarrow = getConfig().getDouble("WidthInBlocksNarrow",1);
        widthInBlocksWide = getConfig().getDouble("WidthInBlocksWide",2);
        hasBalloon = getConfig().getBoolean("HasBalloon", true);
        try {
            balloonMaterial = Material.valueOf(getConfig().getString("BalloonMaterial", "WHITE_STAINED_GLASS").toUpperCase());
        } catch (IllegalArgumentException ex) {
            balloonMaterial = Material.WHITE_STAINED_GLASS;
        }
        linePrefix = getConfig().getString("LinePrefix", "");
        linePitch = getConfig().getDouble("LinePitch",0.26);
        modelDataNarrow = getConfig().getIntegerList("CustomModelDataNarrow").toArray(new Integer[0]);
        if(modelDataNarrow.length==0) {
            modelDataNarrow = new Integer[]{1, 2, 3, 4, 5};
        }
        modelDataWide = getConfig().getIntegerList("CustomModelDataWide").toArray(new Integer[0]);
        if(modelDataWide.length==0) {
            modelDataWide = new Integer[]{6, 7, 8, 9, 10};
        }
        this.width = width;
        switch(position) {
            case LEFT:
                baseOffset = ConfigurationUtil.getVector(getConfig(),"PositionLeft", new Vector(-1.5,0.2,0));
                break;
            case RIGHT:
                baseOffset = ConfigurationUtil.getVector(getConfig(),"PositionRight", new Vector(1.5,0.2,0));
                break;
            case TOP:
                baseOffset = ConfigurationUtil.getVector(getConfig(),"PositionTop", new Vector(0,1.2,0));
                break;
        }
Logger.getGlobal().info("baseOffset: "+baseOffset.toString());
        layoutOffset = baseOffset;
    };

    public void layout() {
        // for Width.OPTIMAL determine optimal width
        switch(width) {
            case NARROW: isWide = false; break;
            case WIDE: isWide = true; break;
            case OPTIMAL:
                if(isJson) {
                    isWide = true;
                } else {
                    int maxLineLength = 0;
                    for(String line: lines) {
                        int length = ChatColor.stripColor(line).length();
                        if(length>maxLineLength) maxLineLength = length;
                    }
                    isWide = maxLineLength > lineLengthNarrow && (lines.length > 1 || maxLineLength >= lineLengthNarrow*2);
                }
        }
        if(!isJson) {
            // wrap text to fit with width

            //todo: repeat last color code in new line!

            int lineLength = (isWide ? lineLengthWide : lineLengthNarrow);
            List<String> wrappedLines = new ArrayList<>();
Logger.getGlobal().info("Lines: "+lines.length);
            for (String line : lines) {
                String[] words = line.split(" ");
                if (words.length == 0) {
                    wrappedLines.add("");
                    continue;
                }
                int i = 0;
                while (i < words.length) {
                    String wrappedLine = "";
                    //add first word to a wrapped line
                    if (!canAdd(wrappedLine, words[i], lineLength)) {
                        //split up overly long word
                        while (ChatColor.stripColor(words[i]).length() > lineLength) {
                            wrappedLine = wrappedLine + words[i].substring(0, lineLength);
                            wrappedLines.add(wrappedLine);
                            words[i] = words[i].substring(lineLength);
                            wrappedLine = "";
                        }
                    }
                    wrappedLine = wrappedLine + words[i];
                    i++;
                    //add more words to line
                    while (i < words.length && canAdd(wrappedLine, words[i], lineLength - 1)) { //-1 -> space added between words
                        wrappedLine = wrappedLine + " " + words[i];
                        i++;
                    }
                    //check if next word is overly long, if yes fill up the current line
                    int charactersLeft = lineLength - 1 - ChatColor.stripColor(wrappedLine).length();
                    if (i < words.length && ChatColor.stripColor(words[i]).length() > lineLength && charactersLeft > 5) {
                        wrappedLine = wrappedLine + " " + words[i].substring(0, charactersLeft);
                        words[i] = words[i].substring(charactersLeft);
                    }
                    wrappedLines.add(wrappedLine);
                }
            }
            lines = wrappedLines.toArray(new String[0]);
Logger.getGlobal().info("Wrapped "+lines.length);
            //add Line prefix
            for(int i = 0; i < lines.length; i++) {
                lines[i] = (linePrefix + lines[i]).replace('&','§');
            }
        }
        // set currentOffset to match amount of lines and width
        layoutOffset = new Vector(0,0,baseOffset.getZ());
        switch(position) {
            case RIGHT:
                layoutOffset.setX(baseOffset.getX()+(isWide?widthInBlocksWide/2:widthInBlocksNarrow/2)); break;
            case LEFT:
                layoutOffset.setX(baseOffset.getX()-(isWide?widthInBlocksWide/2:widthInBlocksNarrow/2)); break;
            case TOP:
                layoutOffset.setX(baseOffset.getX());
        }
        layoutOffset.setY(baseOffset.getY()+linePitch*lines.length);
    }

    private boolean canAdd(String wrappedLine, String append, int maxLength) {
        return ChatColor.stripColor(wrappedLine).length()+ChatColor.stripColor(append).length() <= maxLength;
    }

    public Integer getBalloonModelData() {
        int dataIndex = this.lines.length-1;
        Integer[] modelData;
        if(isWide) {
            modelData = modelDataWide;
        } else {
            modelData = modelDataNarrow;
        }
        dataIndex = Math.min(modelData.length-1,dataIndex);
        return modelData[dataIndex];
    }

    public SpeechBalloonLayout withBalloonModelData(Integer[] dataValues, boolean wide) {
        if (dataValues.length > 0) {
            if(wide) {
                modelDataWide = dataValues;
            } else {
                modelDataNarrow = dataValues;
            }
        }
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public SpeechBalloonLayout withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public String[] getLines() {
        return lines;
    }

    public SpeechBalloonLayout withMessage(String message) {
        this.lines = message.split("\\\\");
Logger.getGlobal().info("Lines: "+lines.length);
        isJson = false;
        return this;
    }

    public SpeechBalloonLayout withLines(String[] lines) {
        this.lines = lines;
        isJson = false;
        return this;
    }

    public SpeechBalloonLayout withJson(String[] jsonLines) {
        this.lines = lines;
        isJson = true;
        return this;
    }

    public boolean isJson() {
        return isJson;
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
        return baseOffset;
    }

    public Vector getLayoutOffset() {
        return layoutOffset;
    }

    public SpeechBalloonLayout withOffset(Vector offset) {
        this.baseOffset = offset;
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

    public SpeechBalloonLayout withLinePitch(double linePitch) {
        this.linePitch = linePitch;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public enum Position {
        LEFT, RIGHT, TOP;
    }

    public enum Width {
        NARROW, WIDE, OPTIMAL;
    }

    private ConfigurationSection getConfig() {
        ConfigurationSection result = EntitiesPlugin.getInstance().getConfig().getConfigurationSection("SpeechBalloonLayout");
        if(result==null) {
            result = new MemoryConfiguration();
        }
        return result;
    }

    @Override
    public SpeechBalloonLayout clone() {
        SpeechBalloonLayout clone = new SpeechBalloonLayout(position, width);
        clone.hasBalloon = this.hasBalloon;
        clone.balloonMaterial = this.balloonMaterial;
        clone.baseOffset = this.baseOffset.clone();
        clone.layoutOffset = this.layoutOffset.clone();
        clone.linePitch = this.linePitch;
        clone.linePrefix = this.linePrefix;
        clone.duration = this.duration;
        clone.isWide = this.isWide;
        clone.lines = this.lines;
        clone.isJson = this.isJson;
        clone.modelDataNarrow = this.modelDataNarrow;
        clone.modelDataWide = this.modelDataWide;
        return clone;
    }
}
