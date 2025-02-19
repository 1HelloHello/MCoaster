package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.BlockPos;

public class OrientationToolItem extends ToolItem {

    public OrientationToolItem(ToolType type, Settings settings) {
        super(type, settings);
    }

    @Override
    public int click(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        int clickResolution = isSneaking ? 5 : TrackMarkerBlockEntity.ORIENTATION_RESOLUTION / 8;

        int value = marker.getValueForTool(type);
        int newVal = (value  + (((rightClick ? -clickResolution : clickResolution) + TrackMarkerBlockEntity.ORIENTATION_RESOLUTION))) % TrackMarkerBlockEntity.ORIENTATION_RESOLUTION;
        marker.setValueForTool(type, newVal);
        marker.updatePose(pos);
        return newVal;
    }

}
