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
import com.mcmiddleearth.entities.ai.goal.*;
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

public class TestCommand extends McmeEntitiesCommandHandler {

    public TestCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulLiteralBuilder.literal("path")
                        .executes(context -> findPath(context.getSource())));
        return commandNodeBuilder;
    }

    private int findPath(McmeCommandSender sender) {
        RealPlayer player = ((RealPlayer)sender);
        VirtualEntity entity = (VirtualEntity) player.getSelectedEntities().iterator().next();
        try {
            GoalVirtualEntity goal = new VirtualEntityGoalFactory(GoalType.FOLLOW_ENTITY)
                    .withTargetEntity(player)
                    .build(entity);
            goal.update();
            Path path = ((GoalPath)goal).getPath();
            if(path!=null) {
                Logger.getGlobal().info("Target: " +path.getTarget());
                Logger.getGlobal().info("Start: " +path.getStart());
                Logger.getGlobal().info("End: " +path.getEnd());
                path.getPoints().forEach(point -> {
                    Logger.getGlobal().info(point.getBlockX()+" "+point.getBlockY()+" "+point.getBlockZ());
                    player.getLocation().getWorld()
                            .dropItem(point.toLocation(player.getLocation().getWorld()), new ItemStack(Material.STONE));
                });
            } else {
                Logger.getGlobal().info("no path found");
            }
        } catch (Exception ignore) {}
        return 0;
    }

}
