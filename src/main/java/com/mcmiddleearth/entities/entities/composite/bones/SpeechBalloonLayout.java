package com.mcmiddleearth.entities.entities.composite.bones;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.util.ConfigurationUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SpeechBalloonLayout {

    private boolean hasBalloon;
    private Material balloonMaterial;

    private Vector baseOffset, layoutOffset;

    private String linePrefix;
    private double linePitch;

    private Integer[] modelDataWide, modelDataNarrow;

    private Width width;
    private boolean isWide;
    private int lineLengthNarrow, lineLengthWide;
    private double widthInBlocksNarrow, widthInBlocksWide;

    private Position position;

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
//Logger.getGlobal().info("Lines: "+lines.length);
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
//Logger.getGlobal().info("Wrapped "+lines.length);
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

    public SpeechBalloonLayout withPosition(Position position) {
        this.position = position;
        return this;
    }

    public Width getWidth() {
        return width;
    }

    public SpeechBalloonLayout withWidth(Width width) {
        this.width = width;
        return this;
    }

    public int getLineLengthNarrow() {
        return lineLengthNarrow;
    }

    public SpeechBalloonLayout withLineLengthNarrow(int lineLengthNarrow) {
        this.lineLengthNarrow = lineLengthNarrow;
        return this;
    }

    public int getLineLengthWide() {
        return lineLengthWide;
    }

    public SpeechBalloonLayout withLineLengthWide(int lineLengthWide) {
        this.lineLengthWide = lineLengthWide;
        return this;
    }

    public double getWidthInBlocksNarrow() {
        return widthInBlocksNarrow;
    }

    public SpeechBalloonLayout withWidthInBlocksNarrow(double widthInBlocksNarrow) {
        this.widthInBlocksNarrow = widthInBlocksNarrow;
        return this;
    }

    public double getWidthInBlocksWide() {
        return widthInBlocksWide;
    }

    public SpeechBalloonLayout withWidthInBlocksWide(double widthInBlocksWide) {
        this.widthInBlocksWide = widthInBlocksWide;
        return this;
    }

    public Vector getBaseOffset() {
        return baseOffset;
    }

    public SpeechBalloonLayout withBaseOffset(Vector baseOffset) {
        this.baseOffset = baseOffset;
        return this;
    }

    public Integer[] getModelDataWide() {
        return modelDataWide;
    }

    public Integer[] getModelDataNarrow() {
        return modelDataNarrow;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpeechBalloonLayout that = (SpeechBalloonLayout) o;
        return hasBalloon == that.hasBalloon &&
                Double.compare(that.linePitch, linePitch) == 0 &&
                //isWide == that.isWide &&
                lineLengthNarrow == that.lineLengthNarrow &&
                lineLengthWide == that.lineLengthWide &&
                Double.compare(that.widthInBlocksNarrow, widthInBlocksNarrow) == 0 &&
                Double.compare(that.widthInBlocksWide, widthInBlocksWide) == 0 &&
                balloonMaterial == that.balloonMaterial &&
                Objects.equals(baseOffset, that.baseOffset) &&
                Objects.equals(linePrefix, that.linePrefix) &&
                Arrays.equals(modelDataWide, that.modelDataWide) &&
                Arrays.equals(modelDataNarrow, that.modelDataNarrow) &&
                width == that.width &&
                position == that.position;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hasBalloon, balloonMaterial, baseOffset, linePrefix, linePitch, width, isWide, lineLengthNarrow, lineLengthWide, widthInBlocksNarrow, widthInBlocksWide, position);
        result = 31 * result + Arrays.hashCode(modelDataWide);
        result = 31 * result + Arrays.hashCode(modelDataNarrow);
        return result;
    }
}
