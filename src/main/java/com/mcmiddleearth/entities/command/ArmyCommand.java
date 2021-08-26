package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.command.argument.AnimationFileArgument;
import com.mcmiddleearth.entities.command.argument.EntityTypeArgument;
import com.mcmiddleearth.entities.command.argument.GoalTypeArgument;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.util.Vector;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
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
        int counter = 1;
        for (int i = 0; i < size; i++) {
            factory.withLocation(((RealPlayer) sender).getLocation().add(new Vector(i * 2, 0, 0)));
            for (int j = 0; j < size; j++) {
                factory.withName(name+counter);
                try {
                    ((BukkitCommandSender) sender).addToSelectedEntities(EntityAPI.spawnEntity(factory));
                } catch (InvalidLocationException e) {
                    sender.sendMessage(new ComponentBuilder("Can't spawn because of invalid or missing location!").create());
                } catch (InvalidDataException e) {
                    sender.sendMessage(new ComponentBuilder(e.getMessage()).create());
                }
                factory.withLocation(factory.getLocation().add(new Vector(0, 0, 2)));
                counter++;
            }
        }
        sender.sendMessage(new ComponentBuilder((counter) + " entities spawned.").create());
        return 0;
    }
}
