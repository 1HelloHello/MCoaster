package io.github.foundationgames.splinecart.block;

import com.mojang.serialization.MapCodec;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackColorPreset;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class TrackMarkerBlock extends Block implements BlockEntityProvider {

    public Direction placeDirection = null;

    public static final MapCodec<TrackMarkerBlock> CODEC = createCodec(TrackMarkerBlock::new);

    public static final int OUTLINE_SHAPE_MARGIN = 2;

    public TrackMarkerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        placeDirection = ctx.getHorizontalPlayerFacing();
        return this.getDefaultState();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN,
                16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(stack.getItem() instanceof  DyeItem item) {
            for(TrackColorPreset trackColor : TrackColorPreset.values()) {
                if(item.getColor() == trackColor.item) {
                    TrackMarkerBlockEntity blockEntity = (TrackMarkerBlockEntity) world.getBlockEntity(pos);
                    assert blockEntity != null;
                    onColorTrack(blockEntity, trackColor.get(),
                            hit.isInsideBlock());
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private void onColorTrack(TrackMarkerBlockEntity trackMarker, TrackColor newColor, boolean shiftClicked) {
        TrackColor oldColor = trackMarker.getNextColor();
         do {
             setNextColorWithUpdate(trackMarker, newColor);
            trackMarker = trackMarker.getNextMarker();
        }while (shiftClicked && trackMarker != null && oldColor.equals(trackMarker.getNextColor()));
    }

    private void setNextColorWithUpdate(TrackMarkerBlockEntity blockEntity, TrackColor trackColor) {
        blockEntity.setNextColor(trackColor);
        blockEntity.markDirty();
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity tie) {
            if (!newState.isOf(state.getBlock())) {
                if (!world.isClient()) tie.onDestroy();
            } else {
                tie.updatePose(pos);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected MapCodec<TrackMarkerBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrackMarkerBlockEntity(pos, state);
    }

}
