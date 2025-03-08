package io.github.foundationgames.splinecart.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Item that has a specific right and left click function different from Vanilla.
 */
public abstract class ActionItem extends Item {

    public ActionItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            this.click(miner, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && playerEntity != null) {
            BlockPos blockPos = context.getBlockPos();
            if (!this.click(playerEntity, world, blockPos, true, context.getStack())) {
                return ActionResult.FAIL;
            }
        }

        return ActionResult.SUCCESS;
    }

    /**
     *
     * @param player
     * @param world
     * @param pos
     * @param rightClick
     * @return true if the action was successful and the player hand animation should play
     */
    public abstract boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand);

}
