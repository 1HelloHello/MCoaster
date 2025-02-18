package io.github.foundationgames.splinecart.track;

import org.joml.Vector3f;

public enum TrackColor {

    WHITE(0, 0,0),
    BLACK(1, 1, 1),
    RED(1, 0, 0),
    GREEN(0, 1, 0),
    BLUE(0, 0, 1);

    public final float r;
    public final float g;
    public final float b;

    TrackColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }


    public static TrackColor read(int type) {
        if (type < 0 || type >= values().length) {
            return BLACK;
        }

        return values()[type];
    }

    public Vector3f getVec() {
        return new Vector3f(r, g, b);
    }

}
