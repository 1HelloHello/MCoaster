package io.github.foundationgames.splinecart.block;

import com.mojang.serialization.MapCodec;
import io.github.foundationgames.splinecart.item.TrackItem;
import io.github.foundationgames.splinecart.util.Pose;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class TrackTiesBlock extends Block implements BlockEntityProvider {

    public static final int ORIENTATION_RESOLUTION = 16;
    public static final double TRACK_HIGHT = (double) 0; // 0 means half a block above the floor; -7/16 (default) means almost on the floor

    public static final MapCodec<TrackTiesBlock> CODEC = createCodec(TrackTiesBlock::new);
    public static final IntProperty HEADING = IntProperty.of("heading", 0, ORIENTATION_RESOLUTION - 1);
    public static final IntProperty PITCHING = IntProperty.of("pitching", 0, ORIENTATION_RESOLUTION - 1);
    public static final IntProperty BANKING = IntProperty.of("banking", 0, ORIENTATION_RESOLUTION - 1);

    public static final int OUTLINE_SHAPE_MARGIN = 2;
    public static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];

    public TrackTiesBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                //.with(FACING, Direction.UP)
                .with(HEADING, 0)
                .with(PITCHING, 0)
                .with(BANKING, 0)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(HEADING, PITCHING, BANKING);
    }

//    @Nullable
//    @Override
//    public BlockState getPlacementState(ItemPlacementContext ctx) {
//        return getDefaultState().with(FACING, ctx.getSide());
//    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        //return SHAPES[state.get(FACING).ordinal()];
        return createCuboidShape(OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN, OUTLINE_SHAPE_MARGIN,
                16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN,16 - OUTLINE_SHAPE_MARGIN); //TODO
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof TrackTiesBlockEntity tie) {
            if (!newState.isOf(state.getBlock())) {
                if (!world.isClient()) tie.onDestroy();
            } else {
                tie.updatePose(pos, newState);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);

        if (world.getBlockEntity(pos) instanceof TrackTiesBlockEntity tie) {
            tie.updatePower();
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // TODO add new items to change blockstate

//        if (player.canModifyBlocks() &&
//                !(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof TrackItem) &&
//                world.getBlockEntity(pos) instanceof TrackTiesBlockEntity tie) {
//            if (tie.prev() == null && tie.next() == null) {
//                if (world.isClient()) {
//                    return ActionResult.SUCCESS;
//                } else {
//                    var newState = state.with(HEADING, (state.get(HEADING) + 1) % ORIENTATION_RESOLUTION);
//                    world.setBlockState(pos, newState);
//                    tie.updatePose(pos, newState);
//                    tie.markDirty();
//                    tie.sync();
//
//                    return ActionResult.CONSUME;
//                }
//            }
//        }

        return super.onUse(state, world, pos, player, hit);
    }

    public Pose getPose(BlockState state, BlockPos blockPos) {
        if (!state.contains(HEADING) || !state.contains(PITCHING)) {
            return null;
        }
        //final Direction facing = state.get(FACING);
        final int heading = state.get(HEADING);
        final int pitching = state.get(PITCHING);
        final int banking = state.get(BANKING);

        //final Vec3i facingVec = facing.getVector();
//        Vector3d pos = new Vector3d(facingVec.getX(), facingVec.getY(), facingVec.getZ())
//                .mul(TRACK_HIGHT) // scalar: -7/16 TODO
//                .add(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        Vector3d pos = new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        Matrix3d basis = new Matrix3d();

        basis.rotate(new AxisAngle4d(heading * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION), 0, 1, 0));
        //basis.rotate(facing.getRotationQuaternion());
        basis.rotateX(pitching * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateZ(banking* MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));

        return new Pose(pos, basis);
    }

    @Override
    protected MapCodec<TrackTiesBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrackTiesBlockEntity(pos, state);
    }

    static {
        for (var dir : Direction.values()) {
            int idx = dir.ordinal();
            var min = new Vector3d(-8, -8, -8);
            var max = new Vector3d(8, -6, 8);

            var rot = dir.getRotationQuaternion();
            rot.transform(min);
            rot.transform(max);

            min.add(8, 8, 8);
            max.add(8, 8, 8);

            SHAPES[idx] = createCuboidShape(
                    Math.min(min.x(), max.x()),
                    Math.min(min.y(), max.y()),
                    Math.min(min.z(), max.z()),
                    Math.max(min.x(), max.x()),
                    Math.max(min.y(), max.y()),
                    Math.max(min.z(), max.z()));
        }
    }
}
