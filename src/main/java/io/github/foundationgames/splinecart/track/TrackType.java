package io.github.foundationgames.splinecart.track;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.util.SUtil;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public enum TrackType {
    DEFAULT(MotionModifier.FRICTION, null, "Default"),
    CHAIN_DRIVE((m, g, p) -> p >= 0
                    ? Math.max(MotionModifier.FRICTION.calculate(m, g, p), p * .1 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH)
                    : MotionModifier.FRICTION.calculate(m, g, p),
            (p, t, col, v) -> v[0] = t * ((float) p / 10 / 15 * 0.05f), // 0.05 original
            "Chain Drive"
    ),
    MAGNETIC((m, g, p) -> {
                double speed = p * TrackFollowerEntity.MAGNETIC_SPEED_FACTOR;
                m = (MotionModifier.FRICTION.calculate(m, g, p));
                return m + ((speed - m) * TrackFollowerEntity.MAGNETIC_ACCEL * (1.0 - g));
            },
            (p, t, col, v) ->  col.set(SUtil.REDSTONE_COLOR_LUT[p > 15 || p < 0 ? 15 : (int)p]),
            "Magnetic"
    ),
    TIRE_DRIVE((m, g, p) -> p * 1 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH,
            (p, t, col, v) -> v[0] = t * ((float) p / 15 * 0.05f), // TODO
            "Tire Drive"
    ) ,
    HOLDING_BREAKS((m, g, p) -> p > 0 ? MotionModifier.FRICTION.calculate(m, g, p) : 0,
            (p, t, col, v) -> v[0] = t * ((float) p / 15 * 0.05f), // TODO
            "Holding Breaks"
    ) ;

    public static final int CANVAS_SIZE = 4;
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final MotionModifier motion;
    public final @Nullable Overlay overlay;
    public final String name;

    TrackType(MotionModifier motion, @Nullable Overlay overlay, String name) {
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

        double calculate(double motion, double grade, double power);
    }

    @FunctionalInterface
    public interface Overlay {
        void calculateEffects(double power, float time, Vector3f outputColor, float[] outputVOffset);
    }
}
