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
        return 0;
    }
}
