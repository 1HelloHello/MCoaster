package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.block.TrackMarkerTrigger;
import io.github.foundationgames.splinecart.item.ActionItem;
import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TriggerTool extends ActionItem {

    public TriggerTool(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
    }

    @Override
    public boolean click(PlayerEntity playerEntity, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker)) {
            return false;
        }
        PlayerMixinInterface player = (PlayerMixinInterface) playerEntity;
        if(!rightClick) {
            player.setSelectedTrigger(pos);
            return true;
        }
        if(player.getSelectedTrigger() == null || !(world.getBlockEntity(player.getSelectedTrigger()) instanceof TrackMarkerBlockEntity savedMarker)) {
            return false;
        }
        marker.triggers.triggers.add(new TrackMarkerTrigger(player.getSelectedTrigger(), savedMarker.getPower(), savedMarker.getStrength()));
        return true;
    }

}
