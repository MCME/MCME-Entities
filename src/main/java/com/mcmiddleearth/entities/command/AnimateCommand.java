package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.command.argument.AnimationIdArgument;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.composite.BakedAnimationEntity;
import net.md_5.bungee.api.chat.ComponentBuilder;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

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
        for(McmeEntity entity :player.getSelectedEntities()) {
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
                    ((BakedAnimationEntity) entity).setManualAnimationControl(false);
                } else if(animationId.equals("manual")) {
                        ((BakedAnimationEntity)entity).setManualAnimationControl(true);
                } else {
                    //((BakedAnimationEntity) entity).setManualAnimationControl(true);
                    ((BakedAnimationEntity) entity).setAnimation(animationId, true, null,0);
                }
            }
        });
        if(animationId.equals("auto")) {
            sender.sendMessage(new ComponentBuilder("Setting automated animation mode." + " for "
                    + player.getSelectedEntities().size() + " entities.").create());
        } else if(animationId.equals("manual")) {
                sender.sendMessage(new ComponentBuilder("Setting manual animation mode."+" for "
                        +player.getSelectedEntities().size()+" entities.").create());
        } else {
            sender.sendMessage(new ComponentBuilder("Playing animation " + animationId+" for "
                    +player.getSelectedEntities().size()+" entities.").create());
        }
        return 0;
    }

}
