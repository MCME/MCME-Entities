package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.*;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.ai.goal.*;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
import com.mcmiddleearth.entities.entities.*;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import com.mcmiddleearth.entities.entities.composite.SpeechBalloonLayout;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class VirtualCommand extends AbstractCommandHandler implements TabExecutor {

    public VirtualCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> !((sender instanceof RealPlayer)
                        && !((RealPlayer)sender).getBukkitPlayer().hasPermission(Permission.USER.getNode())))
                .requires(sender -> sender instanceof RealPlayer)
                .then(HelpfulLiteralBuilder.literal("spawn")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .then(HelpfulRequiredArgumentBuilder.argument("goal", word())
                                        .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class), null,
                                                context.getArgument("goal",String.class),0))
                                        .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                                .then(HelpfulRequiredArgumentBuilder.argument("delay", integer())
                                                    .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class),
                                                        context.getArgument("name", String.class), context.getArgument("goal",String.class),
                                                        context.getArgument("delay",Integer.class))))))))
                .then(HelpfulLiteralBuilder.literal("army")
                    .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                        .then(HelpfulRequiredArgumentBuilder.argument("size", integer())
                            .then(HelpfulRequiredArgumentBuilder.argument("goal", word())
                                .executes(context -> spawnEntityArmy(context.getSource(),
                                                               context.getArgument("type", String.class),
                                                               context.getArgument("size", Integer.class),
                                                               null,
                                                               context.getArgument("goal",String.class)))
                                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                                .executes(context -> spawnEntityArmy(context.getSource(),
                                                                               context.getArgument("type", String.class),
                                                                               context.getArgument("size", Integer.class),
                                                                               context.getArgument("name", String.class),
                                                                               context.getArgument("goal",String.class))))))))
                /*.then(HelpfulLiteralBuilder.literal("army")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .then(HelpfulRequiredArgumentBuilder.argument("size", integer())
                                    .executes(context -> spawnEntityArmy(context.getSource(),
                                                                         context.getArgument("type", String.class),
                                                                         context.getArgument("size", Integer.class))))))*/
                .then(HelpfulLiteralBuilder.literal("remove")
                        .executes(context -> removeEntity((BukkitCommandSender)context.getSource(),
                                                          ((BukkitCommandSender)context.getSource())
                                                          .getSelectedEntities()))
                        .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                .executes(context -> removeEntity(context.getSource(),
                                                    Collections.singleton(EntityAPI.getEntity(context.getArgument("name", String.class)))))))
                .then(HelpfulLiteralBuilder.literal("say")
                        .then(HelpfulRequiredArgumentBuilder.argument("side",word())
                            .then(HelpfulRequiredArgumentBuilder.argument("text",greedyString())
                                    .executes(context -> say(context.getSource(),context.getArgument("side",String.class),context.getArgument("text",String.class))))))
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
                                .executes(context -> animateEntity(context.getSource(), context.getArgument("animationId",String.class)))))
                .then(HelpfulLiteralBuilder.literal("frame")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", word())
                            .then(HelpfulRequiredArgumentBuilder.argument("frameId",integer())
                                .executes(context -> applyAnimationFrame(context.getSource(), context.getArgument("animationId", String.class),
                                                                                              context.getArgument("frameId", Integer.class))))));
        return commandNodeBuilder;
    }

    private int spawnEntity(McmeCommandSender sender, String type, String name, String goal, int delay) {
        VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation())
                .withName(name)
                .withDataFile(name)
                .withHeadPoseDelay(delay)
                .withHeadPitchCenter(new Vector(0,0,0.3))
                .withGoalType(GoalType.valueOf(goal.toUpperCase()))
                .withTargetLocation(((RealPlayer)sender).getLocation().add(new Vector(20,0,20)))
                .withTargetEntity((RealPlayer)sender);
        try {
            VirtualEntity entity = (VirtualEntity) EntityAPI.spawnEntity(factory);
            /*if(goal.equalsIgnoreCase("hold_position")) {
                GoalVirtualEntity entityGoal = (GoalVirtualEntity) entity.getGoal();
                entityGoal.clearHeadGoals();
                entityGoal.addHeadGoal(new HeadGoalWatch((RealPlayer) sender, entity));
            }*/
            ((BukkitCommandSender)sender).setSelection(entity);
            sender.sendMessage(new ComponentBuilder("Spawning: "+factory.getType()+" "+GoalType.valueOf(goal.toUpperCase())).create());
        } catch (InvalidLocationException e) {
            sender.sendMessage(new ComponentBuilder("Can't spawn because of invalid location!").create());
            e.printStackTrace();
        }
        return 0;
    }

    private int spawnEntityArmy(McmeCommandSender sender, String type, int size, String name, String goal) {
//Logger.getGlobal().info("Army: type: "+type+" size: "+size+" name: "+name+" goal: "+goal);
        VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation())
                .withTargetLocation(((RealPlayer)sender).getLocation().add(new Vector(20,0,20)))
                .withName(name)
                .withHeadPitchCenter(new Vector(0,0,0.3))
                .withDataFile(name)
                .withGoalType(GoalType.valueOf(goal.toUpperCase()))
                .withTargetEntity((RealPlayer) sender);
        //((BukkitCommandSender)sender).clearSelection();
        for(int i = 0; i < size; i++) {
            factory.withLocation(((RealPlayer)sender).getLocation().add(new Vector(i*2,0,0)));
            for(int j = 0; j < size; j++) {
                try {
                    ((BukkitCommandSender) sender).addToSelection(EntityAPI.spawnEntity(factory));
                } catch (InvalidLocationException e) {
                    sender.sendMessage(new ComponentBuilder("Can't spawn because of invalid location!").create());
                    e.printStackTrace();
                }
                factory.withLocation(factory.getLocation().add(new Vector(0, 0, 2)));
            }
        }
        return 0;
    }

    private int removeEntity(McmeCommandSender sender, Set<? extends Entity> entities) {
        EntityAPI.removeEntity(entities);
        return 0;
    }

    private int applyAnimationFrame(McmeCommandSender sender, String animation, int frameId) {
//Logger.getGlobal().info("Apply Frame command");
        RealPlayer player = ((RealPlayer)sender);
        player.getSelectedEntities().forEach(entity -> {
            if (entity instanceof BakedAnimationEntity) {
                ((BakedAnimationEntity) entity).setManualAnimationControl(true);
                ((BakedAnimationEntity) entity).setAnimationFrame(animation,frameId);
            }
        });

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
        player.getSelectedEntities().forEach(entity -> {
            if (entity instanceof BakedAnimationEntity) {
                ((BakedAnimationEntity)entity).setManualAnimationControl(true);
                ((BakedAnimationEntity) entity).setAnimation(animationId);
            }
        });
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

    private int say(McmeCommandSender sender, String side, String text) {
        String[] words = text.split(" ");
        List<String> lines = new ArrayList<>();
        int i=0;
        while(i<words.length) {
            StringBuilder line = new StringBuilder(words[i]);
            i++;
            while (line.length() < 15 && i < words.length) {
                line.append(" ").append(words[i]);
                i++;
            }
            lines.add(line.toString());
        }
        VirtualEntity entity = (VirtualEntity) ((RealPlayer) sender).getSelectedEntities().iterator().next();
        //entity.say(lines.toArray(new String[0]), 200);
        SpeechBalloonLayout.Position position = (side.equals("l")? SpeechBalloonLayout.Position.LEFT:
                (side.equals("t")? SpeechBalloonLayout.Position.TOP: SpeechBalloonLayout.Position.RIGHT));
        SpeechBalloonLayout layout = new SpeechBalloonLayout(position, SpeechBalloonLayout.Width.OPTIMAL)
                .withDuration(2000)
                .withMessage(text);
        entity.say(layout);
        return 0;
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
