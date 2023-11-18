package com.team254.lib.geometry;

/**
 * A rotation in a 2d coordinate frame represented a point on the unit circle (cosine and sine).
 * <p>
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 */
public class Rotation2dGF extends Rotation2d {

    protected double cos_angle_;
    protected double sin_angle_;

    public Rotation2dGF() {
        cos_angle_ = 1;
        sin_angle_ = 0;
    }

    public static Rotation2dGF fromRadiansGF(double angle_radians, Rotation2dGF instance) {
        instance.cos_angle_ = Math.cos(angle_radians);
        instance.sin_angle_ = Math.sin(angle_radians);
        return instance;
    }
}
