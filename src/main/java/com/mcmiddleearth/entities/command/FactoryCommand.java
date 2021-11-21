package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.head.*;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.command.argument.FactoryPropertyArgument;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class FactoryCommand extends McmeEntitiesCommandHandler {

    public FactoryCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .executes(context -> setFactoryValue(context.getSource(),"show",""))
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
            case "show":
                showFactory(player);
                break;
            case "clear":
                player.setEntityFactory(new VirtualEntityFactory(
                                        new McmeEntityType(McmeEntityType.CustomEntityType.BAKED_ANIMATION),null));
                break;
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
                        factory.withEntityForSpawnLocation((RealPlayer)player);
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
            case "relative_position":
                getOrCreateGoalFactory(factory).withRelativePosition(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
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
            case "saddlepoint":
                try {
                    factory.withSaddlePoint(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse saddle position!").color(ChatColor.RED).create());
                }
                break;
            case "sitpoint":
                try {
                    factory.withSitPoint(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse sit point!").color(ChatColor.RED).create());
                }
                break;
            case "attackpoint":
                try {
                    factory.withAttackPoint(parseVector(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse attack point!").color(ChatColor.RED).create());
                }
                break;
            case "flightlevel":
                goalFactory = getOrCreateGoalFactory(factory);
                try {
                    goalFactory.withFlightLevel(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse double for flight level!").color(ChatColor.RED).create());
                }
                break;
            case "attackpitch":
                goalFactory = getOrCreateGoalFactory(factory);
                try {
                    goalFactory.withAttackPitch(Float.parseFloat(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse double for attack pitch!").color(ChatColor.RED).create());
                }
                break;
            case "dive":
                goalFactory = getOrCreateGoalFactory(factory);
                try {
                    goalFactory.withDive(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse double for dive!").color(ChatColor.RED).create());
                }
                break;
            case "color":
                try {
                    factory.withHorseColor(Horse.Color.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse horse color!").color(ChatColor.RED).create());
                }
                break;
            case "style":
                try {
                    factory.withHorseStyle(Horse.Style.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse horse style!").color(ChatColor.RED).create());
                }
                break;
            case "saddle":
                factory.withSaddled(value.equalsIgnoreCase("true"));
                break;
            case "dev":
                switch(value) {
                    case "1":
                        factory.withViewDistance(300);
                        factory.withMovementType(MovementType.FLYING);
                        factory.withMaxRotationStepFlight(1);
                        factory.withAttackPoint(new Vector(0,-6,20));
                        getOrCreateGoalFactory(factory).withDive(0.4f);
                        getOrCreateGoalFactory(factory).withFlightLevel(40);
                        getOrCreateGoalFactory(factory).withAttackPitch(32.5f);
                        factory.withAttribute(Attribute.GENERIC_ATTACK_SPEED,0.4);
                        factory.withAttribute(Attribute.GENERIC_FLYING_SPEED,0.6);
        //AttributeInstance attackSpeed = factory.getAttributes().get(Attribute.GENERIC_ATTACK_SPEED);
        //if(attackSpeed!=null) Logger.getGlobal().info("Vfactory command: Attack speed: "+attackSpeed.getBaseValue()+" -> "+attackSpeed.getValue());
                        break;
                    case "2":
                        factory.withViewDistance(300);
                        factory.withMovementType(MovementType.FLYING);
                        factory.withMaxRotationStepFlight(0.7f);
                        factory.withAttackPoint(new Vector(0,-15,40));
                        getOrCreateGoalFactory(factory).withDive(1);
                        getOrCreateGoalFactory(factory).withFlightLevel(70);
                        getOrCreateGoalFactory(factory).withAttackPitch(30);
                        factory.withAttribute(Attribute.GENERIC_ATTACK_SPEED,0.4);
                        factory.withAttribute(Attribute.GENERIC_FLYING_SPEED,0.7);
                        break;
                    default:
                }
                break;
            default:
                sender.sendMessage(new ComponentBuilder("Property " + property +" could not be changed.").color(ChatColor.RED).create());
                return 0;
        }
        sender.sendMessage(new ComponentBuilder(property + " set to " + value + ".").create());
        return 0;
    }

    private void showFactory(BukkitCommandSender player) {
        VirtualEntityFactory factory = player.getEntityFactory();
        VirtualEntityGoalFactory goalFactory = factory.getGoalFactory();
        player.sendMessage(new ComponentBuilder("Entity Factory Settings:").create());
        player.sendMessage(new ComponentBuilder("Entity type: ").append(factory.getType().name()).create());
        player.sendMessage(new ComponentBuilder("Movement type: ").append(factory.getMovementType().name()).create());
        player.sendMessage(new ComponentBuilder("Name: ").append(factory.getName()).create());
        player.sendMessage(new ComponentBuilder("Display name: ").append(factory.getDisplayName()).create());
        player.sendMessage(new ComponentBuilder("Display name position: ").append(""+factory.getDisplayNamePosition()).create());
        player.sendMessage(new ComponentBuilder("Entity type: ").append(factory.getDataFile()).create());
        player.sendMessage(new ComponentBuilder("Use Blacklist: ").append(""+factory.hasBlackList()).create());
        player.sendMessage(new ComponentBuilder("UUID: ").append(""+factory.getUniqueId()).create());
        player.sendMessage(new ComponentBuilder("Entity Location: ").append(""+factory.getLocation()).create());
        player.sendMessage(new ComponentBuilder("Max rotation step: ").append(""+factory.getMaxRotationStep()).create());
        player.sendMessage(new ComponentBuilder("Max rotation step in flight: ").append(""+factory.getMaxRotationStepFlight()).create());
        player.sendMessage(new ComponentBuilder("Update interval in ticks: ").append(""+factory.getUpdateInterval()).create());
        player.sendMessage(new ComponentBuilder("View distance in blocks: ").append(""+factory.getViewDistance()).create());
        player.sendMessage(new ComponentBuilder("Jump height in blocks: ").append(""+factory.getJumpHeight()).create());
        player.sendMessage(new ComponentBuilder("Mouth position: ").append(""+factory.getMouth()).create());
        player.sendMessage(new ComponentBuilder("Entity Location: ").append(""+factory.getLocation()).create());
        player.sendMessage(new ComponentBuilder("Base Knockback: ").append(""+factory.getKnockBackBase()).create());
        player.sendMessage(new ComponentBuilder("Knockback per Damage: ").append(""+factory.getKnockBackPerDamage()).create());

    }

}
