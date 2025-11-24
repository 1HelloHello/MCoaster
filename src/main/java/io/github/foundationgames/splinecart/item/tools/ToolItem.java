package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.item.ActionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An ActionItem that can only be used on Track Markers and that has a specific value that needs to be displayed in when looking at a Track Marker.
 */
public abstract class ToolItem extends ActionItem {

    public ToolItem(RegistryKey<Item> registryKey) {
        super(registryKey);
    }

    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker)) {
            return false;
        }
        return use(marker, player, world, rightClick);
    }

    /**
     * Sends a message to the players actionbar about the property (i.e. banking) of the trackMarker.
     * @param player the player to send the message to
     * @param marker the trackMarker
     */
    public void sendCurrentStateMessage(PlayerEntity player, TrackMarkerBlockEntity marker) {
        player.sendMessage(getCurrentStateMessage(marker), true);
    }

    /**
     * Overwrite if you want a completely custom message instead of the normal "name": "value"
     * @param marker The Track Marker the player is looking at
     * @return The full text that should be displayed
     */
    protected Text getCurrentStateMessage(TrackMarkerBlockEntity marker) {
        String langPath = "item." + Splinecart.MOD_NAME + "." + identifier + ".msg";
        MutableText text = Text.translatable(langPath).append(writeCurrentState(marker));
        text.withColor(getTextColor());
        return text;
    }

    /**
     *
     * @param marker The Track Marker the player is looking at
     * @return the current state that should be displayed in the players text bar without prefix.
     */
    protected abstract String writeCurrentState(TrackMarkerBlockEntity marker);

    /**
     * Overwrite to change from the default White.
     * @return The color the text message for the current state should be displayed at.
     */
    protected int getTextColor() {
        return Colors.WHITE;
    }

    /**
     * Gets called when a marker block gets clicked (left OR right).
     * @param marker the marker block that got clicked
     * @param player the player that clicked on it
     * @param world the world
     * @param rightClick true if it was a right click (use), false if it was a left click (attack)
     * @return true if the player should shake the hand for this action (only works in case of right click)
     */
    public abstract boolean use(TrackMarkerBlockEntity marker, PlayerEntity player, World world, boolean rightClick);

}
