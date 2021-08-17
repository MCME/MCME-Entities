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

public class ArmyCommand extends McmeEntitiesCommandHandler {

    public ArmyCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("size", integer())
                        .executes(context -> spawnEntityArmy(context.getSource(),
                                null,
                                context.getArgument("size", Integer.class),
                                null, null, null))
                        .then(HelpfulRequiredArgumentBuilder.argument("type", new EntityTypeArgument())
                                .executes(context -> spawnEntityArmy(context.getSource(),
                                        context.getArgument("type", String.class),
                                        context.getArgument("size", Integer.class),
                                        null, null, null))
                                .then(HelpfulRequiredArgumentBuilder.argument("goal", new GoalTypeArgument())
                                        .executes(context -> spawnEntityArmy(context.getSource(),
                                                context.getArgument("type", String.class),
                                                context.getArgument("size", Integer.class),
                                                null,
                                                context.getArgument("goal", String.class), null))
                                        .then(HelpfulRequiredArgumentBuilder.argument("dataFile", new AnimationFileArgument())
                                                .executes(context -> spawnEntityArmy(context.getSource(),
                                                        context.getArgument("type", String.class),
                                                        context.getArgument("size", Integer.class),
                                                        null,
                                                        context.getArgument("goal", String.class),
                                                        context.getArgument("dataFile", String.class)))
                                                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                                        .executes(context -> spawnEntityArmy(context.getSource(),
                                                                context.getArgument("type", String.class),
                                                                context.getArgument("size", Integer.class),
                                                                context.getArgument("name", String.class),
                                                                context.getArgument("goal", String.class),
                                                                context.getArgument("dataFile",String.class))))))));
        return commandNodeBuilder;
    }

    private int spawnEntityArmy(McmeCommandSender sender, String type, int size, String name, String goal, String dataFile) {
//Logger.getGlobal().info("Army: type: "+type+" size: "+size+" name: "+name+" goal: "+goal);
        /*VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer) sender).getLocation())
                .withTargetLocation(((RealPlayer) sender).getLocation().add(new Vector(20, 0, 20)))
                .withName(name)
                .withHeadPitchCenter(new Vector(0, 0, 0.3))
                .withDataFile(name)
                .withGoalType(GoalType.valueOf(goal.toUpperCase()))
                .withTargetEntity((RealPlayer) sender);*/
        //((BukkitCommandSender)sender).clearSelection();
        VirtualEntityFactory factory = getFactory(sender, type, name, goal, dataFile);
        for (int i = 0; i < size; i++) {
            //factory.withLocation(((RealPlayer) sender).getLocation().add(new Vector(i * 2, 0, 0)));
            for (int j = 0; j < size; j++) {
                try {
                    ((BukkitCommandSender) sender).addToSelectedEntities(EntityAPI.spawnEntity(factory));
                    sender.sendMessage(new ComponentBuilder((size * size) + " entities spawned.").create());
                } catch (InvalidLocationException e) {
                    sender.sendMessage(new ComponentBuilder("Can't spawn because of invalid or missing location!").create());
                } catch (InvalidDataException e) {
                    sender.sendMessage(new ComponentBuilder(e.getMessage()).create());
                }
                factory.withLocation(factory.getLocation().add(new Vector(0, 0, 2)));
            }
        }
        return 0;
    }
}
