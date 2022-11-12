package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.command.argument.McmeEntityArgument;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mojang.brigadier.context.CommandContext;
import net.md_5.bungee.api.chat.ComponentBuilder;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class TagCommand extends McmeEntitiesCommandHandler {

    public TagCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> !((RealPlayer)sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulRequiredArgumentBuilder.argument("entity", new McmeEntityArgument())
                    .then(HelpfulLiteralBuilder.literal("add")
                        .then(HelpfulRequiredArgumentBuilder.argument("tag",word())
                            .executes(this::addTag)))
                    .then(HelpfulLiteralBuilder.literal("remove")
                        .then(HelpfulRequiredArgumentBuilder.argument("tag",word())
                            .executes(this::removeTag))));
        return commandNodeBuilder;
    }

    private int addTag(CommandContext<McmeCommandSender> context) {
        String entityName = context.getArgument("entity",String.class);
        String tag = context.getArgument("tag", String.class);
        McmeEntity entity = EntitiesPlugin.getEntityServer().getEntity(entityName);
        if(entity != null) {
            if(entity.hasTag(tag)) {
                context.getSource().sendMessage(new ComponentBuilder("Entity " + entityName + " already has tag "+tag+".").create());
            } else {
                entity.addTag(tag);
                context.getSource().sendMessage(new ComponentBuilder("Added tag " + tag + " to entity " + entityName+".").create());
            }
        } else {
            context.getSource().sendMessage(new ComponentBuilder("No entity found by name " + entityName+".").create());
        }
        return 0;
    }

    private int removeTag(CommandContext<McmeCommandSender> context) {
        String entityName = context.getArgument("entity",String.class);
        String tag = context.getArgument("tag", String.class);
        McmeEntity entity = EntitiesPlugin.getEntityServer().getEntity(entityName);
        if(entity != null) {
            if(!entity.hasTag(tag)) {
                context.getSource().sendMessage(new ComponentBuilder("Entity " + entityName + " does not have tag "+tag+".").create());
            } else {
                entity.removeTag(tag);
                context.getSource().sendMessage(new ComponentBuilder("Removed tag " + tag + " from entity " + entityName+".").create());
            }
        } else {
            context.getSource().sendMessage(new ComponentBuilder("No entity found by name " + entityName+".").create());
        }
        return 0;
    }

}
