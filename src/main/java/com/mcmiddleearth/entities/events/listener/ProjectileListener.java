package com.mcmiddleearth.entities.events.listener;

import com.mcmiddleearth.entities.EntitiesPlugin;
import com.mcmiddleearth.entities.api.McmeEntityType;
import com.mcmiddleearth.entities.api.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.exception.InvalidDataException;
import com.mcmiddleearth.entities.exception.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

public class ProjectileListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        Location location = projectile.getLocation();
        location.setDirection(projectile.getVelocity());
        McmeEntityType type = new McmeEntityType(projectile.getType());
        double damage = 0;
        double knockback = 0;
        if(projectile instanceof AbstractArrow) {
            damage = ((AbstractArrow) projectile).getDamage();
            knockback = ((AbstractArrow) projectile).getKnockbackStrength();
        }
        McmeEntity shooter = null;
        if(projectile.getShooter() instanceof Player) {
            shooter = EntitiesPlugin.getEntityServer().getOrCreateMcmePlayer((Player) projectile.getShooter());
        }
        VirtualEntityFactory factory = new VirtualEntityFactory(type, location)
                .withShooter(shooter)
                .withProjectileVelocity((float)projectile.getVelocity().length())
                .withProjectileDamage((float)damage)
                .withKnockBackBase((float)knockback)
                .withKnockBackPerDamage(0)
                .withWhitelist(Collections.singleton(UUID.randomUUID()))
                .withDependingEntity(projectile);
        try {
            EntitiesPlugin.getEntityServer().spawnEntity(factory);
        } catch (InvalidLocationException | InvalidDataException e) {
            e.printStackTrace();
        }
        //event.setCancelled(true);
    }
}
