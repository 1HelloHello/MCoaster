package io.github.foundationgames.splinecart.event;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackColorPreset;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class DyeItemUseEvent implements UseBlockCallback {


    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!(player.getStackInHand(hand).getItem() instanceof DyeItem dyeItem)) {
            return ActionResult.PASS;
        }
        if(!(world.getBlockEntity(hitResult.getBlockPos()) instanceof TrackMarkerBlockEntity marker)) {
            return ActionResult.PASS;
        }
        TrackColor trackColor = TrackColorPreset.valueOf(dyeItem.getColor()).get();
        onColorTrack(marker, trackColor, player.isSneaking());
        return ActionResult.SUCCESS;
    }

    private void onColorTrack(TrackMarkerBlockEntity trackMarker, TrackColor newColor, boolean shiftClicked) {
        TrackColor oldColor = trackMarker.getNextColor();
        TrackMarkerBlockEntity thisMarker = trackMarker;
        do {
            trackMarker.setNextColor(newColor);
            trackMarker.markDirty();
            trackMarker = trackMarker.getNextMarker();
        }while (shiftClicked && trackMarker != null && trackMarker != thisMarker && oldColor.equals(trackMarker.getNextColor()));
    }
}
