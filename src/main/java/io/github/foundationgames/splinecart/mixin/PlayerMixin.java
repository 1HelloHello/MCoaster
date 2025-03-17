package io.github.foundationgames.splinecart.mixin;

import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin implements PlayerMixinInterface {

    private BlockPos trackSelectedMarker = null;
    private BlockPos lastTrackSelectedMarker = null;

    @Override
    public BlockPos getTrackSelectedMarker() {
        return trackSelectedMarker;
    }

    @Override
    public void setTrackSelectedMarker(BlockPos trackSelectedMarker) {
        this.trackSelectedMarker = trackSelectedMarker;
    }

    @Override
    public BlockPos getLastTrackSelectedMarker() {
        return lastTrackSelectedMarker;
    }

    @Override
    public void setLastTrackSelectedMarker(BlockPos lastTrackSelectedMarker) {
        this.lastTrackSelectedMarker = lastTrackSelectedMarker;
    }

}
