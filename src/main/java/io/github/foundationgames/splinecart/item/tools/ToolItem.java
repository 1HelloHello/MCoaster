package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.item.ActionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ToolItem extends ActionItem {

    public final ToolType type;

    public ToolItem(ToolType type, Settings settings) {
        super(settings);
        this.type = type;
    }

    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker)) {
            sendErrorNoMarker(player);
            return false;
        }

        int newVal = use(pos, marker, rightClick, player.isSneaking());

        sendMessage(player, Text.of(type.currentStateMsg.get(newVal)));
        return true;
    }

    public abstract int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking);

    private void sendErrorNoMarker(PlayerEntity player) {
        MutableText text = Text.translatable("item.splinecart.tools.error_no_marker");
        text.withColor(Colors.WHITE);
        sendMessage(player, text);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, true);
    }

}
