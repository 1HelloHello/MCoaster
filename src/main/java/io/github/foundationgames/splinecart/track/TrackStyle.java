package io.github.foundationgames.splinecart.track;

public enum TrackStyle {

    DEFAULT(0, "Default"),
    ONE_CONNECTION(1, "One connection"),
    TWO_CONNECTIONS(2, "Two connections"),
    BIG_SPLINE(3, "Big Spline"),
    SMALL_SPLINE(4, "Small Spline"),
    ARROW(5, "Arrow"),
    TEST_ONE(6, "Test1"),
    TEST_TWO(7, "Test2"),
    ;

    public static final int CANVAS_SIZE = 8;
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final int textureU;
    public final String name;

    TrackStyle(int textureU, String name) {
        this.textureU = textureU;
        this.name = name;
    }

    public static TrackStyle read(int type) {
        if (type < 0 || type >= values().length) {
            return DEFAULT;
        }

        return values()[type];
    }

}
