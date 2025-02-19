package io.github.foundationgames.splinecart.track;

import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import org.joml.Vector3f;

public enum TrackColor {

    WHITE(0xffffff, DyeColor.WHITE),
    LIGHT_GRAY(0xc6c6c6, DyeColor.LIGHT_GRAY),
    GREY(0x636362, DyeColor.GRAY),
    BLACK(0, 0, 0, DyeColor.BLACK),
    BROWN(0x7f5b00, DyeColor.BROWN),
    RED(1, 0, 0, DyeColor.RED),
    ORANGE(0xfca905, DyeColor.ORANGE),
    YELLOW(0xffff00, DyeColor.YELLOW),
    LIME(0x00ff00, DyeColor.LIME),
    GREEN(0x04ce04, DyeColor.GREEN),
    CYAN(0x02fcad, DyeColor.CYAN),
    LIGHT_BLUE(0x00f7e2, DyeColor.LIGHT_BLUE),
    BLUE(0, 0, 1, DyeColor.BLUE),
    PURPLE(0xd202fc, DyeColor.PURPLE),
    MAGENTA(0xef02fc, DyeColor.MAGENTA),
    PINK(0xfc02eb, DyeColor.PINK),
    ;

    public final float r;
    public final float g;
    public final float b;
    public final DyeColor item;

    TrackColor(float r, float g, float b, DyeColor item) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.item = item;
    }

    TrackColor(int hex, DyeColor item) {
        this.item = item;
        r = (float) ((hex & 0xff0000) >> 16) / 0xff;
        g = (float) ((hex & 0x00ff00) >> 8) / 0xff;
        b = (float) ((hex & 0x0000ff)) / 0xff;
    }


    public static TrackColor read(int type) {
        if (type < 0 || type >= values().length) {
            return BLACK;
        }

        return values()[type];
    }

    public Vector3f getVec() {
        return new Vector3f(r, g, b);
    }

}
