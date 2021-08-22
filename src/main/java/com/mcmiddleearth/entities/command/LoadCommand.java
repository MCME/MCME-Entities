package com.mcmiddleearth.entities.command;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class LoadCommand extends McmeEntitiesCommandHandler {

    public LoadCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("file", word())
                        .executes(context -> loadEntities(context.getSource(), context.getArgument("file", String.class))));
        return commandNodeBuilder;
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
}
