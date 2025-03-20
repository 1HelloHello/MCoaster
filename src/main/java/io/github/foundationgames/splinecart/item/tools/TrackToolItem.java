package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.World;

/**
 * Handles the functionality for the two track tools (track type and track style)
 */
public class TrackToolItem extends ToolItem {

    public final Type type;

    public TrackToolItem(Type type, String identifier, RegistryKey<Item> registryKey) {
        super(identifier, registryKey);
        this.type = type;
    }

    @Override
    public boolean use(TrackMarkerBlockEntity marker, PlayerEntity player, World world, boolean rightClick) {
        int oldValue = getValueForTool(marker);
        int newValue = (oldValue + (rightClick ? 1 : -1) + type.settings) % type.settings;
        do {
            setValueForTool(marker, newValue);
            marker.markDirty();
            marker.sync();
            marker = marker.getNextMarker();
        } while(player.isSneaking() && marker != null && getValueForTool(marker)== oldValue);
        return true;
    }

    private int getValueForTool(TrackMarkerBlockEntity marker) {
        return switch (type) {
            case STYLE -> marker.nextStyle.ordinal();
            case TYPE -> marker.nextType.ordinal();
        };
    }

    private void setValueForTool(TrackMarkerBlockEntity marker, int value) {
        switch(type) {
            case STYLE -> marker.nextStyle = TrackStyle.read(value);
            case TYPE -> marker.nextType = TrackType.read(value);
        }
    }

    public enum Type {
        STYLE(8),
        TYPE(3)
        ;
        /**
         * How many different options there are to cycle through.
         */
        public final int settings;

        Type(int settings) {
            this.settings = settings;
        }

    }

    @Override
    protected String writeCurrentState(TrackMarkerBlockEntity marker) {
        return switch(type) {
            case STYLE -> marker.nextStyle.name;
            case TYPE -> marker.nextType.name;
        };
    }

}
