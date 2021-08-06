package com.mcmiddleearth.entities.util;

import com.mcmiddleearth.entities.entities.RealPlayer;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WorldEditUtil {

    public static Region getSelection(Player player) {
        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            return null;
        }
        LocalSession session = worldEdit.getSession(player);

        try {
            return session.getSelection(BukkitAdapter.adapt(player).getWorld());
        } catch (IncompleteRegionException var4) {
            //Logger.getLogger(WorldEditUtil.class.getName()).log(Level.SEVERE, (String)null, var4);
            return null;
        }
    }

    public static World getWEWorld(Player player) {
        WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if(worldEdit == null) {
            return null;
        }
        LocalSession session = worldEdit.getSession(player);

        try {
            return BukkitAdapter.adapt(session.getSelectionWorld());
        } catch (Exception var4) {
            //Logger.getLogger(WorldEditUtil.class.getName()).log(Level.SEVERE, (String)null, var4);
            return null;
        }
    }

    public static List<Vector> getSelectedPoints(Player player) {
        Region selection = WorldEditUtil.getSelection(player);
        if(selection == null) {
            return null;
        }
        if(selection instanceof Polygonal2DRegion) {
            return ((Polygonal2DRegion) selection).getPoints().stream()
                    .map(blockVector -> new Vector(blockVector.getX(),selection.getMinimumPoint().getY(),blockVector.getZ())).collect(Collectors.toList());
        }
        if(selection instanceof CuboidRegion) {
            List<Vector> result = new ArrayList<>();
            BlockVector3 vec3 = ((CuboidRegion) selection).getMinimumPoint();
            result.add(new Vector(vec3.getX(),vec3.getY(), vec3.getZ()));
            vec3 = ((CuboidRegion) selection).getMaximumPoint();
            result.add(new Vector(vec3.getX(),vec3.getY(), vec3.getZ()));
            return result;
        }
        return null;
    }


}
