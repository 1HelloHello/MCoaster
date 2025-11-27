package io.github.foundationgames.splinecart.util;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public record InterpolationResult(Vector3f translation, Matrix3f basis, Vector3f gradient  /* Change in position per change in spline progress */) {
    public InterpolationResult() {
        this(new Vector3f(), new Matrix3f(), new Vector3f());
    }
}
