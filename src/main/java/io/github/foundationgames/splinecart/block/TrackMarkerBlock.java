package io.github.foundationgames.splinecart.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class TrackMarkerBlock extends Block implements BlockEntityProvider {

    public ItemPlacementContext itemPlacementContext = null;

    public static final MapCodec<TrackMarkerBlock> CODEC = createCodec(TrackMarkerBlock::new);

    public static final int OUTLINE_SHAPE_MARGIN = 2;

    private boolean redstonePowered = false;

    public TrackMarkerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        this.itemPlacementContext = itemPlacementContext;
        return this.getDefaultState();
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN,
                16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity tie) {
            if (!newState.isOf(state.getBlock())) {
                if (!world.isClient()) tie.onDestroy();
            } else {
                tie.updatePose();
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean receivedPower = world.getReceivedRedstonePower(pos) != 0;
        if(!redstonePowered && receivedPower) {
            if (world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker) {
                marker.triggers.execute(world);
            }
        }
        redstonePowered = receivedPower;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker) {
            int markerPower = marker.getPower();
            return Math.abs(markerPower == Integer.MAX_VALUE ? 0 : Math.abs(markerPower / 10));
        }
        return 0;
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
