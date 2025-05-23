package io.github.foundationgames.splinecart.mixin.client;

import io.github.foundationgames.splinecart.SplinecartClient;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.item.tools.ToolItem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo info) {
        // return if the player is sitting in a coaster cart
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        Entity vehicle = player.getVehicle();
        while (vehicle != null) {
            if (vehicle instanceof TrackFollowerEntity trackFollower) {
                // send velocity display msg
                if(SplinecartClient.CFG_SHOW_SPEED_INFO.get()) {
                    player.sendMessage(trackFollower.getSpeedInfo(
                            SplinecartClient.CFG_SHOW_SPEED_INFO_PEAK.get(),
                            SplinecartClient.CFG_SHOW_SPEED_INFO_FORCE.get(),
                            SplinecartClient.CFG_SHOW_IMPERIAL.get()),
                            true);
                }else {
                    clear(player);
                }
                return;
            }
            vehicle = vehicle.getVehicle();
        }
        // return if the player doesn't have a tool item in their hand
        if(!(player.getInventory().getMainHandStack().getItem() instanceof ToolItem item)) {
            clear(player);
            return;
        }
        // return if the player isn't looking at a block
        World world = player.getWorld();
        HitResult hit = player.raycast(5, 0, false);
        if(hit.getType() != HitResult.Type.BLOCK) {
            clear(player);
            return;
        }
        // return if the player isn't looking at a track marker
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
