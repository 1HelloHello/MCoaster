package io.github.foundationgames.splinecart.track;

import net.minecraft.item.DyeItem;
import net.minecraft.util.DyeColor;
import org.joml.Vector3f;

public enum TrackColor {

    WHITE(1, 1,1, DyeColor.WHITE),
    BLACK(0, 0, 0, DyeColor.BLACK),
    RED(1, 0, 0, DyeColor.RED),
    GREEN(0, 1, 0, DyeColor.GREEN),
    BLUE(0, 0, 1, DyeColor.BLUE);

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
