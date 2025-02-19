package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.BlockPos;

public class TrackToolItem extends ToolItem {
    public TrackToolItem(ToolType type, Settings settings) {
        super(type, settings);
    }

    @Override
    public int click(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        int oldValue = marker.getValueForTool(type);
        int newValue = oldValue + (rightClick ? 1 : -1);
        if(newValue >= type.settings) {
            newValue -= type.settings;
        } else if (newValue < 0) {
            newValue += type.settings;
        }
        do {
            marker.setValueForTool(type, newValue);
            marker.markDirty();
            marker.sync();
            marker = marker.getNextMarker();
        } while(isSneaking && marker != null && marker.getValueForTool(type) == oldValue);
        return newValue;
    }

}
