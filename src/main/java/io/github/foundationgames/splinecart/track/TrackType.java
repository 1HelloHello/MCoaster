package io.github.foundationgames.splinecart.track;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.util.SUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public enum TrackType {
    DEFAULT(0, MotionModifier.FRICTION, null, "Default"),
    CHAIN_DRIVE(1,
            (m, g, p) -> Math.max(MotionModifier.FRICTION.calculate(m, g, p), TrackFollowerEntity.CHAIN_DRIVE_SPEED * ((double) p /15)),
            (p, t, col, v) -> v[0] = t * ((float) p / 15 * 0.05f), // 0.05 original
            "Chain Drive"
    ),
    MAGNETIC(2,
            (m, g, p) -> {
                double speed = (p / 15.0) * TrackFollowerEntity.MAGNETIC_SPEED_FACTOR;
                m = (MotionModifier.FRICTION.calculate(m, g, p));
                return m + ((speed - m) * TrackFollowerEntity.MAGNETIC_ACCEL * (1.0 - g));
            },
            (p, t, col, v) -> col.set(SUtil.REDSTONE_COLOR_LUT[p]),
            "Magnetic"
    );

    public static final int CANVAS_SIZE = 4;
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final int textureU;
    public final MotionModifier motion;
    public final @Nullable Overlay overlay;
    public final String name;

    TrackType(int textureU, MotionModifier motion, @Nullable Overlay overlay, String name) {
        this.textureU = textureU;
        this.motion = motion;
        this.overlay = overlay;
        this.name = name;
    }

    public static TrackType read(int type) {
        if (type < 0 || type >= values().length) {
            return DEFAULT;
        }

        return values()[type];
    }

    @FunctionalInterface
    public interface MotionModifier {
        MotionModifier FRICTION = (m, g, p) -> m - (m * TrackFollowerEntity.FRICTION);

        double calculate(double motion, double grade, int redstonePower);
    }

    @FunctionalInterface
    public interface Overlay {
        void calculateEffects(int redstonePower, float time, Vector3f outputColor, float[] outputVOffset);
    }
}
