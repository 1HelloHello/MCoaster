package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;

public class PowerToolItem extends ToolItem {

    private static final int LOW_INCREMENT = 5;
    private static final int HIGH_INCREMENT = 100;

    public PowerToolItem(ToolType type, String identifier, RegistryKey<Item> registryKey) {
        super(type, identifier, registryKey);
    }

    @Override
    public int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        int oldValue = marker.getValueForTool(type);
        int newValue;
        if(oldValue == Integer.MAX_VALUE) {
            newValue = rightClick ? 0 : (isSneaking ? -LOW_INCREMENT : -HIGH_INCREMENT);
        }else {
            newValue = oldValue + (rightClick ? 1 : -1) * (isSneaking ? LOW_INCREMENT : HIGH_INCREMENT);
            if((oldValue < 0 && newValue >= 0) || (oldValue >= 0 && newValue < 0)) {
                newValue = Integer.MAX_VALUE;
            }
        }
        marker.setValueForTool(type, newValue);
        marker.markDirty();
        marker.sync();
        return newValue;
    }
}
