package io.github.foundationgames.splinecart.track;

public enum TrackStyle {

    DEFAULT(0, "Default"),
    ONE_CONNECTION(1, "Single Beam"),
    TWO_CONNECTIONS(2, "Double Beam"),
    BIG_SPLINE(3, "Single Beam Triangle"),
    SMALL_SPLINE(4, "Double Beam Triangle"),
    ARROW(5, "Spine"),
    TEST_ONE(6, "Box"),
    TEST_TWO(7, "Arrow"),
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
