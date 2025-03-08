package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.BlockPos;

public class PowerToolItem extends ToolItem {

    public PowerToolItem(Settings settings) {
        super(ToolType.POWER, settings);
    }

    @Override
    public int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        return 0;
    }
}
