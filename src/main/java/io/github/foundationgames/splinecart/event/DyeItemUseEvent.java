package io.github.foundationgames.splinecart.event;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackColor;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.awt.*;

public class DyeItemUseEvent implements UseBlockCallback {


    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!(player.getStackInHand(hand).getItem() instanceof DyeItem dyeItem)) {
            return ActionResult.PASS;
        }
        if(!(world.getBlockEntity(hitResult.getBlockPos()) instanceof TrackMarkerBlockEntity marker)) {
            return ActionResult.PASS;
        }
        Color trackColor = TrackColor.valueOf(dyeItem.getColor()).color;
        onColorTrack(marker, trackColor, player.isSneaking());
        return ActionResult.SUCCESS;
    }

    private void onColorTrack(TrackMarkerBlockEntity trackMarker, Color newColor, boolean shiftClicked) {
        Color oldColor = trackMarker.nextColor;
        TrackMarkerBlockEntity thisMarker = trackMarker;
        do {
            trackMarker.nextColor = newColor;
            trackMarker.markDirty();
            trackMarker = trackMarker.getNextMarker();
        }while (shiftClicked && trackMarker != null && trackMarker != thisMarker && oldColor.equals(trackMarker.nextColor));
    }
}
