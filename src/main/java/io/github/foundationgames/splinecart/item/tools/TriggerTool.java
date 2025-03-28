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

    /**
     * The suffix to be used for all chat messages
     */
    public static final String SUFFIX = "[Trigger] ";

    public TriggerTool(String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
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
        BlockPos pos = marker.getPos();
        if(!rightClick) {
            return selectTrigger(player, playerEntity, pos);
        }
        if(playerEntity.isSneaking()) {
            return removeAllTriggers(playerEntity, marker);
        }
        if(player.getSelectedTrigger() == null || !(world.getBlockEntity(player.getSelectedTrigger()) instanceof TrackMarkerBlockEntity savedMarker)) {
            return false;
        }
        return addTrigger(player, playerEntity, marker, savedMarker);
    }

    private boolean selectTrigger(PlayerMixinInterface player, PlayerEntity playerEntity, BlockPos pos) {
        player.setSelectedTrigger(pos);
        sendChatMessage(playerEntity, String.format("Selected block (%d, %d, %d)",
                pos.getX(),
                pos.getY(),
                pos.getZ()));
        return true;
    }

    private boolean removeAllTriggers(PlayerEntity playerEntity, TrackMarkerBlockEntity marker) {
        marker.triggers = new TrackMarkerTriggers();
        marker.markDirty();
        sendChatMessage(playerEntity, "Removed all triggers.");
        return true;
    }

    private boolean addTrigger(PlayerMixinInterface player, PlayerEntity playerEntity, TrackMarkerBlockEntity marker, TrackMarkerBlockEntity savedMarker) {
        BlockPos selectedTrigger = player.getSelectedTrigger();
        TrackMarkerTrigger trigger = new TrackMarkerTrigger(selectedTrigger, savedMarker.getPower(), savedMarker.getStrength());
        marker.triggers.triggers.add(trigger);
        marker.markDirty();
        sendChatMessage(playerEntity, "Added new Trigger: " + trigger.getDisplayString("(%d, %d, %d) power: %s setting: %s"));
        return true;
    }

    private static void sendChatMessage(PlayerEntity player, String text) {
        if(player.getWorld().isClient)
            player.sendMessage(Text.of(SUFFIX + text), false);
    }

    @Override
    protected String writeCurrentState(TrackMarkerBlockEntity marker) {
        return "";
    }

}
