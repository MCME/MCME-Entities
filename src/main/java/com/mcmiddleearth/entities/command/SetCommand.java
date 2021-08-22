package com.mcmiddleearth.entities.command;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.ai.goal.GoalPath;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.GoalVirtualEntity;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.api.Entity;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.command.argument.AnimationIdArgument;
import com.mcmiddleearth.entities.command.argument.AttributeTypeArgument;
import com.mcmiddleearth.entities.command.argument.GoalTypeArgument;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.attributes.VirtualAttributeFactory;
import com.mcmiddleearth.entities.entities.attributes.VirtualEntityAttributeInstance;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class SetCommand extends McmeEntitiesCommandHandler {

    public SetCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulLiteralBuilder.literal("goal")
                    .then(HelpfulRequiredArgumentBuilder.argument("type", new GoalTypeArgument())
                        .executes(context -> setGoal(context.getSource(), context.getArgument("type", String.class), false))
                        .then(HelpfulLiteralBuilder.literal("loop")
                            .executes(context -> setGoal(context.getSource(), context.getArgument("type", String.class), true)))))
                .then(HelpfulLiteralBuilder.literal("displayname")
                    .then(HelpfulRequiredArgumentBuilder.argument("displayname", word())
                        .executes(context -> setDisplayName(context.getSource(), context.getArgument("displayname", String.class)))))
                .then(HelpfulLiteralBuilder.literal("attribute")
                    .then(HelpfulRequiredArgumentBuilder.argument("type", new AttributeTypeArgument())
                        .then(HelpfulRequiredArgumentBuilder.argument("value", word())
                            .executes(context -> setAttribute(context.getSource(),context.getArgument("type",String.class),
                                                                                  context.getArgument("value", String.class))))));
        return commandNodeBuilder;
    }

    private int setAttribute(McmeCommandSender sender, String type, String valueString) {
        try {
            double value = Double.parseDouble(valueString);
            Entity entity = ((BukkitCommandSender)sender).getSelectedEntities().stream().findFirst().orElse(null);
            if(entity instanceof VirtualEntity) {
                Attribute attributeType = Attribute.valueOf(type.toUpperCase());
                AttributeInstance attribute = ((VirtualEntity)entity).getAttribute(attributeType);
//Logger.getGlobal().info("attribute instance set command: "+attribute);
                if(attribute == null) {
                    ((VirtualEntity)entity).registerAttribute(attributeType);
                    attribute = ((VirtualEntity)entity).getAttribute(attributeType);
                }
                if(attribute!=null) {
                    attribute.setBaseValue(value);
                    sender.sendMessage(new ComponentBuilder("Attribute '"+type+"' set to "+value).create());
                } else {
                    sender.sendMessage(new ComponentBuilder("Attribute not found!").color(ChatColor.RED).create());
                }
            } else {
                sender.sendMessage(new ComponentBuilder("You need to select an entity first!").color(ChatColor.RED).create());
            }
        } catch(NumberFormatException ex) {
            sender.sendMessage(new ComponentBuilder("Attribute value must be  a decimal number!").color(ChatColor.RED).create());
        } catch(IllegalArgumentException ex) {
            sender.sendMessage(new ComponentBuilder("Invalid attribute type!").color(ChatColor.RED).create());
        }
        return 0;
    }

    private int setDisplayName(McmeCommandSender source, String displayname) {
        Entity entity = ((BukkitCommandSender)source).getSelectedEntities().stream().findFirst().orElse(null);
        if(entity instanceof VirtualEntity) {
            ((VirtualEntity)entity).setDisplayName(displayname);
            source.sendMessage(new ComponentBuilder("Set display name to: "+displayname).create());
        } else {
            source.sendMessage(new ComponentBuilder("You need to select an entity first!").color(ChatColor.RED).create());
        }
        return 0;
    }


    private int setGoal(McmeCommandSender sender, String type, boolean loop) {
        try {
            RealPlayer player = (RealPlayer) sender;
            McmeEntity mcmeEntity = player.getSelectedEntities().stream().findFirst().orElse(null);
            if(!(mcmeEntity instanceof VirtualEntity)) {
                if(mcmeEntity == null) {
                    sender.sendMessage(new ComponentBuilder("You need to select at least one entity to apply the goal to.").create());
                } else {
                    sender.sendMessage(new ComponentBuilder("Goals can be applied at virtual entities only.").create());
                }
                return 0;
            }
            VirtualEntity entity = (VirtualEntity) mcmeEntity;
            GoalType goalType = GoalType.valueOf(type.toUpperCase());
            VirtualEntityGoalFactory factory = new VirtualEntityGoalFactory(goalType)
                    .withLoop(loop)
                    .withTargetEntity(player.getSelectedTargetEntity())
                    .withTargetLocation(player.getSelectedPoints().stream().findFirst().orElse(null))
                    .withCheckpoints(player.getSelectedPoints().toArray(new Location[0]));
            entity.setGoal(factory.build(entity));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(new ComponentBuilder("Invalid goal type!").create());
        } catch (InvalidLocationException e) {
            sender.sendMessage(new ComponentBuilder("Invalid location. All location must be same world!").create());
        } catch (InvalidDataException e) {
            sender.sendMessage(new ComponentBuilder(e.getMessage()).create());
        }
        return 0;
        /*
        World world = ((RealPlayer) sender).getBukkitPlayer().getLocation().getWorld();
        Location[] checkpoints = new Location[]{new Location(world, -10, 20, 3),
                                                new Location(world, 10, 20, 3),
                                                new Location(world, 10, 20, 13),
                                                new Location(world, -10, 20, 13)};
        VirtualEntity entity = (VirtualEntity) ((RealPlayer) sender).getSelectedEntities().iterator().next();
        Goal goal = new GoalLocationTargetFollowCheckpoints(GoalType.FOLLOW_CHECKPOINTS, entity,
                                              new WalkingPathfinder(entity),checkpoints,true);
        entity.setGoal(goal);
        return 0;*/
    }

}
