package io.github.foundationgames.splinecart.track;

/**
 * Contains the different appearances of the track that can be used. There can only be a maximum of 8 different ones, because of the size of the canvas (.png file).
 */
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

    /**
     * The size of the canvas, as in how many textures are in the .png file. This is also the maximum of different track styles that can exist.
     */
    public static final int CANVAS_SIZE = 8;

    /**
     * How much of the canvas each texture takes up (used for parsing the texture)
     */
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    public final int id;

    /**
     * The name that is displayed when the player cycles through the track styles.
     */
    public final String name;

    TrackStyle(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @param id
     * @return The TrackStyle with that id, or default if none exist.
     */
    public static TrackStyle read(int id) {
        if (id < 0 || id >= values().length) {
            return DEFAULT;
        }

        return values()[id];
    }

}
