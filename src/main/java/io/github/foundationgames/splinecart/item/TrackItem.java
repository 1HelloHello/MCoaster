package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrackItem extends ActionItem {

    public TrackItem(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
    }

    @Override
    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if (world.isClient()) {
            return true;
        }
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity)) {
            return false;
        }
        PlayerMixinInterface playerInterface = (PlayerMixinInterface) player;
        if(!rightClick) {
            playerInterface.setTrackSelectedMarker(pos);
            return true;
        }
        BlockPos otherPos = playerInterface.getTrackSelectedMarker();
        if(otherPos == null) {
            return false;
        }
        if (!pos.equals(otherPos) && world.getBlockEntity(otherPos) instanceof TrackMarkerBlockEntity otherMarker) {
            otherMarker.setNext(pos);
            world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
        }
        playerInterface.setTrackSelectedMarker(pos);
        return false;
    }

}
