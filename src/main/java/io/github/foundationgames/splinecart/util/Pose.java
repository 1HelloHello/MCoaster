package io.github.foundationgames.splinecart.util;

import net.minecraft.util.math.BlockPos;
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record Pose(Matrix3dc basis) {
    public static void interpolate(Pose self, Pose other, BlockPos start, BlockPos end, double t, InterpolationResult res) {
        double factor = Math.sqrt(start.getSquaredDistance(end));
        var grad0 = new Vector3d(0, 0, 1).mul(self.basis());
        var grad1 = new Vector3d(0, 0, 1).mul(other.basis());

        cubicHermiteSpline(t, factor, new Vector3d(start.getX(), start.getY(), start.getZ()) , grad0, new Vector3d(end.getX(), end.getY(), end.getZ()), grad1, res);
        var ngrad = res.gradient().normalize(new Vector3d());

        var rot0 = self.basis().getNormalizedRotation(new Quaterniond());
        var rot1 = other.basis().getNormalizedRotation(new Quaterniond());

        var rotT = rot0.nlerp(rot1, t, new Quaterniond());
        res.basis().set(rotT);

        var basisGrad = new Vector3d(0, 0, 1).mul(res.basis());
        var axis = ngrad.cross(basisGrad, new Vector3d());

        if (axis.length() > 0) {
            axis.normalize();
            double angleToNewBasis = basisGrad.angleSigned(ngrad, axis);
            if (angleToNewBasis != 0) {
                new Matrix3d().identity().rotate(angleToNewBasis, axis)
                        .mul(res.basis(), res.basis()).normal();
            }
        }
    }

    public static void cubicHermiteSpline(double t, double factor, Vector3dc clientPos, Vector3dc clientVelocity, Vector3dc serverPos, Vector3dc serverVelocity, InterpolationResult res) {
        var temp = new Vector3d();
        var diff = new Vector3d(serverPos).sub(clientPos);

        res.gradient().set(temp.set(diff).mul(6*t - 6*t*t))
                .add(temp.set(clientVelocity).mul(3*t*t - 4*t + 1).mul(factor))
                .add(temp.set(serverVelocity).mul(3*t*t - 2*t).mul(factor));

        res.translation().zero()
                .add(temp.set(clientVelocity).mul(t*t*t - 2*t*t + t).mul(factor))
                .add(temp.set(diff).mul(-2*t*t*t + 3*t*t))
                .add(temp.set(serverVelocity).mul(t*t*t - t*t).mul(factor));
    }
}
