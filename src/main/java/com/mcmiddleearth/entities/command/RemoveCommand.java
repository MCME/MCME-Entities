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

public class RemoveCommand extends McmeEntitiesCommandHandler {

    public RemoveCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .executes(context -> removeEntity((BukkitCommandSender) context.getSource(),null))
                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                        .executes(context -> removeEntity(context.getSource(),context.getArgument("name",String.class))));
        return commandNodeBuilder;
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

}
