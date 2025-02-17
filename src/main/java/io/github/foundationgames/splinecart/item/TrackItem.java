package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.TrackType;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackItem extends Item {
    public static final Map<TrackType, Item> ITEMS_BY_TYPE = new HashMap<>();

    public final TrackType track;

    public TrackItem(TrackType track, Settings settings) {
        super(settings);

        this.track = track;
        ITEMS_BY_TYPE.put(track, this);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            leftClick(miner.getStackInHand(Hand.MAIN_HAND), pos);
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) { // TODO make better track placement handling
        if (context.getPlayer() != null && !context.getPlayer().canModifyBlocks()) {
            return ActionResult.PASS;
        }
        return rightClick(context.getWorld(), context.getBlockPos(), context.getStack());
    }

    private void leftClick(ItemStack stack, BlockPos pos) {
        stack.set(Splinecart.ORIGIN_POS, new OriginComponent(pos));
    }

    private ActionResult rightClick(World world, BlockPos pos, ItemStack stack) {
        if (world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker) { // you aim at a marker block
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            var origin = stack.get(Splinecart.ORIGIN_POS);
            if (origin != null) {
                var oPos = origin.pos();
                if (!pos.equals(oPos) && world.getBlockEntity(oPos) instanceof TrackMarkerBlockEntity oTies && oTies.getNextMarker() == null && marker.getPrevMarker() == null) {
                    oTies.setNext(pos, this.track);
                    world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
                }
                stack.set(Splinecart.ORIGIN_POS, new OriginComponent(pos));
            }
        }

        return ActionResult.PASS;
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
