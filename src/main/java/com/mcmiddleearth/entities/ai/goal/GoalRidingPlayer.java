package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.MovementType;
import com.mcmiddleearth.entities.api.VirtualEntityGoalFactory;
import com.mcmiddleearth.entities.entities.RealPlayer;
import com.mcmiddleearth.entities.entities.composite.WingedFlightEntity;
import com.mcmiddleearth.entities.protocol.packets.AddPassengerPacket;
import com.mcmiddleearth.entities.protocol.packets.VehicleMovePacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.Vector;

import java.util.logging.Logger;

public class GoalRidingPlayer extends GoalVirtualEntity implements Listener {

    private final Player player;

    private final WingedFlightEntity entity;

    private Skeleton anchor;

    private float yaw;
    private final float step = 10;

    private final VehicleMovePacket movePacket;

    public GoalRidingPlayer(WingedFlightEntity entity, VirtualEntityGoalFactory factory) {
        super(entity, factory);
        this.player = ((RealPlayer)factory.getTargetEntity()).getBukkitPlayer();
        this.entity = entity;
        yaw = entity.getYaw();
        AddPassengerPacket addPacket = new AddPassengerPacket(entity.getBones().stream().findFirst().get().getEntityId(),
                                         player.getEntityId());
        movePacket = new VehicleMovePacket(entity);
        //anchor = entity.getLocation().getWorld().spawn(entity.getLocation(), Skeleton.class);
        //anchor.setInvisible(true);
        //anchor.setInvulnerable(true);
        //anchor.setBasePlate(false);
        //anchor.setGravity(false);
        //anchor.setAI(false);
        //anchor.addPassenger(player);
        Bukkit.getPluginManager().registerEvents(this, EntitiesPlugin.getInstance());
    }

    @Override
    public void doTick() {
        if(getEntity().isDead() || getEntity().isTerminated()) {
            HandlerList.unregisterAll(this);
        }
        if(!isFinished()) {
            //anchor.teleport(getEntity().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            movePacket.update();
            movePacket.send(player);
            setYaw(Math.min(entity.getCurrentYaw()+90,Math.max(entity.getCurrentYaw()-90,yaw)));
        }
    }


    @Override
    public Vector getDirection() {
        if(!isFinished() && (getEntity() instanceof WingedFlightEntity)
                && (getEntity().getMovementType().equals(MovementType.FLYING)|| getEntity().getMovementType().equals(MovementType.GLIDING))) {
            WingedFlightEntity winged = (WingedFlightEntity) getEntity();
            Location loc = winged.getLocation().clone();
            loc.setPitch(winged.getCurrentPitch());
            loc.setYaw(winged.getCurrentYaw());
            return loc.getDirection();
        } else {
            return new Vector(1,0,0);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().equals(player)) {
            event.setCancelled(true);
            Action action = event.getAction();
            if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
                yaw += step;
            } else if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                yaw -= step;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleLeave(VehicleExitEvent event) {
Logger.getGlobal().info("vehicle leave: "+event.getExited()+" player: "+player);
        if(event.getExited().equals(player)) {
            HandlerList.unregisterAll(this);
            setFinished();
            anchor.remove();
        }
    }
}
