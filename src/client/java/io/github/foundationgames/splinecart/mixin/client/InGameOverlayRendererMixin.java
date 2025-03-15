package io.github.foundationgames.splinecart.mixin.client;

import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "getInWallBlockState(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;",
            at = @At("HEAD"), cancellable = true)
    private static void splinecart$modifySuffocatingBlock(PlayerEntity player, CallbackInfoReturnable<BlockState> info) {
        var vehicle = player.getVehicle();
        while (vehicle != null) {
            if (vehicle instanceof TrackFollowerEntity) {
                info.setReturnValue(null);
                return;
            }

            vehicle = vehicle.getVehicle();
        }
    }
}
