package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.item.ActionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ToolItem extends ActionItem {

    public final ToolType type;

    public ToolItem(ToolType type, String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
        this.type = type;
    }

    public boolean click(PlayerEntity player, World world, BlockPos pos, boolean rightClick, ItemStack stackInHand) {
        if(!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity marker)) {
            return false;
        }

        int newVal = use(pos, marker, rightClick, player.isSneaking());

        player.sendMessage(Text.of(type.currentStateMsg.get(newVal)), true);
        return true;
    }

    public void sendCurrentStateMessage(PlayerEntity player, TrackMarkerBlockEntity marker) {
        player.sendMessage(Text.of(type.currentStateMsg.get(marker.getValueForTool(type))), true);
    }

    public abstract int use(BlockPos pos, TrackMarkerBlockEntity marker, boolean rightClick, boolean isSneaking);

}
