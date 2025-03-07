package io.github.foundationgames.splinecart.track;

import org.joml.Vector3f;

/**
 * Represents an RGB color that the track can have.
 * @param hex format: 0x00RRGGBB
 */
public record TrackColor(int hex) {

    public float getR() {
        return (float) ((hex & 0xff0000) >> 16) / 0xff;
    }

    public float getG() {
        return (float) ((hex & 0x00ff00) >> 8) / 0xff;
    }

    public float getB() {
        return (float) (hex & 0x0000ff) / 0xff;
    }

    /**
     *
     * @return a Vec3f with the r, g, and b values in its fields.
     */
    public Vector3f getVec3f() {
        return new Vector3f(getR(), getG(), getB());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TrackColor(int other)) {
            return hex == other;
        }
        return false;
    }
}
