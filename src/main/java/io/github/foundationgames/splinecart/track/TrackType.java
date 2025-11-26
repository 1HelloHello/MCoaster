package io.github.foundationgames.splinecart.track;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public enum TrackType {
    DEFAULT(MotionModifier.FRICTION, null, "Default"),
    CHAIN_DRIVE((m, g, p, s, f) -> {
        double chainliftSpeed = p * .1 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH;
        double frictionSpeed = MotionModifier.FRICTION.calculate(m, g, p, s, f);
        return s == 0
                ? frictionSpeed
                : chainliftSpeed >= 0
                ? Math.max(frictionSpeed, chainliftSpeed)
                : Math.min(frictionSpeed, chainliftSpeed);
    },
            (p, t, col, v) -> v[0] = t * 0.0004f * p,
            "Chain Drive"
    ),
    MAGNETIC((m, g, p, s, f) -> {
                double targetSpeed = p / 10 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH;
                return Math.abs(targetSpeed - m) < 0.01
                    ? targetSpeed
                    : m + (targetSpeed < m ? -1 : 1) * (1.0 / (20 * 20)) * s / 10;
            },
            (p, t, col, v) ->  {
                float strength = Math.min(Math.abs(p) / 1000f, 1);
                col.set(new Vector3f(
                        strength * 0.6f + (strength > 0.0f ? 0.4f : 0.3f),
                        MathHelper.clamp((strength * strength * 0.7f) - 0.5f, 0, 1),
                        MathHelper.clamp((strength * strength * 0.6f) - 0.7f, 0, 1)
                ));
            },
            "Magnetic"
    ),
    TIRE_DRIVE((m, g, p, s, f) -> p / 10 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH,
            (p, t, col, v) -> v[0] = t * ((float) p / 15 * 0.05f), // TODO
            "Tire Drive"
    ) ,
    HOLDING_BREAKS((m, g, p, s, f) -> p > 0 ? MotionModifier.FRICTION.calculate(m, g, p, s, f) : 0,
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
        MotionModifier FRICTION = (m, g, p, s, f) -> m - (m * f);

        /**
         *
         * @param motion the current speed of the cart
         * @param grade unused
         * @param power the power set to the track
         * @param strength the strength (setting) set to the track
         * @param friction the track friction loaded from the gamerule
         * @return the new speed of the cart
         */
        double calculate(double motion, double grade, double power, double strength, double friction);
    }

    @FunctionalInterface
    public interface Overlay {
        void calculateEffects(int power, float time, Vector3f outputColor, float[] outputVOffset);
    }
}
