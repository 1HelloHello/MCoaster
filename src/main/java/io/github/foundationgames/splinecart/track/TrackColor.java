package io.github.foundationgames.splinecart.track;

import org.joml.Vector3f;

public class TrackColor {

    private final int hex;

    public TrackColor(int hex) {
        this.hex = hex;
    }

    public int getHex() {
        return hex;
    }

    public float getR() {
        return (float) ((hex & 0xff0000) >> 16) / 0xff;
    }

    public float getG() {
        return (float) ((hex & 0x00ff00) >> 8) / 0xff;
    }

    public float getB() {
        return (float) (hex & 0x0000ff) / 0xff;
    }

    public Vector3f getVec3f() {
        return new Vector3f(getR(), getG(), getB());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  TrackColor other) {
            return hex == other.hex;
        }
        return false;
    }
}
