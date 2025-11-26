package io.github.foundationgames.splinecart.track;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum TrackType {
    DEFAULT(MotionModifier.FRICTION, false, false, null, null, "Default"),
    CHAIN_DRIVE((m, p, s, f) -> {
        double chainLiftSpeed = p * .1 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH;
        double frictionSpeed = MotionModifier.FRICTION.calculate(m, p, s, f);
        return s == 0 ? frictionSpeed : chainLiftSpeed >= 0 ? Math.max(frictionSpeed, chainLiftSpeed) : Math.min(frictionSpeed, chainLiftSpeed);
    }, true, true, (p, t) -> t * 0.0004f * p, p -> TrackColor.WHITE.color, "Chain Drive"),
    MAGNETIC((m, p, s, f) -> {
        double targetSpeed = p / 10 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH;
        return Math.abs(targetSpeed - m) < 0.01 ? targetSpeed : m + (targetSpeed < m ? -1 : 1) * (1.0 / (20 * 20)) * s / 10;
    }, true, false, (p, t) -> 0f, p -> {
        float strength = Math.min(Math.abs(p) / 1000f, 1);
        return new Color(strength * 0.6f + (strength > 0.0f ? 0.4f : 0.3f), MathHelper.clamp((strength * strength * 0.7f) - 0.5f, 0, 1), MathHelper.clamp((strength * strength * 0.6f) - 0.7f, 0, 1));
    }, "Magnetic"),
    TIRE_DRIVE((m, p, s, f) -> p / 10 / TrackFollowerEntity.METERS_PER_TICK_TO_KMH, false, false, (p, t) -> t * ((float) p / 15 * 0.05f), // TODO
            p -> TrackColor.WHITE.color, "Tire Drive"),
    HOLDING_BREAKS((m, p, s, f) -> p > 0 ? MotionModifier.FRICTION.calculate(m, p, s, f) : 0, false, false, (p, t) -> t * ((float) p / 15 * 0.05f), // TODO
            p -> TrackColor.WHITE.color, "Holding Breaks");

    private static final int CANVAS_SIZE = 4;
    private static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final MotionModifier motion;
    public final boolean hasDynamic;
    public final boolean hasStatic;
    public final BiFunction<Integer, Float, Float> progress;
    public final Function<Integer, Color> color;
    public final String name;

    TrackType(MotionModifier motion, boolean hasDynamic, boolean hasStatic, BiFunction<Integer, Float, Float> progress, Function<Integer, Color> color, String name) {
        this.motion = motion;
        this.hasDynamic = hasDynamic;
        this.hasStatic = hasStatic;
        this.progress = progress;
        this.color = color;
        this.name = name;
    }

    public static TrackType read(int type) {
        if (type < 0 || type >= values().length) {
            return DEFAULT;
        }

        return values()[type];
    }

    public float getTextureStart() {
        return ordinal() * TrackType.INVERSE_CANVAS_SIZE;
    }
    public float getTextureEnd() {
        return (ordinal() +1) * TrackType.INVERSE_CANVAS_SIZE;
    }

    @FunctionalInterface
    public interface MotionModifier {
        MotionModifier FRICTION = (m, p, s, f) -> m - (m * f);

        /**
         * @param motion   the current speed of the cart
         * @param power    the power set to the track
         * @param strength the strength (setting) set to the track
         * @param friction the track friction loaded from the gamerule
         * @return the new speed of the cart
         */
        double calculate(double motion, double power, double strength, double friction);
    }
}
