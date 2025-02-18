package io.github.foundationgames.splinecart.track;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.util.SUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public enum TrackType {
    DEFAULT(0, MotionModifier.FRICTION, null),
    CHAIN_DRIVE(1,
            (m, g, p) -> Math.max(m * TrackFollowerEntity.FRICTION, TrackFollowerEntity.CHAIN_DRIVE_SPEED * ((double) p /15)),
            (p, t, col, v) -> v[0] = t * ((float) p / 15 * 0.05f) // 0.05 original
    ),
    MAGNETIC(2,
            (m, g, p) -> {
                double speed = (p / 15.0) * TrackFollowerEntity.MAGNETIC_SPEED_FACTOR;
                m = (m * TrackFollowerEntity.FRICTION);
                return m + ((speed - m) * TrackFollowerEntity.MAGNETIC_ACCEL * (1.0 - g));
            },
            (p, t, col, v) -> col.set(SUtil.REDSTONE_COLOR_LUT[p])
    );

    public static final int CANVAS_SIZE = 4;
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final int textureU;
    public final MotionModifier motion;
    public final @Nullable Overlay overlay;

    TrackType(int textureU, MotionModifier motion, @Nullable Overlay overlay) {
        this.textureU = textureU;
        this.motion = motion;
        this.overlay = overlay;
    }

    public static TrackType read(int type) {
        if (type < 0 || type >= values().length) {
            return DEFAULT;
        }

        return values()[type];
    }

    @FunctionalInterface
    public interface MotionModifier {
        MotionModifier FRICTION = (m, g, p) -> m * TrackFollowerEntity.FRICTION;

        double calculate(double motion, double grade, int redstonePower);
    }

    @FunctionalInterface
    public interface Overlay {
        void calculateEffects(int redstonePower, float time, Vector3f outputColor, float[] outputVOffset);
    }
}
