package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.util.math.BlockPos;

public class PowerToolItem extends ToolItem {

    public PowerToolItem(Settings settings) {
        super(ToolType.POWER, settings);
    }

    @Override
    public int click(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking) {
        return 0;
    }
}
