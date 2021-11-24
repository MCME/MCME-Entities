package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.ai.movement.MovementEngine;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.simple.SimpleNonLivingEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.ArrowMovePacket;
import com.mcmiddleearth.entities.protocol.packets.simple.ProjectileSpawnPacket;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

public class Projectile extends SimpleNonLivingEntity {

    private final Vector gravity = new Vector(0,-0.05000000074505806,0);;

    private final MovementEngine movementEngine = new MovementEngine(this);

    private final McmeEntity shooter;

    private boolean onGround = false;

    private final float damage, knockBack;

    private static final Random random = new Random();

    public Projectile(int entityId, VirtualEntityFactory factory) throws InvalidLocationException, InvalidDataException {
        super(entityId, factory);
//Logger.getGlobal().info("factory Yaw: "+factory.getLocation().getYaw()+" Pitch: "+factory.getLocation().getPitch());
        setVelocity(factory.getLocation().getDirection().multiply(factory.getProjectileVelocity()));
//Logger.getGlobal().info("PVelo: "+factory.getProjectileVelocity()+" - "+getVelocity());
//Logger.getGlobal().info("entity Yaw: "+getLocation().getYaw()+" Pitch: "+getLocation().getPitch());

        /*setVelocity(new Vector(0,
                               FastMath.sin(factory.getLocation().getPitch()/180*FastMath.PI)*factory.getKnockBackBase(),
                               FastMath.cos(factory.getLocation().getPitch()/180*FastMath.PI)*factory.getKnockBackBase()));*/
        spawnPacket = new ProjectileSpawnPacket(this, getVelocity(), factory.getShooter());
        movePacket = new ArrowMovePacket();
        teleportPacket = new ArrowMovePacket();
        this.shooter = factory.getShooter();
        this.damage = factory.getProjectileDamage();
        this.knockBack = factory.getKnockBackBase();
    }

    @Override
    public void doTick() {
        if(!onGround) {
            spawnPacket.update();
            Vector velocity = getVelocity().clone();//
            //Logger.getGlobal().info("FALLING: "+velocity);
            double speed = velocity.length();
            int steps = (int)(speed / 0.1)+1;
            Vector stepVelocity = new Vector(getVelocity().getX()/steps,getVelocity().getY()/steps,getVelocity().getZ()/steps);
            for(int i = 0; i< steps; i++) {
                setLocation(getLocation().clone().add(stepVelocity));
                McmeEntity collision = checkEntityCollisions();
                if (collision != null && collision != shooter) {
//Logger.getGlobal().info("Hit! Shooter: "+shooter);
                    getLocation().getWorld().spawnParticle(Particle.SPELL_INSTANT, getLocation(), 1, 0, 0, 0,
                            2, null, true);
                    collision.receiveAttack(shooter, damage, knockBack);
                    terminate();
                    if(getDependingEntity() != null) {
                        getDependingEntity().remove();
                    }
                    break;
                }
                if (movementEngine.cannotMove(stepVelocity)) {
//Logger.getGlobal().info("Block!");
                    onGround = true;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            //Logger.getGlobal().info("Terminate!");
                            terminate();
                        }
                    }.runTaskLater(EntitiesPlugin.getInstance(), 400);
                    break;
                }
//Logger.getGlobal().info("location new: " + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ());
            }
            setVelocity(velocity.multiply(0.99f).add(gravity));
        }
    }

   private McmeEntity checkEntityCollisions() {
        if(getBoundingBox().isZero()) return null;
        BoundingBox entityBB = getBoundingBox().getBoundingBox().clone();
        Collection<McmeEntity> closeEntities = EntitiesPlugin.getEntityServer()
                .getEntitiesAt(getLocation(), (int) (entityBB.getWidthX()*2+1),
                        (int)(entityBB.getHeight()*2+1),
                        (int)(entityBB.getWidthZ()*2+1));
        //closeEntities.addAll(EntitiesPlugin.getEntityServer().getMcmePlayers());
        for(McmeEntity search: closeEntities) {
/*            if(search instanceof RealPlayer) {
Logger.getGlobal().info("player: "+search.getBoundingBox().isZero());
                search.getBoundingBox().setLocation(search.getLocation());
Logger.getGlobal().info("bb: "+search.getBoundingBox().getBoundingBox());
            }*/
            if (!(search instanceof Projectile)
                    && search.getBoundingBox() != null && !search.getBoundingBox().isZero()
                    && entityBB.overlaps(search.getBoundingBox().getBoundingBox())) {
                return search;
            }
//Logger.getGlobal().info("next");
        }
        return null;
    }

    public McmeEntity getShooter() {
        return shooter;
    }

    public static VirtualEntityFactory takeAim(VirtualEntityFactory factory, Location target) {
//Logger.getGlobal().info("start : "+factory.getLocation()+" Target: "+target);
        if(!factory.getLocation().getWorld().equals(target.getWorld())) {
            return factory;
        }
        Location shooter = factory.getLocation();
        double diffX = target.getX() - shooter.getX();
        double diffY = target.getY()+(0.6666666666666666666) - shooter.getY();
        double diffZ = target.getZ() - shooter.getZ();
        double diffHorizontal = FastMath.sqrt(diffX * diffX + diffZ * diffZ);
        double randomFactor = 0.007499999832361937 * 6; // * (14- 4 * difficulty)
        Vector velocity = new Vector(diffX + random.nextGaussian()*randomFactor,
                                     diffY + diffY * diffY * 0.01 + diffY*diffY*diffY*0.0001
                                              + diffHorizontal * 0.15/*0.20000000298023224*/ + diffHorizontal*diffHorizontal * 0.005
                                              + random.nextGaussian()*randomFactor,
                                     diffZ + random.nextGaussian()*randomFactor);
//Logger.getGlobal().info("aim velo: "+velocity);
        factory.withLocation(factory.getLocation().setDirection(velocity))
               .withProjectileVelocity(1.6f);
        //entityarrow.shoot(diffX, diffY + diffHorizontal * 0.20000000298023224, diffZ, 1.6f, 14 - this.world.getDifficulty().a() * 4);
        return factory;
    }
}
