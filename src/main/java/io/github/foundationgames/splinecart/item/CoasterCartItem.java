package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
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

    public CoasterCartItem(RegistryKey<Item> registryKey) {
        super(registryKey);
    }

    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stack) {
        TrackMarkerBlockEntity trackMarker = null;
        if(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity t) {
            trackMarker = t;
        } else {
            pos = ((PlayerMixinInterface) player).getLastCoasterStart();
            if(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity t) {
                trackMarker = t;
            }
        }
        if(trackMarker == null) {
            return false;
        }
        if(trackMarker.hasNoTrackConnected()) {
            return false;
        }
        createCart(player, world, rightClick, stack, trackMarker);
        ((PlayerMixinInterface) player).setLastCoasterStart(pos);
        return true;
    }

    private static void createCart(PlayerEntity player, World world, boolean rightClick, ItemStack stack, TrackMarkerBlockEntity trackMarker) {
        TrackFollowerEntity follower = TrackFollowerEntity.create(world, trackMarker, player.isSneaking() ? 0 : trackMarker.computeLastVelocity());
        world.spawnEntity(follower);
        BlockPos pos = trackMarker.getPos();

        MinecartEntity minecart = AbstractMinecartEntity.create(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                EntityType.MINECART, SpawnReason.COMMAND, stack, player);
        world.spawnEntity(minecart);
        assert minecart != null;
        minecart.startRiding(follower, true);
        if(!rightClick) {
            player.startRiding(minecart);
        }

    }

}
