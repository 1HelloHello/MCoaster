package io.github.foundationgames.splinecart.mixin;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackColorPreset;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class DyeItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"))
    protected void useOnBlock(ItemUsageContext context, CallbackInfoReturnable info) {
        if(!(context.getStack().getItem() instanceof DyeItem dyeItem)) {
            return;
        }
        World world = context.getPlayer().getWorld();
        if(!(world.getBlockEntity(context.getBlockPos()) instanceof TrackMarkerBlockEntity marker)) {
            return;
        }
        TrackColor trackColor = TrackColorPreset.valueOf(dyeItem.getColor()).get();
        onColorTrack(marker, trackColor, context.getPlayer().isSneaking());
    }

    private void onColorTrack(TrackMarkerBlockEntity trackMarker, TrackColor newColor, boolean shiftClicked) {
        TrackColor oldColor = trackMarker.getNextColor();
        do {
            trackMarker.setNextColor(newColor);
            trackMarker.markDirty();
            trackMarker = trackMarker.getNextMarker();
        }while (shiftClicked && trackMarker != null && oldColor.equals(trackMarker.getNextColor()));
    }

}
