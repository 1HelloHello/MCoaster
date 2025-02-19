package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.BlockPos;

public class TrackToolItem extends ToolItem {
    public TrackToolItem(ToolType type, Settings settings) {
        super(type, settings);
    }

    @Override
    public int click(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        int value = marker.getValueForTool(type);
        value += rightClick ? 1 : -1;
        if(value >= type.settings) {
            value -= type.settings;
        } else if (value < 0) {
            value += type.settings;
        }
        marker.setValueForTool(type, value);
        return value;
    }

}
