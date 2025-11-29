package io.github.foundationgames.splinecart.util;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.*;

public record Interpolator(InterpolationResult res, float dist, Vector3fc offset, Vector3fc grad0, Vector3fc grad1, Quaternionfc rot0, Quaternionfc rot1, Quaternionf rotMut, Vector3f ngrad, Vector3f basisGrad, Vector3f axis, Matrix3f rot) {

    public static Interpolator create(TrackMarkerBlockEntity start, TrackMarkerBlockEntity other) {
        Matrix3fc pose0 = start.pose.pose();
        Matrix3fc pose1 = other.pose.pose();
        float factor = MathHelper.sqrt((float) start.getPos().getSquaredDistance(other.getPos()));
        var grad0 = new Vector3f(0, 0, 1).mul(pose0);
        var grad1 = new Vector3f(0, 0, 1).mul(pose1);
        var diff0 = other.getPos().subtract(start.getPos());
        var diff = new Vector3f(diff0.getX(), diff0.getY(), diff0.getZ());
        var rot0 = pose0.getNormalizedRotation(new Quaternionf());
        var rot1 = pose1.getNormalizedRotation(new Quaternionf());
        return new Interpolator(new InterpolationResult(), factor, diff, grad0, grad1, rot0, rot1, new Quaternionf(), new Vector3f(), new Vector3f(), new Vector3f(), new Matrix3f());
    }


    public void interpolate(float t) {
        cubicHermiteSpline(t, dist, offset, grad0, grad1, res);
        res.gradient().normalize(ngrad);

        res.basis().set(rot0.nlerp(rot1, t, rotMut));
        basisGrad.set(0, 0, 1).mul(res.basis());
        ngrad.cross(basisGrad, axis);

        if (axis.length() > 0) {
            axis.normalize();
            float angleToNewBasis = basisGrad.angleSigned(ngrad, axis);
            if (angleToNewBasis != 0) {
                rot.identity().rotate(angleToNewBasis, axis).mul(res.basis(), res.basis()).normal();
            }
        }
    }

    public static void cubicHermiteSpline(float t, float distance, Vector3fc diff, Vector3fc clientVelocity, Vector3fc serverVelocity, InterpolationResult res) {
        diff.mul(6 * t - 6 * t * t, res.gradient());
        clientVelocity.mulAdd((3 * t * t - 4 * t + 1) * distance, res.gradient(), res.gradient());
        serverVelocity.mulAdd((3 * t * t - 2 * t) * distance, res.gradient(), res.gradient());

        clientVelocity.mul((t * t * t - 2 * t * t + t) * distance, res.translation());
        diff.mulAdd(-2 * t * t * t + 3 * t * t, res.translation(), res.translation());
        serverVelocity.mulAdd((t * t * t - t * t) * distance, res.translation(), res.translation());
    }
}
