package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.ai.goal.*;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
import com.mcmiddleearth.entities.entities.*;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class VirtualCommand extends AbstractCommandHandler implements TabExecutor {

    public VirtualCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> sender instanceof RealPlayer)
                .then(HelpfulLiteralBuilder.literal("spawn")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class), null))
                                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                        .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class),
                                                context.getArgument("name", String.class))))))
                .then(HelpfulLiteralBuilder.literal("army")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .then(HelpfulRequiredArgumentBuilder.argument("size", integer())
                                    .executes(context -> spawnEntityArmy(context.getSource(),
                                                                         context.getArgument("type", String.class),
                                                                         context.getArgument("size", Integer.class))))))
                .then(HelpfulLiteralBuilder.literal("remove")
                        .executes(context -> removeEntity((BukkitCommandSender)context.getSource(),
                                                          ((BukkitCommandSender)context.getSource())
                                                          .getSelectedEntities()))
                        .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                .executes(context -> removeEntity(context.getSource(),
                                                    Collections.singleton(EntityAPI.getEntity(context.getArgument("name", String.class)))))))
                .then(HelpfulLiteralBuilder.literal("selection")
                        .executes(context -> showSelection(context.getSource()))
                        .then(HelpfulLiteralBuilder.literal("clear")
                                .executes(context -> clearSelection(context.getSource()))))
                .then(HelpfulLiteralBuilder.literal("path")
                        .executes(context -> findPath(context.getSource())))
                .then(HelpfulLiteralBuilder.literal("goal")
                        .executes(context -> setGoal(context.getSource())))
                .then(HelpfulLiteralBuilder.literal("animate")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", word())
                                .executes(context -> animateEntity(context.getSource(), context.getArgument("animationId",String.class)))));
        return commandNodeBuilder;
    }

    private int spawnEntity(McmeCommandSender sender, String type, String name) {
        VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation())
                .withName(name)
                .withDataFile(name)
                .withGoalType(GoalType.FOLLOW_ENTITY)
                .withTargetEntity((RealPlayer)sender);
        ((BukkitCommandSender)sender).setSelection(EntityAPI.spawnEntity(factory));
        return 0;
    }

    private int spawnEntityArmy(McmeCommandSender sender, String type, int size) {
        VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation())
                .withGoalType(GoalType.FOLLOW_ENTITY)
                .withTargetEntity((RealPlayer)sender);
        //((BukkitCommandSender)sender).clearSelection();
        for(int i = 0; i < size; i++) {
            factory.withLocation(((RealPlayer)sender).getLocation().add(new Vector(i*2,0,0)));
            for(int j = 0; j < size; j++) {
                ((BukkitCommandSender) sender).addToSelection(EntityAPI.spawnEntity(factory));
                factory.withLocation(factory.getLocation().add(new Vector(0, 0, 2)));
            }
        }
        return 0;
    }

    private int removeEntity(McmeCommandSender sender, Set<McmeEntity> entities) {
        EntityAPI.removeEntity(entities);
        return 0;
    }

    private int showSelection(McmeCommandSender sender) {
        sender.sendMessage(new ComponentBuilder("Mcme - Entities - Selection:").create());
        ((BukkitCommandSender)sender).getSelectedEntities().forEach(entity -> {
            sender.sendMessage(new ComponentBuilder(entity.getEntityId()+" "+entity.getName()+" "
                               +entity.getLocation().getBlockX()+" "+entity.getLocation().getBlockY()+" "+entity.getLocation().getBlockZ()).create());
        });
        return 0;
    }

    private int clearSelection(McmeCommandSender sender) {
        ((BukkitCommandSender)sender).clearSelection();
        sender.sendMessage(new ComponentBuilder("Entity Selection cleared").create());
        return 0;
    }

    private int findPath(McmeCommandSender sender) {
        RealPlayer player = ((RealPlayer)sender);
        VirtualEntity entity = (VirtualEntity) player.getSelectedEntities().iterator().next();
        GoalVirtualEntity goal = new GoalEntityTargetFollow(GoalType.FOLLOW_ENTITY, entity,
                                     new WalkingPathfinder(entity), player);
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
        return 0;
    }

    private int animateEntity(McmeCommandSender sender, String animationId) {
        RealPlayer player = ((RealPlayer)sender);
        VirtualEntity entity = (VirtualEntity) player.getSelectedEntities().iterator().next();
        if(entity instanceof BakedAnimationEntity) {
            ((BakedAnimationEntity)entity).setAnimation(animationId);
        }
        return 0;
    }

    private int setGoal(McmeCommandSender sender) {
        World world = ((RealPlayer) sender).getBukkitPlayer().getLocation().getWorld();
        Location[] checkpoints = new Location[]{new Location(world, -10, 20, 3),
                                                new Location(world, 10, 20, 3),
                                                new Location(world, 10, 20, 13),
                                                new Location(world, -10, 20, 13)};
        VirtualEntity entity = (VirtualEntity) ((RealPlayer) sender).getSelectedEntities().iterator().next();
        Goal goal = new GoalLocationTargetFollowCheckpoints(GoalType.FOLLOW_CHECKPOINTS, entity,
                                              new WalkingPathfinder(entity),checkpoints,true);
        entity.setGoal(goal);
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        McmeCommandSender wrappedSender = EntityAPI.wrapCommandSender(sender);
        execute(wrappedSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
//Logger.getGlobal().info("tabComplete 1");
        TabCompleteRequest request = new SimpleTabCompleteRequest(EntityAPI.wrapCommandSender(sender),
                                                                  String.format("/%s %s", alias, Joiner.on(' ').join(args)).trim());
        onTabComplete(request);
//Logger.getGlobal().info("tabComplete 1");
        return request.getSuggestions();
    }

}
