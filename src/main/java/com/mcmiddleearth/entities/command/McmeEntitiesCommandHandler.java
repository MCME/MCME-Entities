package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.SimpleTabCompleteRequest;
import com.mcmiddleearth.command.TabCompleteRequest;
import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.goal.GoalType;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.RealPlayer;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Logger;

public abstract class McmeEntitiesCommandHandler extends AbstractCommandHandler implements TabExecutor {

    public McmeEntitiesCommandHandler(String command) {
        super(command);
    }

    protected VirtualEntityFactory getFactory(McmeCommandSender sender, String type, String name, String goal, String dataFile) {
        RealPlayer player = (RealPlayer) sender;
        VirtualEntityFactory factory = player.getEntityFactory();
//AttributeInstance attackSpeed = factory.getAttributes().get(Attribute.GENERIC_ATTACK_SPEED);
//if(attackSpeed!=null) Logger.getGlobal().info("getFactory: Attack speed: "+attackSpeed.getBaseValue()+" -> "+attackSpeed.getValue());
//Logger.getGlobal().info("Factory: "+factory);
//Logger.getGlobal().info("Factory movement type: "+factory.getMovementType().name());
        McmeEntityType entityType = McmeEntityType.valueOf(type);
        if(entityType !=null)
        {
            factory.withEntityType(entityType);
        }
        if(name !=null)
        {
            factory.withName(name);
        }
        if(dataFile!=null && !dataFile.equals("")) {
            factory.withDataFile(dataFile);
        }
        if(factory.getLocation()==null)
        {
            factory.withEntityForSpawnLocation(player);
        }
        if(goal !=null)
        {
            getOrCreateGoalFactory(factory);
            try {
                factory.getGoalFactory().withGoalType(GoalType.valueOf(goal.toUpperCase()));
            } catch (IllegalArgumentException ignore) { }
            if(factory.getGoalFactory().getTargetLocation()==null) {
                factory.getGoalFactory().withTargetLocation(player.getSelectedPoints().stream()
                                           .findFirst().orElse(player.getLocation()));
            }
            if(player.getSelectedTargetEntity()!=null)
            {
                factory.getGoalFactory().withTargetEntity(player.getSelectedTargetEntity());
            }
            if(factory.getGoalFactory().getTargetEntity() == null) {
                factory.getGoalFactory().withTargetEntity(player);
            }
    //Logger.getGlobal().info("Factory tar type: "+factory.getTargetEntity());
            if(player.getSelectedPoints() != null && player.getSelectedPoints().size()>0) {
                factory.getGoalFactory().withCheckpoints(player.getSelectedPoints().toArray(new Location[0]));
            }
        }
        return factory;
    }

    protected VirtualEntityGoalFactory getOrCreateGoalFactory(VirtualEntityFactory factory) {
        if(factory.getGoalFactory()==null) {
            factory.withGoalFactory(new VirtualEntityGoalFactory(GoalType.HOLD_POSITION));
        }
        return factory.getGoalFactory();
    }

    protected Vector parseVector(Player player, String value) throws IllegalArgumentException {
        if(value.equalsIgnoreCase("@p")) {
            return player.getLocation().toVector().clone();
        }
        String[] split = value.split(" ");
        if(split.length != 3) throw new IllegalArgumentException();
        return new Vector(Double.parseDouble(split[0]),Double.parseDouble(split[1]),Double.parseDouble(split[2]));
    }

    protected Location parseLocation(Player player, String value) throws IllegalArgumentException {
        Vector vector = parseVector(player, value);
        return vector.toLocation(player.getWorld());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        McmeCommandSender wrappedSender = EntitiesPlugin.wrapCommandSender(sender);
        execute(wrappedSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
//Logger.getGlobal().info("tabComplete 1");
        TabCompleteRequest request = new SimpleTabCompleteRequest(EntitiesPlugin.wrapCommandSender(sender),
                                                                  String.format("/%s %s", alias, Joiner.on(' ').join(args)));
        onTabComplete(request);
//Logger.getGlobal().info("tabComplete 1");
        return request.getSuggestions();
    }

}
