package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.block.TrackMarkerTrigger;
import io.github.foundationgames.splinecart.block.TrackMarkerTriggers;
import io.github.foundationgames.splinecart.item.ActionItem;
import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TriggerTool extends ActionItem {

    public TriggerTool(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
    }

    @Override
    public boolean click(PlayerEntity playerEntity, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(world.isClient()) {
            return false;
        }
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker)) {
            return false;
        }
        PlayerMixinInterface player = (PlayerMixinInterface) playerEntity;
        if(!rightClick) {
            player.setSelectedTrigger(pos);
            playerEntity.sendMessage(Text.of(
                    String.format("[Trigger] Selected block (%d, %d, %d)",
                            pos.getX(),
                            pos.getY(),
                            pos.getZ())),
                    false);
            return true;
        }
        if(playerEntity.isSneaking()) {
            marker.triggers = new TrackMarkerTriggers();
            playerEntity.sendMessage(Text.of("[Trigger] Removed all triggers."),
                    false);
            return true;
        }
        if(player.getSelectedTrigger() == null || !(world.getBlockEntity(player.getSelectedTrigger()) instanceof TrackMarkerBlockEntity savedMarker)) {
            return false;
        }
        BlockPos selectedTrigger = player.getSelectedTrigger();
        marker.triggers.triggers.add(new TrackMarkerTrigger(selectedTrigger, savedMarker.getPower(), savedMarker.getStrength()));
        playerEntity.sendMessage(Text.of(
                String.format("[Trigger] Added new trigger to (%d, %d, %d) with power %s and setting %s",
                        selectedTrigger.getX(),
                        selectedTrigger.getY(),
                        selectedTrigger.getZ(),
                        savedMarker.getPower() == Integer.MAX_VALUE ? "unset" : savedMarker.getPower(),
                        savedMarker.getStrength() == Integer.MAX_VALUE ? "unset" : savedMarker.getStrength())),
                false);
        return true;
    }

}
