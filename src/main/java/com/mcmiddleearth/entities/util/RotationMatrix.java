package com.mcmiddleearth.entities.util;

import org.apache.commons.math3.util.FastMath;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class RotationMatrix {

    private final Vector first, second, third;

    public RotationMatrix(Vector first, Vector second, Vector third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static Vector fastRotateZ(Vector vector, float angle) {
        double radian = angle/180*FastMath.PI;
        double sin = FastMath.sin(radian);
        double cos = FastMath.cos(radian);
        return new Vector(cos*vector.getX()-sin*vector.getY(),sin*vector.getX()+cos*vector.getY(),vector.getZ());
    }

    public static Vector fastRotateY(Vector vector, float angle) {
        double radian = angle/180*FastMath.PI;
        double sin = FastMath.sin(radian);
        double cos = FastMath.cos(radian);
        return new Vector(cos*vector.getX()+sin*vector.getZ(),vector.getY(),-sin*vector.getX()+cos*vector.getZ());
    }

    public static Vector fastRotateX(Vector vector, float angle) {
        double radian = angle/180*FastMath.PI;
        double sin = FastMath.sin(radian);
        double cos = FastMath.cos(radian);
        return new Vector(vector.getX(),cos*vector.getY()-sin*vector.getZ(),sin*vector.getY()+cos*vector.getZ());
    }

    public static EulerAngle rotateXEulerAngleDegree(EulerAngle source, double rotationAngle) {
        EulerAngle radian = new EulerAngle(degreeToRadian(source.getX()),degreeToRadian(source.getY()),degreeToRadian(source.getZ()));
        radian = rotateXEulerAngleRadian(radian, degreeToRadian(rotationAngle));
        return new EulerAngle(radianToDegree(radian.getX()),radianToDegree(radian.getY()),radianToDegree(radian.getZ()));
    }

    public static EulerAngle rotateXZEulerAngleDegree(EulerAngle source, double pitch, double roll) {
        EulerAngle radian = new EulerAngle(degreeToRadian(source.getX()),degreeToRadian(source.getY()),degreeToRadian(source.getZ()));
        radian = rotateXZEulerAngleRadian(radian, degreeToRadian(pitch),degreeToRadian(roll));
        return new EulerAngle(radianToDegree(radian.getX()),radianToDegree(radian.getY()),radianToDegree(radian.getZ()));
    }

    public static EulerAngle rotateXEulerAngleRadian(EulerAngle source, double rotationAngle) {
        //double radian = rotationAngle/180*FastMath.PI;
        double sinX = FastMath.sin(source.getX());
        double cosX = FastMath.cos(source.getX());
        double sinY = FastMath.sin(source.getY());
        double cosY = FastMath.cos(source.getY());
        double sinZ = FastMath.sin(source.getZ());
        double cosZ = FastMath.cos(source.getZ());
        double sinA = FastMath.sin(rotationAngle);
        double cosA = FastMath.cos(rotationAngle);
        double R11 = cosY*cosZ;
        double R21 = cosA * (cosY*sinZ) + sinA*sinY;
        double R31 = sinA * (cosY*sinZ) - cosA*sinY;
        double R32 = sinA * (sinX * sinY * sinZ + cosX * cosZ) + cosA * sinX * cosY;
        double R33 = sinA * (cosX * sinY * sinZ - sinX * cosZ) + cosA * cosX * cosY;
        double rotatedX = FastMath.atan(R32 / R33);
        double rotatedY = FastMath.asin(-R31);
        double rotatedZ = FastMath.atan(R21 / R11);
        if(FastMath.signum(R32)!=FastMath.signum(rotatedX)) {
//Logger.getGlobal().info("ShiftX: "+ FastMath.signum(R32));
            rotatedX = rotatedX + FastMath.signum(R32)*FastMath.PI;
        }
        if(FastMath.signum(R21)!=FastMath.signum(rotatedZ)) {
//Logger.getGlobal().info("ShiftZ: "+ FastMath.signum(R21));
            rotatedZ = rotatedZ + FastMath.signum(R21)*FastMath.PI;
        }
/*        if(R32>0 && rotatedX<0) {
Logger.getGlobal().info("ShiftX+");
            rotatedX += FastMath.PI;
        } else if(R32<0 && rotatedX>0) {
            rotatedX -= FastMath.PI;
Logger.getGlobal().info("ShiftX-");
        }
        if(R21>0 && rotatedZ<0) {
Logger.getGlobal().info("ShiftZ+");
            rotatedZ += FastMath.PI;
        } else if(R21<0 && rotatedZ>0) {
Logger.getGlobal().info("ShiftZ-");
            rotatedZ -= FastMath.PI;
        }*/
        return new EulerAngle(rotatedX,rotatedY,rotatedZ);
    }

    public static EulerAngle rotateXZEulerAngleRadian(EulerAngle source, double pitch, double roll) {
        double sinX = FastMath.sin(source.getX());
        double cosX = FastMath.cos(source.getX());
        double sinY = FastMath.sin(source.getY());
        double cosY = FastMath.cos(source.getY());
        double sinZ = FastMath.sin(source.getZ());
        double cosZ = FastMath.cos(source.getZ());
        double sinP = FastMath.sin(pitch);
        double cosP = FastMath.cos(pitch);
        double sinR = FastMath.sin(roll);
        double cosR = FastMath.cos(roll);
        double R11 = cosY * cosZ * cosR - cosY * sinZ * sinR;
        double R21 = sinY * sinP + cosY * cosZ * cosP * sinR + cosY * cosP * cosR * sinZ;
        double R31 = -cosP * sinY + cosY * cosZ * sinP * sinR + cosY * cosR * sinZ * sinP;
        double R32 = cosY * cosP * sinX + cosR * sinP * (cosX * cosZ + sinX * sinY * sinZ) + sinP * sinR * (-cosX * sinZ + cosZ * sinX * sinY);
        double R33 = cosX * cosY * cosP + cosR * sinP * (-cosZ * sinX + cosX * sinY * sinZ) + sinP * sinR * (sinX * sinZ + cosX * cosZ * sinY);
        double rotatedX = FastMath.atan(R32 / R33);
        double rotatedY = FastMath.asin(-R31);
        double rotatedZ = FastMath.atan(R21 / R11);
        if(FastMath.signum(R32)!=FastMath.signum(rotatedX)) {
//Logger.getGlobal().info("ShiftX: "+ FastMath.signum(R32));
            rotatedX = rotatedX + FastMath.signum(R32)*FastMath.PI;
        }
        if(FastMath.signum(R21)!=FastMath.signum(rotatedZ)) {
//Logger.getGlobal().info("ShiftZ: "+ FastMath.signum(R21));
            rotatedZ = rotatedZ + FastMath.signum(R21)*FastMath.PI;
        }
        return new EulerAngle(rotatedX,rotatedY,rotatedZ);
    }

    public static double degreeToRadian(double angle) {
        return angle/180*FastMath.PI;
    }

    public static double radianToDegree(double angle) {
        return angle/FastMath.PI*180;
    }

    public Vector multiply(Vector vector) {
        return new Vector(first.getX()*vector.getX()+second.getX()*vector.getY()+third.getX()*vector.getZ(),
                first.getY()*vector.getX()+second.getY()*vector.getY()+third.getY()*vector.getZ(),
                first.getZ()*vector.getZ()+second.getZ()*vector.getY()+third.getZ()*vector.getZ());
    }

    public RotationMatrix multiply(RotationMatrix other) {
        throw new UnsupportedOperationException();
    }
}
