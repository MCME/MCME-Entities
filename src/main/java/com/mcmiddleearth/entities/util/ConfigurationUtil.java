package com.mcmiddleearth.entities.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class ConfigurationUtil {

    public static Vector getVector(ConfigurationSection config, String key, Vector defaultValue) {
        if(config == null) return defaultValue;
        ConfigurationSection vectorConfig = config.getConfigurationSection(key);
        if(vectorConfig == null) return defaultValue;
        double x = vectorConfig.getDouble("x",0);
        double y = vectorConfig.getDouble("y",0);
        double z = vectorConfig.getDouble("z",0);
        return new Vector(x,y,z);
    }
}
