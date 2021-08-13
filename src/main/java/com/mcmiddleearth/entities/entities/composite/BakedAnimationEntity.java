package com.mcmiddleearth.entities.entities.composite;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementSpeed;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.animation.BakedAnimation;
import com.mcmiddleearth.entities.entities.composite.animation.BakedAnimationTree;
import com.mcmiddleearth.entities.entities.composite.animation.BakedAnimationType;
import com.mcmiddleearth.entities.events.events.virtual.composite.BakedAnimationEntityAnimationChangedEvent;
import com.mcmiddleearth.entities.events.events.virtual.composite.BakedAnimationEntityStateChangedEvent;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.Material;
import org.graalvm.compiler.lir.aarch64.AArch64Move;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BakedAnimationEntity extends CompositeEntity {

    //private final Map<String, BakedAnimation> animations = new HashMap<>();

    private final BakedAnimationTree animationTree = new BakedAnimationTree(null);

    private final Map<String, Integer> states = new HashMap<>();

    private BakedAnimation currentAnimation;

    private int currentState;

    private boolean manualAnimationControl = false;

    private MovementSpeed movementSpeedAnimation;
    private int startMovementCounter, stopMovementCounter;

    private String animationFileName;

    public BakedAnimationEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
//Logger.getGlobal().info("Baked Animation Get location "+getLocation());
        manualAnimationControl = factory.getManualAnimationControl();
        movementSpeedAnimation = getMovementSpeed();
        animationFileName = factory.getDataFile();
        File animationFile = new File(EntitiesPlugin.getAnimationFolder(), animationFileName+".json");
        try (FileReader reader = new FileReader(animationFile)) {
//long start = System.currentTimeMillis();
            JsonObject data = new JsonParser().parse(reader).getAsJsonObject();
//Logger.getGlobal().info("File loading: "+(System.currentTimeMillis()-start));
            JsonObject modelData = data.get("model").getAsJsonObject();
            Material itemMaterial = Material.valueOf(modelData.get("head_item").getAsString().toUpperCase());
            JsonObject animationData = data.get("animations").getAsJsonObject();
//start = System.currentTimeMillis();
            animationData.entrySet().forEach(entry -> {
                String[] split;
                if(entry.getKey().contains(factory.getDataFile()+".")) {
                    split = entry.getKey().split(factory.getDataFile() + "\\.");
                } else {
                    split = entry.getKey().split("animations\\.");
//Logger.getGlobal().info("Length: "+split.length);
                }
                String animationKey;
                if(split.length>1) {
                    animationKey = split[1];
                } else {
//Logger.getGlobal().info("DataFile: "+factory.getDataFile());
                    animationKey = entry.getKey();
                }
//Logger.getGlobal().info("AnimationKey: "+animationKey);
                animationTree.addAnimation(animationKey, BakedAnimation.loadAnimation(entry.getValue().getAsJsonObject(),
                        itemMaterial, this, animationKey));
            });
//Logger.getGlobal().info("Animation loading: "+(System.currentTimeMillis()-start));
        } catch (IOException | JsonParseException | IllegalStateException e) {
            throw new InvalidDataException("Data file '"+factory.getDataFile()+"' doesn't exist or does not contain valid animation data.");
        }
//animationTree.debug();
        createPackets();
    }

    public static List<String> getDataFiles() {
        return Arrays.stream(EntitiesPlugin.getAnimationFolder().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        })).map(file -> file.getName().substring(0,file.getName().lastIndexOf('.'))).collect(Collectors.toList());
    }

    @Override
    public void doTick() {
        if(movementSpeedAnimation.equals(MovementSpeed.STAND)
                && (getMovementSpeed().equals(MovementSpeed.WALK) || getMovementSpeed().equals(MovementSpeed.SPRINT)
                                                                  || getMovementSpeed().equals(MovementSpeed.SLOW))) {
            startMovementCounter++;
            if(startMovementCounter>0) {
                movementSpeedAnimation = getMovementSpeed();
                startMovementCounter = 0;
            }
        } else if(getMovementSpeed().equals(MovementSpeed.STAND)
                && (movementSpeedAnimation.equals(MovementSpeed.WALK) || movementSpeedAnimation.equals(MovementSpeed.SPRINT)
                || movementSpeedAnimation.equals(MovementSpeed.SLOW))) {
            stopMovementCounter++;
            if(stopMovementCounter>3) {
                movementSpeedAnimation = getMovementSpeed();
                stopMovementCounter = 0;
            }
        } else {
            startMovementCounter = 0;
            stopMovementCounter = 0;
        }
        if(!manualAnimationControl) {
            BakedAnimation expected = animationTree.getAnimation(this);
            if(currentAnimation!=expected) {
//Logger.getGlobal().info("Switch: "+(expected == null?"none":expected.getName()));
                currentAnimation = expected;
                if(currentAnimation!=null)
                    currentAnimation.reset();
            }
        }
        if(currentAnimation!=null) {
            if (currentAnimation.isFinished()) {
                if (currentAnimation.getType().equals(BakedAnimationType.CHAIN)) {
                    currentAnimation = animationTree.getAnimation(currentAnimation.getNext());
                    currentAnimation.reset();
                }
            }
            currentAnimation.doTick();
        }
        super.doTick();
    }

    public void setAnimation(String name) {
        BakedAnimationEntityAnimationChangedEvent event = new BakedAnimationEntityAnimationChangedEvent(this, name);
        EntitiesPlugin.getEntityServer().handleEvent(event);
        if(!event.isCancelled()) {
            BakedAnimation newAnim = animationTree.getAnimation(event.getNextAnimationKey());
            if (newAnim != null) {
                currentAnimation = newAnim;
                currentAnimation.reset();
            } else {
                currentAnimation = null;
            }
        }
    }

    public void setState(String state) {
        BakedAnimationEntityStateChangedEvent event = new BakedAnimationEntityStateChangedEvent(this, state);
        EntitiesPlugin.getEntityServer().handleEvent(event);
        if(!event.isCancelled()) {
            Integer stateId = states.get(event.getNextState());
            if (stateId != null) {
                currentState = stateId;
            }
        }
    }

    public int getState() {
        return currentState;
    }

    public Map<String, Integer> getStates() {
        return states;
    }

    public void setAnimationFrame(String animation, int frameIndex) {
//Logger.getGlobal().info("set Animation Frame "+animation + " "+ frameIndex);
        currentAnimation = null;
        BakedAnimation anim = animationTree.getAnimation(animation);
        if (anim != null) {
//Logger.getGlobal().info("Apply Frame: "+ anim);
            anim.applyFrame(frameIndex);
        }
    }

    public boolean isManualAnimationControl() {
        return manualAnimationControl;
    }

    public void setManualAnimationControl(boolean manualAnimationControl) {
        this.manualAnimationControl = manualAnimationControl;
    }

    public List<String> getAnimations() {
        return animationTree.getAnimationKeys();
    }

    public MovementSpeed getMovementSpeedAnimation() {
        return movementSpeedAnimation;
    }

    @Override
    public VirtualEntityFactory getFactory() {
        VirtualEntityFactory factory = super.getFactory()
                .withDataFile(animationFileName)
                .withManualAnimationControl(manualAnimationControl);
        return factory;
    }

}
