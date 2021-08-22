package com.mcmiddleearth.entities.command;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class SaveCommand extends McmeEntitiesCommandHandler {

    public SaveCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("file", word())
                        .executes(context -> saveEntities(context.getSource(),
                                                 context.getArgument("file", String.class),false))
                        .then(HelpfulRequiredArgumentBuilder.argument("writeDefaults", word())
                            .executes(context -> saveEntities(context.getSource(),
                                            context.getArgument("file", String.class),
                                            context.getArgument("writeDefaults", String.class)
                                                   .equalsIgnoreCase("true")))));
        return commandNodeBuilder;
    }

    private int saveEntities(McmeCommandSender sender, String fileName, boolean writeDefaults) {
        File file = new File(EntitiesPlugin.getEntitiesFolder(),fileName+".json");
        Gson gson = EntitiesPlugin.getEntitiesGsonBuilder().create();
        int counter = 0;
        try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
            writer.beginArray();
            for(McmeEntity entity: ((BukkitCommandSender)sender).getSelectedEntities()) {
                if(entity instanceof VirtualEntity) {
                    VirtualEntityFactory factory = ((VirtualEntity)entity).getFactory();
                    if(writeDefaults) factory.withWriteDefaultValuesToFile(true);
                    gson.toJson(factory, VirtualEntityFactory.class,writer);
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

}
