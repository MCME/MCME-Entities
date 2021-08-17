package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.ai.goal.GoalPath;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.GoalVirtualEntity;
import com.mcmiddleearth.entities.ai.goal.head.*;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.api.*;
import com.mcmiddleearth.entities.command.argument.*;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class FactoryCommand extends McmeEntitiesCommandHandler {

    public FactoryCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("property", new FactoryPropertyArgument())
                        .then(HelpfulRequiredArgumentBuilder.argument("value", greedyString())
                                .executes(context -> setFactoryValue(context.getSource(),
                                                                     context.getArgument("property", String.class),
                                                                     context.getArgument("value", String.class)))));
        return commandNodeBuilder;
    }

    private int setFactoryValue(McmeCommandSender sender, String property, String value) {
        BukkitCommandSender player = (BukkitCommandSender) sender;
        VirtualEntityFactory factory = player.getEntityFactory();
        switch (property.toLowerCase()) {
            case "clear":
                player.setEntityFactory(new VirtualEntityFactory(
                                        new McmeEntityType(McmeEntityType.CustomEntityType.BAKED_ANIMATION),null));
            case "type":
                McmeEntityType entityType = McmeEntityType.valueOf(value);
                if (entityType != null) {
                    factory.withEntityType(entityType);
                }
                break;
            case "blacklist":
                factory.withBlackList(value.equalsIgnoreCase("true"));
                break;
            case "uniqueid":
                try {
                    factory.withUuid(UUID.fromString(value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse UUID!").create());
                }
                break;
            case "name":
                factory.withName(value);
                break;
            case "datafile":
                factory.withDataFile(value);
                break;
            case "displayname":
                factory.withDisplayName(value);
                break;
            case "displaynameposition":
                try {
                    factory.withDisplayNamePosition(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse display name position!").color(ChatColor.RED).create());
                }
                break;
            case "location":
                try {
                    if(value.equalsIgnoreCase("@p") && (player instanceof RealPlayer)) {
                        factory.useEntityForSpawnLocation((RealPlayer)player);
                    } else {
                        factory.withLocation(parseLocation(((RealPlayer) player).getBukkitPlayer(), value));
                    }
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse location!").color(ChatColor.RED).create());
                }
                break;
            case "movementtype":
                try {
                    factory.withMovementType(MovementType.valueOf(value.toUpperCase()));
//Logger.getGlobal().info("Factory: "+factory);
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse movement type").color(ChatColor.RED).create());
                }
                break;
            case "goaltype":
                try {
                    getOrCreateGoalFactory(factory).withGoalType(GoalType.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse goal type").color(ChatColor.RED).create());
                }
                break;
            case "loop":
                getOrCreateGoalFactory(factory).withLoop(value.equalsIgnoreCase("true"));
                break;
            case "checkpoints":
                getOrCreateGoalFactory(factory).withCheckpoints(player.getSelectedPoints().toArray(new Location[0]));
                break;
            case "headgoal":
                String[] split = value.split(" ");
                int paramA = 10, paramB = 0, paramC = 0;
                try {
                    if(split.length>1) paramA = Integer.parseInt(split[1]);
                    if(split.length>2) paramB = Integer.parseInt(split[2]);
                    if(split.length>3) paramC = Integer.parseInt(split[3]);
                } catch (NumberFormatException ignore) {}
                VirtualEntityGoalFactory goalFactory = getOrCreateGoalFactory(factory);
                try {
                    HeadGoalType headGoalType = HeadGoalType.valueOf(value.toUpperCase());
                    switch(headGoalType) {
                        case LOOK:
                            goalFactory.getHeadGoals().add(new HeadGoalLook(((RealPlayer)player).getBukkitPlayer().getLocation(),null,paramA));
                            break;
                        case WATCH:
                            goalFactory.getHeadGoals().add(new HeadGoalWatch(player.selectedTargetEntity,null, paramA));
                            break;
                        case WAYPOINT_TARGET:
                            goalFactory.getHeadGoals().add(new HeadGoalWaypointTarget(null, paramA));
                            break;
                        case LOCATION_TARGET:
                            goalFactory.getHeadGoals().add(new HeadGoalLocationTarget(null, paramA));
                            break;
                        case ENTITY_TARGET:
                            goalFactory.getHeadGoals().add(new HeadGoalEntityTarget(null, paramA));
                            break;
                        case STARE:
                            goalFactory.getHeadGoals().add(new HeadGoalStare(paramB,paramC, paramA));
                            break;
                    }
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse head goal type!").color(ChatColor.RED).create());
                }
                break;
            case "targetlocation":
                try {
                    getOrCreateGoalFactory(factory).withTargetLocation(parseLocation(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse target location!").color(ChatColor.RED).create());
                }
                break;
            case "targetentity":
                McmeEntity target = EntitiesPlugin.getEntityServer().getEntity(value);
                if(target != null) {
                    getOrCreateGoalFactory(factory).withTargetEntity(target);
                } else {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Target entity not found!").color(ChatColor.RED).create());
                }
                break;
            case "headpitchcenter":
                try {
                    factory.withHeadPitchCenter(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse pitch center!").color(ChatColor.RED).create());
                }
                break;
            case "speechballoonlayout":
                try {
                    split = value.split(" ");
                    factory.withSpeechBalloonLayout(new SpeechBalloonLayout(SpeechBalloonLayout.Position.valueOf(split[0]),
                                                                            SpeechBalloonLayout.Width.valueOf(split[1])));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse speech balloon layout!").color(ChatColor.RED).create());
                }
                break;
            case "mouth":
                try {
                    factory.withMouth(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse mouth position!").color(ChatColor.RED).create());
                }
                break;
            case "manualanimation":
                factory.withManualAnimationControl(value.equalsIgnoreCase("true"));
                break;
            case "headposedelay":
                try {
                    factory.withHeadPoseDelay(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse integer for head pose delay!").color(ChatColor.RED).create());
                }
                break;
            case "viewdistance":
                try {
                    factory.withViewDistance(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse integer for viewDistance!").color(ChatColor.RED).create());
                }
                break;
            case "maxrotationstep":
                try {
                    factory.withMaxRotationStep(Float.parseFloat(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse float for maxRotationStep!").color(ChatColor.RED).create());
                }
                break;
            case "maxrotationstepflight":
                try {
                    factory.withMaxRotationStepFlight(Float.parseFloat(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse float for maxRotationStepFlight!").color(ChatColor.RED).create());
                }
                break;
            case "updateinterval":
                try {
                    factory.withUpdateInterval(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse integer for update interval!").color(ChatColor.RED).create());
                }
                break;
            case "jumpheight":
                try {
                    factory.withJumpHeight(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse integer for jump height!").color(ChatColor.RED).create());
                }
                break;
            case "knockbackbase":
                try {
                    factory.withKnockBackBase(Float.parseFloat(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse float for knockBackBase!").color(ChatColor.RED).create());
                }
                break;
            case "knockbackperdamage":
                try {
                    factory.withKnockBackPerDamage(Float.parseFloat(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse float for knockBackPerDamage!").color(ChatColor.RED).create());
                }
                break;
            default:
                sender.sendMessage(new ComponentBuilder("Property " + property +" could not be found.").color(ChatColor.RED).create());
                return 0;
        }
        sender.sendMessage(new ComponentBuilder(property + " set to " + value + ".").create());
        return 0;
    }

}
