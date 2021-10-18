package com.mcmiddleearth.entities.effect;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Explosion {

    private Location location;

    private double radius;

    private double damage;

    private double knockback = 1;

    private double velocity = 1;

    private Particle particle = Particle.SPELL_INSTANT;

    private Set<McmeEntity> unaffected = new HashSet<>();

    private Set<McmeEntity> settled = new HashSet<>();

    private Set<Player> viewer = new HashSet<>();

    private McmeEntity damager;

    public Explosion(Location location, double radius, double damage) {
        this.location = location;
        this.radius = radius;
        this.damage = damage;
    }

    public void explode() {
        new BukkitRunnable() {

            private double currentRadius = velocity;
            private double currentKnockback = knockback;
            private double currentDamage = damage;

            private Set<McmeEntity> affected = new HashSet<>();

            @Override
            public void run() {
                double square = currentRadius * currentRadius;
                int ceil = (int) Math.ceil(currentRadius);
                Collection<McmeEntity> entities = EntitiesPlugin.getEntityServer().getEntitiesAt(location,
                                                                                        ceil,ceil,ceil);
//Logger.getGlobal().info("Radius: "+currentRadius+" Entities: "+entities.size()+" damage: "+damage);
                entities.stream().filter(entity -> location.distanceSquared(entity.getLocation()) <= square
                                                && !settled.contains(entity))
                        .forEach(entity -> {
                            entity.receiveAttack(damager,currentDamage,currentKnockback);
                            settled.add(entity);
                        });
                double start = FastMath.random()*5;
                double phi = start;
                while(phi < start+2*FastMath.PI) {
                    Location particleLoc = location.clone().add(new Vector(FastMath.sin(phi)*currentRadius,
                                                                           1,FastMath.cos(phi)*currentRadius));
                    double offset = velocity/2d;
                    location.getWorld().spawnParticle(particle, particleLoc, 1,offset,offset,offset,
                                                      2, null,true);
                    phi += 1.0/radius;
                }
                if(currentRadius == radius) {
//Logger.getGlobal().info("cancel");
                    cancel();
                } else {
                    currentRadius = Math.min(currentRadius+=velocity,radius);
                    currentDamage = damage * (1 - currentRadius/radius);//Math.max(0,currentDamage - 0.3);
                    currentKnockback = knockback * (1 - currentRadius/radius); //Math.max(0, currentKnockback - 1.0/radius)
                }
            }
        }.runTaskTimer(EntitiesPlugin.getInstance(),0,1);
    }

    public Explosion setKnockback(double knockback) {
        this.knockback = knockback;
        return this;
    }

    public Explosion setVelocity(double velocity) {
        this.velocity = velocity;
        return this;
    }

    public Explosion setDamager(McmeEntity damager) {
        this.damager = damager;
        return this;
    }

    public Explosion setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Explosion setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public Explosion setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public Explosion setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Explosion addUnaffected(McmeEntity unaffected) {
        this.unaffected.add(unaffected);
        return this;
    }
}
