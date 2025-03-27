package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoasterCartItem extends ActionItem {

    public static final double INITIAL_VELOCITY = 0;

    public CoasterCartItem(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
    }

    @SuppressWarnings("unchecked")
    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stack) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity trackMarker)) {
            return false;
        }
        if(trackMarker.hasNoTrackConnected()) {
            return true;
        }
        TrackFollowerEntity follower = TrackFollowerEntity.create(world, pos, player.isSneaking() ? INITIAL_VELOCITY : trackMarker.computeLastVelocity());
        if (follower != null) {
            world.spawnEntity(follower);

            MinecartEntity minecart = AbstractMinecartEntity.create(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    (EntityType<MinecartEntity>) EntityType.get("minecraft:minecart").orElseThrow(), SpawnReason.COMMAND, stack, player);
            if(minecart == null)
                return false;
            world.spawnEntity(minecart);
            minecart.startRiding(follower, true);
            if(!rightClick) {
                player.startRiding(minecart);
            }
        }
        return true;
    }

}
