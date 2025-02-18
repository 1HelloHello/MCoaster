package io.github.foundationgames.splinecart.track;

public enum TrackStyle {

    DEFAULT(0),
    ONE_CONNECTION(1),
    TWO_CONNECTIONS(2),
    BIG_SPLINE(3),
    SMALL_SPLINE(4),
    ARROW(5),
    TEST_ONE(6),
    TEST_TWO(7),
    ;

    public static final int CANVAS_SIZE = 8;
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final int textureU;

    TrackStyle(int textureU) {
        this.textureU = textureU;
    }

    public static TrackStyle read(int type) {
        if (type < 0 || type >= values().length) {
            return DEFAULT;
        }

        return values()[type];
    }

}
