package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import com.mcmiddleearth.entities.entities.composite.bones.SpeechBalloonLayout;

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
                    .then(HelpfulRequiredArgumentBuilder.argument("duration", integer())
                        .then(HelpfulRequiredArgumentBuilder.argument("text", greedyString())
                            .executes(context -> say(context.getSource(), context.getArgument("side", String.class),
                                                     context.getArgument("text", String.class),
                                                     context.getArgument("duration", Integer.class))))));
        return commandNodeBuilder;
    }

    private int say(McmeCommandSender sender, String side, String text, int duration) {
        /*String[] words = text.split(" ");
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
        }*/
        VirtualEntity entity = (VirtualEntity) ((RealPlayer) sender).getSelectedEntities().iterator().next();
        //entity.say(lines.toArray(new String[0]), 200);
        SpeechBalloonLayout.Position position = (side.equals("l")? SpeechBalloonLayout.Position.LEFT:
                (side.equals("t")? SpeechBalloonLayout.Position.TOP: SpeechBalloonLayout.Position.RIGHT));
        SpeechBalloonLayout layout = new SpeechBalloonLayout(position, SpeechBalloonLayout.Width.OPTIMAL)
                .withDuration(duration)
                .withMessage(text);
        entity.say(layout);
        return 0;
    }



}
