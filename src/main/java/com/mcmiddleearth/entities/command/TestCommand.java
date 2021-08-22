package com.mcmiddleearth.entities.command;

import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.entities.Permission;
import com.mcmiddleearth.entities.ai.goal.GoalPath;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.ai.goal.GoalVirtualEntity;
import com.mcmiddleearth.entities.ai.pathfinding.Path;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.VirtualEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class TestCommand extends McmeEntitiesCommandHandler {

    public TestCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> (sender instanceof RealPlayer)
                        && ((RealPlayer) sender).getBukkitPlayer().hasPermission(Permission.USER.getNode()))
                .then(HelpfulLiteralBuilder.literal("path")
                        .executes(context -> findPath(context.getSource())));
        return commandNodeBuilder;
    }

    private int findPath(McmeCommandSender sender) {
        RealPlayer player = ((RealPlayer)sender);
        VirtualEntity entity = (VirtualEntity) player.getSelectedEntities().iterator().next();
        try {
            GoalVirtualEntity goal = new VirtualEntityGoalFactory(GoalType.FOLLOW_ENTITY)
                    .withTargetEntity(player)
                    .build(entity);
            goal.update();
            Path path = ((GoalPath)goal).getPath();
            if(path!=null) {
                Logger.getGlobal().info("Target: " +path.getTarget());
                Logger.getGlobal().info("Start: " +path.getStart());
                Logger.getGlobal().info("End: " +path.getEnd());
                path.getPoints().forEach(point -> {
                    Logger.getGlobal().info(point.getBlockX()+" "+point.getBlockY()+" "+point.getBlockZ());
                    player.getLocation().getWorld()
                            .dropItem(point.toLocation(player.getLocation().getWorld()), new ItemStack(Material.STONE));
                });
            } else {
                Logger.getGlobal().info("no path found");
            }
        } catch (Exception ignore) {}
        return 0;
    }

}
