package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Colors;
import net.minecraft.world.World;

/**
 * Implements the functionality for all the orientation tools (heading, banking, etc.).
 */
public class OrientationToolItem extends ToolItem {

    public final Type type;

    public OrientationToolItem(Type type, RegistryKey<Item> registryKey) {
        super(registryKey);
        this.type = type;
    }

    @Override
    public boolean use(TrackMarkerBlockEntity marker, PlayerEntity player, World world, boolean rightClick) {
        int clickResolution = player.isSneaking() ? 5 : TrackMarkerBlockEntity.ORIENTATION_RESOLUTION / 8;

        int value = getValueForTool(marker);
        int newVal = (value  + (((rightClick ? clickResolution : -clickResolution) + TrackMarkerBlockEntity.ORIENTATION_RESOLUTION))) % TrackMarkerBlockEntity.ORIENTATION_RESOLUTION;
        setValueForTool(marker, newVal);
        marker.sync();
        marker.markDirty();
        return true;
    }

    private int getValueForTool(TrackMarkerBlockEntity marker) {
        return switch (type) {
            case HEADING -> marker.getHeading();
            case PITCHING -> marker.getPitching();
            case BANKING -> marker.getBanking();
            case RELATIVE_ORIENTATION -> marker.getRelativeOrientation();
        };
    }

    private void setValueForTool(TrackMarkerBlockEntity marker, int value) {
        switch(type) {
            case HEADING -> marker.setHeading(value);
            case PITCHING -> marker.setPitching(value);
            case BANKING -> marker.setBanking(value);
            case RELATIVE_ORIENTATION -> marker.setRelativeOrientation(value);
        }
    }

    @Override
    protected String writeCurrentState(TrackMarkerBlockEntity marker) {
        int value = getValueForTool(marker);
        return type == Type.HEADING
                ? valToQuarterDirection(value) + " (" + value + ")"
                : valToOrientation(value);
    }

    @Override
    protected int getTextColor() {
        return type.color;
    }

    private static String valToOrientation(int val) {
        if(val == 0) {
            return "0";
        }
        if(val <= TrackMarkerBlockEntity.ORIENTATION_RESOLUTION / 2) {
            return val + "°";
        }
        return "-" + (TrackMarkerBlockEntity.ORIENTATION_RESOLUTION - val) + "°";
    }

    private static String valToQuarterDirection(int val) {
        for(OrientationToolItem.QuarterDirection direction : OrientationToolItem.QuarterDirection.values()) {
            if(val == direction.value) {
                return direction.name;
            }
            if(val > direction.value && val < direction.value + STEP) {
                int relativeDirection = val - direction.value;
                if(relativeDirection == STEP / 2)
                    return direction.compiledName;
                return direction.compiledName + " " + relativeDirection + "°";
            }
        }
        return "ERROR";
    }

    public static final int STEP = 90;

    public enum QuarterDirection {
        SOUTH(0, "S", "SE"),
        EAST(STEP, "E", "NE"),
        NORTH(2 * STEP, "N", "NW"),
        WEST(3 * STEP, "W", "SW");

        public final int value;
        public final String name;
        public final String compiledName;

        QuarterDirection(int value, String name, String compiledName) {
            this.value = value;
            this.name = name;
            this.compiledName = compiledName;
        }
    }

    public enum Type {
        HEADING(Colors.GREEN),
        PITCHING(Colors.CYAN),
        BANKING(Colors.YELLOW),
        RELATIVE_ORIENTATION(Colors.RED),
        ;
        final int color;
        Type(int color) {
            this.color = color;
        }

    }

}
