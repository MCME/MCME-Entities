package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.EntityAPI;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.entities.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class EntitiesCommand extends McmeEntitiesCommandHandler {

    public EntitiesCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> !((sender instanceof RealPlayer)
                                      && !((RealPlayer)sender).getBukkitPlayer().hasPermission(Permission.ADMIN.getNode())))
                .then(HelpfulLiteralBuilder.literal("reload")
                        .executes(context -> {
                            EntitiesPlugin.getInstance().reloadConfig();
                            context.getSource().sendMessage(new ComponentBuilder("Configuration reloaded!").create());
                            return 0;
                        }))
                .then(HelpfulLiteralBuilder.literal("restart")
                        .executes(context -> {
                            EntitiesPlugin.getInstance().restartServer();
                            context.getSource().sendMessage(new ComponentBuilder("Entities server restarted!").create());
                            return 0;
                        }));
        return commandNodeBuilder;
    }

}
