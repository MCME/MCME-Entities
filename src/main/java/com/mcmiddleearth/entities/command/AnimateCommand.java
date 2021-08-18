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
import com.mcmiddleearth.entities.command.argument.GoalTypeArgument;
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
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class AnimateCommand extends McmeEntitiesCommandHandler {

    public AnimateCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulLiteralBuilder.literal("play")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", new AnimationIdArgument())
                                .executes(context -> animateEntity(context.getSource(), context.getArgument("animationId", String.class)))))
                .then(HelpfulLiteralBuilder.literal("frame")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", new AnimationIdArgument())
                                .then(HelpfulRequiredArgumentBuilder.argument("frameId", integer())
                                        .executes(context -> applyAnimationFrame(context.getSource(), context.getArgument("animationId", String.class),
                                                context.getArgument("frameId", Integer.class))))));
        return commandNodeBuilder;
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

}
