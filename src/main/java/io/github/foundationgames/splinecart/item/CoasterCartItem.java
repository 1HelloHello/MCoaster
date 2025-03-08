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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoasterCartItem extends ActionItem {

    public static final double INITIAL_VELOCITY = 0.01;

    public CoasterCartItem(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
    }

    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stack) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity trackMarker)) {
            sendErrorNoMarker(player);
            return false;
        }
        TrackFollowerEntity follower = TrackFollowerEntity.create(world, pos, player.isSneaking() ? INITIAL_VELOCITY : trackMarker.getLastVelocity());
        if (follower != null) {
            world.spawnEntity(follower);
            MinecartEntity minecart = AbstractMinecartEntity.create(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    (EntityType<MinecartEntity>) EntityType.get("minecraft:minecart").get(), SpawnReason.COMMAND, stack, player);
            world.spawnEntity(minecart);
            minecart.startRiding(follower, true);
            if(!rightClick) {
                player.startRiding(minecart);
            }
        }
        return true;
    }

    private void sendErrorNoMarker(PlayerEntity player) {
        MutableText text = Text.translatable("item.splinecart.coaster_cart.error_no_marker");
        text.withColor(Colors.WHITE);
        sendMessage(player, text);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, true);
    }

}
