package io.github.foundationgames.splinecart.mixin.client;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.item.tools.ToolItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(!(player.getInventory().getMainHandStack().getItem() instanceof ToolItem item)) {
            clear(player);
            return;
        }
        World world = player.getWorld();
        HitResult hit = player.raycast(5, 0, false);
        if(hit.getType() != HitResult.Type.BLOCK) {
            clear(player);
            return;
        }
        BlockHitResult blockHitResult = (BlockHitResult) hit;
        if(!(world.getBlockEntity(blockHitResult.getBlockPos()) instanceof TrackMarkerBlockEntity trackMarker)) {
            clear(player);
            return;
        }
        item.sendCurrentStateMessage(player, trackMarker);
    }

    /**
     * Clears the display message of the player
     * @param player
     */
    private void clear(PlayerEntity player) {
        player.sendMessage(Text.of(""), true);
    }

}
