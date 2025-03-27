package io.github.foundationgames.splinecart.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Item that has a specific right and left click function different from Vanilla.
 */
public abstract class ActionItem extends RegistrableItem {

    public ActionItem(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey, 1);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        this.click(miner, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        if(playerEntity == null) {
            return ActionResult.PASS;
        }
        BlockPos blockPos = context.getBlockPos();
        if (this.click(playerEntity, world, blockPos, true, context.getStack())) {
            return ActionResult.SUCCESS;
        }else {
            return ActionResult.PASS;
        }
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
