package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.MovementEngine;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.ArrowMovePacket;
import com.mcmiddleearth.entities.protocol.packets.ProjectileSpawnPacket;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Projectile extends SimpleNonLivingEntity {

    private final Vector gravity;

    private final MovementEngine movementEngine = new MovementEngine(this);

    private final McmeEntity shooter;

    private boolean onGround = false;

    public Projectile(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
        setVelocity(new Vector(0, FastMath.sin(factory.getMaxRotationStep()/180*FastMath.PI)*factory.getKnockBackBase(),
                                 FastMath.cos(factory.getMaxRotationStep()/180*FastMath.PI)*factory.getKnockBackBase()));
        spawnPacket = new ProjectileSpawnPacket(this, getVelocity(), factory.getShooter());
        movePacket = new ArrowMovePacket();
        teleportPacket = new ArrowMovePacket();
        gravity = new Vector(0,-0.05000000074505806/*factory.getMaxRotationStepFlight()*/,0);
        this.shooter = factory.getShooter();
    }

    @Override
    public void doTick() {
        if(!onGround) {
            spawnPacket.update();
            Vector velocity = getVelocity().clone();//
            //Logger.getGlobal().info("FALLING: "+velocity);
            setLocation(getLocation().clone().add(velocity));
            setVelocity(velocity.multiply(0.99f).add(gravity));
//Logger.getGlobal().info("location new: "+ getLocation().getX()+" "+getLocation().getY()+" "+getLocation().getZ());
            if (movementEngine.cannotMove(velocity)) {
                onGround = true;
                new BukkitRunnable() {
                    @Override
                    public void run() {
//Logger.getGlobal().info("Terminate!");
                        terminate();
                    }
                }.runTaskLater(EntitiesPlugin.getInstance(),400);
            }
            McmeEntity collision = checkEntityCollisions();
            if(collision != null && collision != shooter) {
//Logger.getGlobal().info("Hit! Shooter: "+shooter);
                getLocation().getWorld().spawnParticle(Particle.SPELL_INSTANT, getLocation(), 1, 0, 0, 0,
                        2, null, true);
                collision.receiveAttack(shooter,1,1);
                terminate();

            }
        }
    }

   private McmeEntity checkEntityCollisions() {
        if(getBoundingBox().isZero()) return null;
        BoundingBox entityBB = getBoundingBox().getBoundingBox().clone();
        Collection<McmeEntity> closeEntities = EntitiesPlugin.getEntityServer()
                .getEntitiesAt(getLocation(), (int) (entityBB.getWidthX()*2+1),
                        (int)(entityBB.getHeight()*2+1),
                        (int)(entityBB.getWidthZ()*2+1));
        for(McmeEntity search: closeEntities) {
            if (search != this && search.getBoundingBox() != null && !search.getBoundingBox().isZero()
                    && entityBB.overlaps(search.getBoundingBox().getBoundingBox())) {
                return search;
            }
        }
        return null;
    }
}
