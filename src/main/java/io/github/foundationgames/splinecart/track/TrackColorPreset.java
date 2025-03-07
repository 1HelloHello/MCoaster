package io.github.foundationgames.splinecart.track;

import net.minecraft.util.DyeColor;

/**
 * Contains a specific RGB color for every minecraft dye.
 */
public enum TrackColorPreset {

    WHITE(0xffffff, DyeColor.WHITE),
    LIGHT_GRAY(0xc6c6c6, DyeColor.LIGHT_GRAY),
    GREY(0x636362, DyeColor.GRAY),
    BLACK(0, DyeColor.BLACK),
    BROWN(0x7f5b00, DyeColor.BROWN),
    RED(0xff0000, DyeColor.RED),
    ORANGE(0xfca905, DyeColor.ORANGE),
    YELLOW(0xffff00, DyeColor.YELLOW),
    LIME(0x00ff00, DyeColor.LIME),
    GREEN(0x04ce04, DyeColor.GREEN),
    CYAN(0x02fcad, DyeColor.CYAN),
    LIGHT_BLUE(0x00f7e2, DyeColor.LIGHT_BLUE),
    BLUE(0x0000ff, DyeColor.BLUE),
    PURPLE(0xd202fc, DyeColor.PURPLE),
    MAGENTA(0xef02fc, DyeColor.MAGENTA),
    PINK(0xfc02eb, DyeColor.PINK),
    ;

    public final int hex;
    public final DyeColor item;

    TrackColorPreset(int hex, DyeColor item) {
        this.hex = hex;
        this.item = item;
    }

    public TrackColor get() {
        return new TrackColor(hex);
    }

}
