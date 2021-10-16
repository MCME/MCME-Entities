package com.mcmiddleearth.entities.effect;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.entities.McmeEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Explosion {

    private Location location;

    private double radius;

    private double damage;

    private double knockback = 1;

    private double velocity = 1;

    private Set<McmeEntity> unaffected = new HashSet<>();

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

            private Set<McmeEntity> affected = new HashSet<>();

            @Override
            public void run() {
                double square = currentRadius * currentRadius;
                int ceil = (int) Math.ceil(currentRadius);
                Collection<McmeEntity> entities = EntitiesPlugin.getEntityServer().getEntitiesAt(location,
                                                                                        ceil,ceil,ceil);
                entities.stream().filter(entity -> location.distanceSquared(entity.getLocation()) <= square
                                                && !entities.contains(entity))
                        .forEach(entity -> {
                            entity.receiveAttack(damager,damage,knockback);
                            entities.add(entity);
                        });
                if(currentRadius == radius) cancel();
                else currentRadius = Math.min(currentRadius+=velocity,radius);
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

    public Explosion addUnaffected(McmeEntity unaffected) {
        this.unaffected.add(unaffected);
        return this;
    }
}
