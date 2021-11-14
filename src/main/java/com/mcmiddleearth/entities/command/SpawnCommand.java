package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.Entity;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.command.argument.AnimationFileArgument;
import com.mcmiddleearth.entities.command.argument.EntityTypeArgument;
import com.mcmiddleearth.entities.command.argument.GoalTypeArgument;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.Projectile;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class SpawnCommand extends McmeEntitiesCommandHandler {

    public SpawnCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
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
                                                        context.getArgument("dataFile", String.class)))))));
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
        VirtualEntityFactory factory = getFactory(sender, type, name, goal, dataFile)
                                            .withShooter((McmeEntity) sender);
//AttributeInstance attackSpeed = factory.getAttributes().get(Attribute.GENERIC_ATTACK_SPEED);
//if(attackSpeed!=null) Logger.getGlobal().info("vspawn command: Attack speed: "+attackSpeed.getBaseValue()+" -> "+attackSpeed.getValue());
        Location loc = factory.getLocation();
        Entity spawnLocationEntity = factory.getSpawnLocationEntity();
        if(type.equalsIgnoreCase("arrow") && factory.getGoalFactory()!=null) {
            if(factory.getGoalFactory().getTargetEntity()!=null) {
                Projectile.takeAim(factory, factory.getGoalFactory().getTargetEntity().getLocation());
            } else if(factory.getGoalFactory().getTargetLocation()!=null) {
                Projectile.takeAim(factory, factory.getGoalFactory().getTargetLocation());
            }
        }
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
        factory.withLocation(loc).withEntityForSpawnLocation(spawnLocationEntity);
        return 0;
    }

}
