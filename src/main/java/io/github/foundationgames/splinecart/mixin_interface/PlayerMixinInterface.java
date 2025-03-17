package io.github.foundationgames.splinecart.mixin_interface;

import net.minecraft.util.math.BlockPos;

public interface PlayerMixinInterface {

    BlockPos getTrackSelectedMarker();
    void setTrackSelectedMarker(BlockPos blockPos);

}
