package io.github.foundationgames.splinecart.track;

/**
 * Contains the different appearances of the track that can be used. There can only be a maximum of 8 different ones, because of the size of the canvas (.png file).
 */
public enum TrackStyle {

    DEFAULT("Default"),
    SINGLE_BEAM("Single Beam"),
    DOUBLE_BEAM("Double Beam"),
    SINGLE_BEAM_TRIANGLE("Single Beam Triangle"),
    DOUBLE_BEAM_TRIANGLE("Double Beam Triangle"),
    SPINE("Spine"),
    BOX("Box"),
    ARROW("Arrow"),
    ;

    /**
     * The size of the canvas, as in how many textures are in the .png file. This is also the maximum of different track styles that can exist.
     */
    public static final int CANVAS_SIZE = 8;

    /**
     * How much of the canvas each texture takes up (used for parsing the texture)
     */
    public static final float INVERSE_CANVAS_SIZE = (float) 1 / CANVAS_SIZE;

    /**
     * The name that is displayed when the player cycles through the track styles.
     */
    public final String name;

    TrackStyle(String name) {
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
