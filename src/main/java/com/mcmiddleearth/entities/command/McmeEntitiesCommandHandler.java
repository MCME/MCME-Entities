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

public abstract class McmeEntitiesCommandHandler extends AbstractCommandHandler implements TabExecutor {

    public McmeEntitiesCommandHandler(String command) {
        super(command);
    }

    protected VirtualEntityFactory getFactory(McmeCommandSender sender, String type, String name, String goal, String dataFile) {
        RealPlayer player = (RealPlayer) sender;
        VirtualEntityFactory factory = player.getEntityFactory();
Logger.getGlobal().info("Factory: "+factory);
Logger.getGlobal().info("Factory movement type: "+factory.getMovementType().name());
        McmeEntityType entityType = McmeEntityType.valueOf(type);
        if(entityType !=null)
        {
            factory.withEntityType(entityType);
        }
        if(name !=null)
        {
            factory.withName(name);
        }
        if(dataFile!=null && !dataFile.equals("")) {
            factory.withDataFile(dataFile);
        }
        if(factory.getLocation()==null)
        {
            factory.useEntityForSpawnLocation(player);
        }
        if(goal !=null)
        {
            getOrCreateGoalFactory(factory);
            try {
                factory.getGoalFactory().withGoalType(GoalType.valueOf(goal.toUpperCase()));
            } catch (IllegalArgumentException ignore) { }
            if(factory.getGoalFactory().getTargetLocation()==null) {
                factory.getGoalFactory().withTargetLocation(player.getSelectedPoints().stream()
                                           .findFirst().orElse(new Location(player.getLocation().getWorld(),
                                                                            0,0,0)));
            }
            if(player.getSelectedTargetEntity()!=null)
            {
                factory.getGoalFactory().withTargetEntity(player.getSelectedTargetEntity());
            }
            if(factory.getGoalFactory().getTargetEntity() == null) {
                factory.getGoalFactory().withTargetEntity(player);
            }
    //Logger.getGlobal().info("Factory tar type: "+factory.getTargetEntity());
            if(player.getSelectedPoints() != null && player.getSelectedPoints().size()>0) {
                factory.getGoalFactory().withCheckpoints(player.getSelectedPoints().toArray(new Location[0]));
            }
        }
        return factory;
    }

    protected VirtualEntityGoalFactory getOrCreateGoalFactory(VirtualEntityFactory factory) {
        if(factory.getGoalFactory()==null) {
            factory.withGoalFactory(new VirtualEntityGoalFactory(GoalType.HOLD_POSITION));
        }
        return factory.getGoalFactory();
    }

    protected Vector parseVector(Player player, String value) throws IllegalArgumentException {
        if(value.equalsIgnoreCase("@p")) {
            return player.getLocation().toVector().clone();
        }
        String[] split = value.split(" ");
        if(split.length != 3) throw new IllegalArgumentException();
        return new Vector(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2]));
    }

    protected Location parseLocation(Player player, String value) throws IllegalArgumentException {
        Vector vector = parseVector(player, value);
        return vector.toLocation(player.getWorld());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        McmeCommandSender wrappedSender = EntitiesPlugin.wrapCommandSender(sender);
        execute(wrappedSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
//Logger.getGlobal().info("tabComplete 1");
        TabCompleteRequest request = new SimpleTabCompleteRequest(EntitiesPlugin.wrapCommandSender(sender),
                                                                  String.format("/%s %s", alias, Joiner.on(' ').join(args)).trim());
        onTabComplete(request);
//Logger.getGlobal().info("tabComplete 1");
        return request.getSuggestions();
    }

}
