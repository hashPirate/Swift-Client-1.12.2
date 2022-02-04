package me.pignol.swift.api.util.objects;

public class Vec3dM {

    public double x;
    public double y;
    public double z;

    public Vec3dM() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3dM(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
