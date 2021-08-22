package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.entities.RealPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;

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
