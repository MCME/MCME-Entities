package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
                .executes(context -> removeEntity(context.getSource(),null))
                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                        .executes(context -> removeEntity(context.getSource(),context.getArgument("name",String.class))));
        return commandNodeBuilder;
    }


    private int removeEntity(McmeCommandSender sender, String name) {
        Collection<? extends McmeEntity> entities = new HashSet<>();
        if(name !=null && name.equalsIgnoreCase("all")) {
            entities = EntitiesPlugin.getEntityServer().getEntities(VirtualEntity.class);
        } else if(name != null) {
            McmeEntity entity = (EntityAPI.getEntity(name));
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
