package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

public class PowerToolItem extends ToolItem {

    public PowerToolItem(String identifier, RegistryKey<Item> registryKey) {
        super(ToolType.POWER, identifier, registryKey);
    }

    @Override
    public int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        double oldValue = marker.getPower();
        if(oldValue == Double.POSITIVE_INFINITY) {
            oldValue = 0;
        }
        double newValue = oldValue + (rightClick ? 1 : -1) * (isSneaking ? .1 : 1);
        do {
            marker.setPower(newValue);
            marker.markDirty();
            marker.sync();
            marker = marker.getNextMarker();
        } while(isSneaking && marker != null && marker.getValueForTool(type) == oldValue);
        return (int) (newValue * 10);
    }
}
