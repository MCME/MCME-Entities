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
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.ai.pathfinding.WalkingPathfinder;
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

public class VirtualCommand extends AbstractCommandHandler implements TabExecutor {

    public VirtualCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .requires(sender -> sender instanceof RealPlayer)
                .then(HelpfulLiteralBuilder.literal("spawn")
                        .executes(context -> spawnEntity(context.getSource(),
                                null,
                                null,
                                null, null))
                        .then(HelpfulRequiredArgumentBuilder.argument("type", new EntityTypeArgument())
                                .executes(context -> spawnEntity(context.getSource(),
                                        context.getArgument("type", String.class),
                                        null,
                                        null, null))
                                .then(HelpfulRequiredArgumentBuilder.argument("goal", new GoalTypeArgument())
                                        .executes(context -> spawnEntity(context.getSource(),
                                                context.getArgument("type", String.class),
                                                null,
                                                context.getArgument("goal", String.class), null))
                                        .then(HelpfulRequiredArgumentBuilder.argument("dataFile", new AnimationFileArgument())
                                                .executes(context -> spawnEntity(context.getSource(),
                                                                                 context.getArgument("type", String.class),
                                                                                 null,
                                                                                 context.getArgument("goal", String.class),
                                                                                 context.getArgument("dataFile", String.class)))
                                                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                                        .executes(context -> spawnEntity(context.getSource(),
                                                                context.getArgument("type", String.class),
                                                                context.getArgument("name", String.class),
                                                                context.getArgument("goal", String.class),
                                                                context.getArgument("dataFile", String.class))))))))
                .then(HelpfulLiteralBuilder.literal("army")
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
                                                                        context.getArgument("dataFile",String.class)))))))))
                /*.then(HelpfulLiteralBuilder.literal("army")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .then(HelpfulRequiredArgumentBuilder.argument("size", integer())
                                    .executes(context -> spawnEntityArmy(context.getSource(),
                                                                         context.getArgument("type", String.class),
                                                                         context.getArgument("size", Integer.class))))))*/
                .then(HelpfulLiteralBuilder.literal("remove")
                        .executes(context -> removeEntity((BukkitCommandSender) context.getSource(),null))
                        .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                .executes(context -> removeEntity(context.getSource(),context.getArgument("name",String.class)))))
                .then(HelpfulLiteralBuilder.literal("save")
                        .then(HelpfulRequiredArgumentBuilder.argument("file", word())
                                .executes(context -> saveEntities(context.getSource(), context.getArgument("file", String.class)))))
                .then(HelpfulLiteralBuilder.literal("load")
                        .then(HelpfulRequiredArgumentBuilder.argument("file", word())
                                .executes(context -> loadEntities(context.getSource(), context.getArgument("file", String.class)))))
                .then(HelpfulLiteralBuilder.literal("say")
                        .then(HelpfulRequiredArgumentBuilder.argument("side", word())
                                .then(HelpfulRequiredArgumentBuilder.argument("text", greedyString())
                                        .executes(context -> say(context.getSource(), context.getArgument("side", String.class), context.getArgument("text", String.class))))))
                .then(HelpfulLiteralBuilder.literal("sel")
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
                                .executes(context -> clearSelectedLocations(context.getSource())))))
                .then(HelpfulLiteralBuilder.literal("factory")
                        .then(HelpfulRequiredArgumentBuilder.argument("property", new FactoryPropertyArgument())
                                .then(HelpfulRequiredArgumentBuilder.argument("value", greedyString())
                                        .executes(context -> setFactoryValue(context.getSource(),
                                                                             context.getArgument("property", String.class),
                                                                             context.getArgument("value", String.class))))))
                .then(HelpfulLiteralBuilder.literal("path")
                        .executes(context -> findPath(context.getSource())))
                .then(HelpfulLiteralBuilder.literal("setgoal")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", new GoalTypeArgument())
                            .executes(context -> setGoal(context.getSource(), context.getArgument("type", String.class), false))
                            .then(HelpfulLiteralBuilder.literal("loop")
                                .executes(context -> setGoal(context.getSource(), context.getArgument("type", String.class), true)))))
                .then(HelpfulLiteralBuilder.literal("animate")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", new AnimationIdArgument())
                                .executes(context -> animateEntity(context.getSource(), context.getArgument("animationId", String.class)))))
                .then(HelpfulLiteralBuilder.literal("frame")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", new AnimationIdArgument())
                                .then(HelpfulRequiredArgumentBuilder.argument("frameId", integer())
                                        .executes(context -> applyAnimationFrame(context.getSource(), context.getArgument("animationId", String.class),
                                                context.getArgument("frameId", Integer.class))))));
        return commandNodeBuilder;
    }

    private int spawnEntity(McmeCommandSender sender, String type, String name, String goal, String dataFile) {//, int delay) {
        /*VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation())
                .withName(name)
                .withDataFile(name)
                .withHeadPoseDelay(delay)
                .withHeadPitchCenter(new Vector(0,0,0.3))
                .withGoalType(GoalType.valueOf(goal.toUpperCase()))
                .withTargetEntity((RealPlayer)sender);*/
        VirtualEntityFactory factory = getFactory(sender, type, name, goal, dataFile);
        try {
            VirtualEntity entity = (VirtualEntity) EntityAPI.spawnEntity(factory);
            /*if(goal.equalsIgnoreCase("hold_position")) {
                GoalVirtualEntity entityGoal = (GoalVirtualEntity) entity.getGoal();
                entityGoal.clearHeadGoals();
                entityGoal.addHeadGoal(new HeadGoalWatch((RealPlayer) sender, entity));
            }*/
            ((BukkitCommandSender)sender).setSelectedEntities(entity);
            sender.sendMessage(new ComponentBuilder("Spawning: " + type).create());
        } catch (InvalidLocationException e) {
            sender.sendMessage(new ComponentBuilder("Can't spawn because of invalid or missing location!").create());
        } catch (InvalidDataException e) {
            sender.sendMessage(new ComponentBuilder(e.getMessage()).create());
        }
        return 0;
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

    private VirtualEntityFactory getFactory(McmeCommandSender sender, String type, String name, String goal, String dataFile) {
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
            createGoalFactory(factory);
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

    private int removeEntity(McmeCommandSender sender, String name) {
        Collection<? extends Entity> entities = new HashSet<>();
        if(name !=null && name.equalsIgnoreCase("all")) {
            entities = EntitiesPlugin.getEntityServer().getEntities(VirtualEntity.class);
        } else if(name != null) {
            Entity entity = (EntityAPI.getEntity(name));
            if(entity != null) {
                entities = Collections.singleton(entity);
            }
        } else {
            entities =  ((BukkitCommandSender) sender).getSelectedEntities();
        }
        EntityAPI.removeEntity(entities);
        sender.sendMessage(new ComponentBuilder(entities.size()+" entities removed.").create());
        return 0;
    }

    private int loadEntities(McmeCommandSender sender, String fileName) {
        File file = new File(EntitiesPlugin.getEntitiesFolder(),fileName+".json");
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        int counter = 0;
        try (JsonReader reader = gson.newJsonReader(new FileReader(file))) {
            reader.beginArray();
            while(reader.hasNext()) {
                VirtualEntityFactory factory = gson.fromJson(reader, VirtualEntityFactory.class);
                EntitiesPlugin.getEntityServer().spawnEntity(factory);
                counter++;
            }
            reader.endArray();
            sender.sendMessage(new ComponentBuilder(counter+" entities loaded.").create());
        } catch (FileNotFoundException e) {
            sender.sendMessage(new ComponentBuilder("File not found.").color(ChatColor.RED).create());
        } catch (IOException e) {
            sender.sendMessage(new ComponentBuilder("File input error.").color(ChatColor.RED).create());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(new ComponentBuilder(ex.getMessage()).color(ChatColor.RED).create());
        } catch (InvalidDataException e) {
            sender.sendMessage(new ComponentBuilder("Invalid entity data in file.").color(ChatColor.RED).create());
        } catch (InvalidLocationException e) {
            sender.sendMessage(new ComponentBuilder("Invalid location data in file.").color(ChatColor.RED).create());
        }
        return 0;
    }

    private int saveEntities(McmeCommandSender sender, String fileName) {
        File file = new File(EntitiesPlugin.getEntitiesFolder(),fileName+".json");
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        int counter = 0;
        try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
            writer.beginArray();
            for(McmeEntity entity: ((BukkitCommandSender)sender).getSelectedEntities()) {
                if(entity instanceof VirtualEntity) {
                    gson.toJson(((VirtualEntity)entity).getFactory(), VirtualEntityFactory.class,writer);
                    counter++;
                }
            }
            writer.endArray();
            sender.sendMessage(new ComponentBuilder(counter + " entities save to file '"+file+"'.").create());
        } catch (IOException e) {
            sender.sendMessage(new ComponentBuilder("File output error.").color(ChatColor.RED).create());
        }
        return 0;
    }

    private int applyAnimationFrame(McmeCommandSender sender, String animation, int frameId) {
//Logger.getGlobal().info("Apply Frame command");
        RealPlayer player = ((RealPlayer)sender);
        int counter = 0;
        for(Entity entity :player.getSelectedEntities()) {
//Logger.getGlobal().info("entity: "+entity.getClass().getSimpleName());
            if (entity instanceof BakedAnimationEntity) {
                ((BakedAnimationEntity) entity).setManualAnimationControl(true);
                ((BakedAnimationEntity) entity).setAnimationFrame(animation,frameId);
                counter++;
            }
        }
        sender.sendMessage(new ComponentBuilder("Displaying frame "+frameId+" of animation "+animation+" for "
                                                     +counter+" entities.").create());
        return 0;
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

    private int animateEntity(McmeCommandSender sender, String animationId) {
        RealPlayer player = ((RealPlayer)sender);
        player.getSelectedEntities().forEach(entity -> {
            if (entity instanceof BakedAnimationEntity) {
                if(animationId.equals("auto")) {
                    ((BakedAnimationEntity)entity).setManualAnimationControl(false);
                } else {
                    ((BakedAnimationEntity) entity).setManualAnimationControl(true);
                    ((BakedAnimationEntity) entity).setAnimation(animationId);
                }
            }
        });
        if(animationId.equals("auto")) {
            sender.sendMessage(new ComponentBuilder("Setting automated animation mode."+" for "
                    +player.getSelectedEntities().size()+" entities.").create());
        } else {
            sender.sendMessage(new ComponentBuilder("Playing animation " + animationId+" for "
                    +player.getSelectedEntities().size()+" entities.").create());
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

    private int setFactoryValue(McmeCommandSender sender, String property, String value) {
        BukkitCommandSender player = (BukkitCommandSender) sender;
        VirtualEntityFactory factory = player.getEntityFactory();
        switch (property.toLowerCase()) {
            case "type":
                McmeEntityType entityType = McmeEntityType.valueOf(value);
                if (entityType != null) {
                    factory.withEntityType(entityType);
                }
                break;
            case "invertwhitelist":
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
                    createGoalFactory(factory);
                    factory.getGoalFactory().withGoalType(GoalType.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse goal type").color(ChatColor.RED).create());
                }
                break;
            case "targetlocation":
                try {
                    createGoalFactory(factory);
                    factory.getGoalFactory().withTargetLocation(parseLocation(((RealPlayer)player).getBukkitPlayer(), value));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage(new ComponentBuilder("Invalid input! Could not parse target location!").color(ChatColor.RED).create());
                }
                break;
            case "targetentity":
                McmeEntity target = EntitiesPlugin.getEntityServer().getEntity(value);
                if(target != null) {
                    createGoalFactory(factory);
                    factory.getGoalFactory().withTargetEntity(target);
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
                    String[] split = value.split(" ");
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
            default:
                sender.sendMessage(new ComponentBuilder("Property " + property +" could not be found.").color(ChatColor.RED).create());
                return 0;
        }
        sender.sendMessage(new ComponentBuilder(property + " set to " + value + ".").create());
        return 0;
    }

    private void createGoalFactory(VirtualEntityFactory factory) {
        factory.withGoalFactory(new VirtualEntityGoalFactory(GoalType.HOLD_POSITION));
    }

    private Vector parseVector(Player player, String value) throws IllegalArgumentException {
        if(value.equalsIgnoreCase("@p")) {
            return player.getLocation().toVector().clone();
        }
        String[] split = value.split(" ");
        if(split.length != 3) throw new IllegalArgumentException();
        return new Vector(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2]));
    }

    private Location parseLocation(Player player, String value) throws IllegalArgumentException {
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
