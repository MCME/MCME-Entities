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

public class SelectCommand extends McmeEntitiesCommandHandler {

    public SelectCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulLiteralBuilder.literal("entity")
                    .executes(context -> showSelection(context.getSource()))
                    .then(HelpfulLiteralBuilder.literal("target")
                        .executes(context -> setSelectTargetEntity(context.getSource()))
                        .then(HelpfulLiteralBuilder.literal("@p")
                            .executes(context -> {
                                ((BukkitCommandSender)context.getSource()).setSelectedTargetEntity((RealPlayer)context.getSource());
                                context.getSource().sendMessage(new ComponentBuilder("Saved you as target entity!").create());
                                return 0;
                            })))
                    .then(HelpfulLiteralBuilder.literal("clear")
                        .executes(context -> clearSelection(context.getSource()))))
                .then(HelpfulLiteralBuilder.literal("location")
                    .executes(context -> showSelectedLocations(context.getSource()))
                    .then(HelpfulLiteralBuilder.literal("add")
                        .executes(context -> addSelectedLocation(context.getSource(),null))
                        .then(HelpfulRequiredArgumentBuilder.argument("location", greedyString())
                            .executes(context -> addSelectedLocation(context.getSource(),context.getArgument("location",String.class)))))
                    .then(HelpfulLiteralBuilder.literal("clear")
                        .executes(context -> clearSelectedLocations(context.getSource()))));
        return commandNodeBuilder;
    }

    private int showSelection(McmeCommandSender sender) {
        sender.sendMessage(new ComponentBuilder("Selected Entities:").create());
        ((BukkitCommandSender)sender).getSelectedEntities().forEach(entity -> {
            sender.sendMessage(new ComponentBuilder(entity.getEntityId()+" "+entity.getName()+" "
                    +entity.getLocation().getBlockX()+" "+entity.getLocation().getBlockY()+" "+entity.getLocation().getBlockZ()).create());
        });
        return 0;
    }

    private int clearSelection(McmeCommandSender sender) {
        ((BukkitCommandSender)sender).clearSelectedEntities();
        sender.sendMessage(new ComponentBuilder("Entity selection cleared").create());
        return 0;
    }

    private int setSelectTargetEntity(McmeCommandSender sender) {
        McmeEntity entity = ((BukkitCommandSender)sender).getSelectedEntities().stream().findFirst().orElse(null);
        if(entity != null) {
            ((BukkitCommandSender) sender).setSelectedTargetEntity(entity);
            sender.sendMessage(new ComponentBuilder("Saved as target entity:  " + entity.getName() + " "
                    + entity.getLocation().getBlockX() + " " + entity.getLocation().getBlockY() + " " + entity.getLocation().getBlockZ()).create());
        } else {
            sender.sendMessage(new ComponentBuilder("You need to select an entity first.").create());
        }
        return 0;
    }

    private int clearSelectedLocations(McmeCommandSender sender) {
        RealPlayer player = (RealPlayer) sender;
        player.getSelectedPoints().clear();
        sender.sendMessage(new ComponentBuilder("Location selection cleared.").create());
        return 0;
    }

    private int addSelectedLocation(McmeCommandSender sender, String location) {
        RealPlayer player = (RealPlayer) sender;
        if(location == null) {
            player.getSelectedPoints().add(player.getLocation());
            sender.sendMessage(new ComponentBuilder("Added your position to your list of selected locations.").create());
        } else {
            try {
                Location loc = parseLocation(player.getBukkitPlayer(), location);
                player.getSelectedPoints().add(loc);
                sender.sendMessage(new ComponentBuilder("Added ("+ loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ()
                                                                    +") to your list of selected locations.").create());
            } catch(IllegalArgumentException ex) {
                sender.sendMessage(new ComponentBuilder("Invalid input! Can't parse location.").create());
            }
        }
        return 0;
    }

    private int showSelectedLocations(McmeCommandSender sender) {
        sender.sendMessage(new ComponentBuilder("Selected Locations:").create());
        ((BukkitCommandSender)sender).getSelectedPoints().forEach(location -> {
            sender.sendMessage(new ComponentBuilder(
                    location.getBlockX()+" "+location.getBlockY()+" "+location.getBlockZ()).create());
        });
        return 0;

    }

}
