package io.github.foundationgames.splinecart.util;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record Pose() {
    public static void cubicHermiteSpline(float t, float factor, Vector3dc clientPos, Vector3fc clientVelocity, Vector3dc serverPos, Vector3fc serverVelocity, InterpolationResult res) {
        var temp = new Vector3f();
        var diff = new Vector3f(new Vector3d(serverPos).sub(clientPos));

        res.gradient().set(temp.set(diff).mul(6*t - 6*t*t))
                .add(temp.set(clientVelocity).mul(3*t*t - 4*t + 1).mul(factor))
                .add(temp.set(serverVelocity).mul(3*t*t - 2*t).mul(factor));

        res.translation().zero()
                .add(temp.set(clientVelocity).mul(t*t*t - 2*t*t + t).mul(factor))
                .add(temp.set(diff).mul(-2*t*t*t + 3*t*t))
                .add(temp.set(serverVelocity).mul(t*t*t - t*t).mul(factor));
    }
}
