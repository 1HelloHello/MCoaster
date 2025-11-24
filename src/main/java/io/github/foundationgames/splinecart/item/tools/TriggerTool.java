package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.block.TrackMarkerTrigger;
import io.github.foundationgames.splinecart.block.TrackMarkerTriggers;
import io.github.foundationgames.splinecart.mixin_interface.PlayerMixinInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class TriggerTool extends ToolItem {

    public TriggerTool(RegistryKey<Item> registryKey) {
        super(registryKey);
    }

    @Override
    protected Text getCurrentStateMessage(TrackMarkerBlockEntity marker) {
        if(marker.triggers.triggers.isEmpty()) {
            return Text.of("No triggers saved");
        }
        StringBuilder msg = new StringBuilder();
        ArrayList<TrackMarkerTrigger> triggers = marker.triggers.triggers;
        for(int i = 0; i < triggers.size(); i++) {
            if(i != 0) {
                msg.append("      ");
            }
            msg.append(triggers.get(i).getDisplayString("(%d, %d, %d) p: %s s: %s"));
        }
        return Text.of(msg.toString());
    }

    @Override
    public boolean use(TrackMarkerBlockEntity marker, PlayerEntity playerEntity, World world, boolean rightClick) {
        PlayerMixinInterface player = (PlayerMixinInterface) playerEntity;
        if(!rightClick) {
            return selectTrigger(player, playerEntity, marker.getPos());
        }
        if(playerEntity.isSneaking()) {
            return removeAllTriggers(playerEntity, marker);
        }
        if(player.getSelectedTrigger() == null || !(world.getBlockEntity(player.getSelectedTrigger()) instanceof TrackMarkerBlockEntity savedMarker)) {
            return false;
        }
        return addTrigger(playerEntity, marker, savedMarker);
    }

    private boolean selectTrigger(PlayerMixinInterface player, PlayerEntity playerEntity, BlockPos pos) {
        if(pos.equals(player.getSelectedTrigger()))
            return false;
        player.setSelectedTrigger(pos);
        sendChatMessage(playerEntity, "Selected marker " + pos.toShortString());
        return true;
    }

    private boolean removeAllTriggers(PlayerEntity playerEntity, TrackMarkerBlockEntity marker) {
        if(marker.triggers.triggers.isEmpty())
            return false;
        marker.triggers = new TrackMarkerTriggers();
        marker.markDirty();
        sendChatMessage(playerEntity, "Removed all triggers.");
        return true;
    }

    private boolean addTrigger(PlayerEntity playerEntity, TrackMarkerBlockEntity marker, TrackMarkerBlockEntity savedMarker) {
        TrackMarkerTrigger trigger = new TrackMarkerTrigger(savedMarker);
        if(marker.triggers.contains(trigger)) {
            return false;
        }
        marker.triggers.containsPos(trigger.getLocation()).ifPresent(duplicate -> {
            marker.triggers.triggers.remove(duplicate);
        });
        marker.triggers.triggers.add(trigger);
        marker.markDirty();
        sendChatMessage(playerEntity, "Added new Trigger: " + trigger.getDisplayString("(%d, %d, %d) power: %s setting: %s"));
        return true;
    }

    private static void sendChatMessage(PlayerEntity player, String text) {
        if(player.getWorld().isClient)
            player.sendMessage(Text.of(text), false);
    }

    @Override
    protected String writeCurrentState(TrackMarkerBlockEntity marker) {
        return "";
    }

}
