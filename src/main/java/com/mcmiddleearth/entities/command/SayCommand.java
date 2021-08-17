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

public class SayCommand extends McmeEntitiesCommandHandler {

    public SayCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("side", word())
                        .then(HelpfulRequiredArgumentBuilder.argument("text", greedyString())
                                .executes(context -> say(context.getSource(), context.getArgument("side", String.class), context.getArgument("text", String.class)))));
        return commandNodeBuilder;
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



}
