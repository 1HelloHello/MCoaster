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
        int oldValue = marker.getPower();
        int newValue;
        if(oldValue == Integer.MAX_VALUE) {
            newValue = rightClick ? 0 : (isSneaking ? -1 : -10);
        }else {
            newValue = oldValue + (rightClick ? 1 : -1) * (isSneaking ? 1 : 10);
            if((oldValue < 0 && newValue >= 0) || (oldValue >= 0 && newValue < 0)) {
                newValue = Integer.MAX_VALUE;
            }
        }
        marker.setPower(newValue);
        marker.markDirty();
        marker.sync();
        return newValue;
    }
}
