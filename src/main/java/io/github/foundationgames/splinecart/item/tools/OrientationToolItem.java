package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

/**
 * Implements the functionality for all the orientation tools (heading, banking, etc.).
 */
public class OrientationToolItem extends ToolItem {

    public OrientationToolItem(ToolType type, String identifier, RegistryKey<Item> registryKey) {
        super(type, identifier, registryKey);
    }

    @Override
    public int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        int clickResolution = isSneaking ? 5 : TrackMarkerBlockEntity.ORIENTATION_RESOLUTION / 8;

        int value = marker.getValueForTool(type);
        int newVal = (value  + (((rightClick ? -clickResolution : clickResolution) + TrackMarkerBlockEntity.ORIENTATION_RESOLUTION))) % TrackMarkerBlockEntity.ORIENTATION_RESOLUTION;
        marker.setValueForTool(type, newVal);
        marker.updatePose(pos);
        marker.sync();
        marker.markDirty();
        return newVal;
    }

}
