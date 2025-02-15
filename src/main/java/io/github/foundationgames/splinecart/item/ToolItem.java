package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackTiesBlock;
import io.github.foundationgames.splinecart.block.TrackTiesBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;

public class ToolItem extends Item {

    public static final Map<ToolType, Item> ITEMS_BY_TYPE = new HashMap<>();

    public final ToolType type;

    public ToolItem(ToolType type, Settings settings) {
        super(settings);

        this.type = type;
        ITEMS_BY_TYPE.put(type, this);
    }

    /**
     * Gets executed, when the player left clicks with the item.
     * @param state
     * @param world
     * @param pos
     * @param miner
     * @return
     */
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            this.use(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    /**
     * Gets executed, when the player right clicks with the item.
     * @param context the usage context
     * @return
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && playerEntity != null) {
            BlockPos blockPos = context.getBlockPos();
            if (!this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, context.getStack())) {
                return ActionResult.FAIL;
            }
        }

        return ActionResult.SUCCESS;
    }

    private boolean use(PlayerEntity player, BlockState blockState, World world, BlockPos pos, boolean rightClick, ItemStack stack) {
        if(!(world.getBlockEntity(pos) instanceof TrackTiesBlockEntity)) {
            sendErrorNoMarker(player);
            return false;
        }
        TrackTiesBlockEntity tie = (TrackTiesBlockEntity) world.getBlockEntity(pos);
        sendMessage(player, Text.of(type.currentStateMsg.get(blockState.get(type.property))));
        int clickResolution = player.isSneaking() ? 1 : TrackTiesBlock.ORIENTATION_RESOLUTION / 8;
        BlockState newState = blockState.with((IntProperty)type.property,
                (blockState.get((IntProperty)type.property) + (rightClick ? -clickResolution : clickResolution) + TrackTiesBlock.ORIENTATION_RESOLUTION) % TrackTiesBlock.ORIENTATION_RESOLUTION);
        world.setBlockState(pos, newState);
        tie.updatePose(pos, newState);
        tie.markDirty();
        tie.sync();
        return true;
    }

    private void sendErrorNoMarker(PlayerEntity player) {
        MutableText text = Text.translatable("item.splinecart.tools.error_no_marker");
        text.withColor(Colors.WHITE);
        sendMessage(player, text);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, true);
    }

}
