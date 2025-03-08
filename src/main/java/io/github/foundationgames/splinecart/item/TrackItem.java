package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.component.OriginComponent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class TrackItem extends ActionItem {

    public final TrackType track;

    public TrackItem(TrackType track, Settings settings) {
        super(settings);

        this.track = track;
    }

    @Override
    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!rightClick) {
            stackInHand.set(Splinecart.ORIGIN_POS, new OriginComponent(pos));
            return true;
        }
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity)) {
            return false;
        }
        if (world.isClient()) {
            return true;
        }
        var origin = stackInHand.get(Splinecart.ORIGIN_POS);
        if(origin == null) {
            return false;
        }
        var oPos = origin.pos();
        if (!pos.equals(oPos) && world.getBlockEntity(oPos) instanceof TrackMarkerBlockEntity oTies) {
            oTies.setNext(pos, this.track);
            world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
        }
        stackInHand.set(Splinecart.ORIGIN_POS, new OriginComponent(pos));
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var origin = stack.get(Splinecart.ORIGIN_POS);
        if (origin != null) {
            origin.appendTooltip(context, tooltip::add, type);
        }
    }

}
