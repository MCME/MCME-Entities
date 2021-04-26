package com.mcmiddleearth.entities.util;

import org.bukkit.util.Vector;

public class RotationMatrix {

    private final Vector first, second, third;

    public RotationMatrix(Vector first, Vector second, Vector third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static Vector fastRotateY(Vector vector, float angle) {
        double radian = angle/180*Math.PI;
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        return new Vector(cos*vector.getX()+sin*vector.getZ(),vector.getY(),-sin*vector.getX()+cos*vector.getZ());
    }

    public Vector multiply(Vector vector) {
        return new Vector(first.getX()*vector.getX()+second.getX()*vector.getY()+third.getX()*vector.getZ(),
                          first.getY()*vector.getX()+second.getY()*vector.getY()+third.getY()*vector.getZ(),
                          first.getZ()*vector.getZ()+second.getZ()*vector.getY()+third.getZ()*vector.getZ());
    }

    public RotationMatrix multiply(RotationMatrix other) {
        return null;
    }
}
