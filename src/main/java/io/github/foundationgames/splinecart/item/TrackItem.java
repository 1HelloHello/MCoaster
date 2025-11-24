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

    public TrackItem(RegistryKey<Item> registryKey) {
        super(registryKey);
    }

    @Override
    public boolean click(PlayerEntity playerEntity, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity trackMarker)) {
            return false;
        }
        PlayerMixinInterface player = (PlayerMixinInterface) playerEntity;
        if(!rightClick) {
            return selectNewMarker(player, world, pos);
        }
        return rightClick(player, world, trackMarker);
    }

    private boolean selectNewMarker(PlayerMixinInterface player, World world, BlockPos pos) {
        BlockPos selectedMarker = player.getTrackSelectedMarker();
        if(pos.equals(selectedMarker)) {
            return false;
        }
        if(world.getBlockEntity(selectedMarker) instanceof TrackMarkerBlockEntity) {
            player.setLastTrackSelectedMarker(selectedMarker);
        }
        player.setTrackSelectedMarker(pos);
        return true;
    }

    private boolean rightClick(PlayerMixinInterface player, World world, TrackMarkerBlockEntity trackMarker) {
        if (connectToMarker(player, world, trackMarker, player.getTrackSelectedMarker())) {
            return true;
        }
        return connectToMarker(player, world, trackMarker, player.getLastTrackSelectedMarker());
    }

    private boolean connectToMarker(PlayerMixinInterface player, World world, TrackMarkerBlockEntity endMarker, BlockPos start) {
        BlockPos end = endMarker.getPos();
        if(end.equals(start))
            return false;
        if(world.getBlockEntity(start) instanceof TrackMarkerBlockEntity startMarker) {
            startMarker.setNext(end);
            world.playSound(null, end, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
            selectNewMarker(player, world, end);
            return true;
        }
        return false;
    }

}
