package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

/**
 * Handles the functionality for the two track tools (track type and track style)
 */
public class TrackToolItem extends ToolItem {

    public TrackToolItem(ToolType type, String identifier, RegistryKey<Item> registryKey) {
        super(type, identifier, registryKey);
    }

    @Override
    public int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
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
