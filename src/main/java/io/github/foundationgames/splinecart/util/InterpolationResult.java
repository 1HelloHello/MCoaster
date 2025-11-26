package io.github.foundationgames.splinecart.util;

import org.joml.Matrix3d;
import org.joml.Vector3d;

public record InterpolationResult(Vector3d translation, Matrix3d basis, Vector3d gradient  /* Change in position per change in spline progress */) {
    public InterpolationResult() {
        this(new Vector3d(), new Matrix3d(), new Vector3d());
    }
}
