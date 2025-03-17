package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
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
    public boolean click(PlayerEntity playerEntity, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if (world.isClient()) {
            return true;
        }
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity)) {
            return false;
        }
        PlayerMixinInterface player = (PlayerMixinInterface) playerEntity;
        if(!rightClick) {
            return selectNewMarker(player, pos, world);
        }
        return connectToMarker(player, world, pos);
    }

    private boolean selectNewMarker(PlayerMixinInterface player, BlockPos pos, World world) {
        if(pos.equals(player.getTrackSelectedMarker())) {
            return true;
        }
        if(checkMarkerPos(player.getTrackSelectedMarker(), world)) {
            player.setLastTrackSelectedMarker(player.getTrackSelectedMarker());
        }
        player.setTrackSelectedMarker(pos);
        return true;
    }

    private boolean connectToMarker(PlayerMixinInterface player, World world, BlockPos pos) {
        BlockPos otherPos = player.getTrackSelectedMarker();
        if(!checkMarkerPos(otherPos, world) && !pos.equals(otherPos)) {
            BlockPos lastSelectedMarker = player.getLastTrackSelectedMarker();
            if(!checkMarkerPos(lastSelectedMarker, world) && !pos.equals(lastSelectedMarker)) {
                return false;
            }
            otherPos = player.getLastTrackSelectedMarker();
        }
        TrackMarkerBlockEntity otherMarker = (TrackMarkerBlockEntity) world.getBlockEntity(otherPos);
        otherMarker.setNext(pos);
        world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
        selectNewMarker(player, pos, world);
        return false;
    }

    private boolean checkMarkerPos(BlockPos pos, World world) {
        return pos != null  && world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity;
    }

}
