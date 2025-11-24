package io.github.foundationgames.splinecart.item.tools;

import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.World;

public class PowerToolItem extends ToolItem {

    /**
     * The lower increment used when shift clicking
     */
    private static final int LOW_INCREMENT = 5;

    /**
     * The higher increment used when normal clicking
     */
    private static final int HIGH_INCREMENT = 100;

    public final Type type;

    public PowerToolItem(Type type, RegistryKey<Item> registryKey) {
        super(registryKey);
        this.type = type;
    }

    @Override
    public boolean use(TrackMarkerBlockEntity marker, PlayerEntity player, World world, boolean rightClick) {
        int oldValue = getValueForTool(marker);
        int newValue;
        if(oldValue == Integer.MAX_VALUE) {
            newValue = rightClick ? 0 : (player.isSneaking() ? -LOW_INCREMENT : -HIGH_INCREMENT);
        }else {
            newValue = oldValue + (rightClick ? 1 : -1) * (player.isSneaking() ? LOW_INCREMENT : HIGH_INCREMENT);
            if((oldValue < 0 && newValue >= 0) || (oldValue >= 0 && newValue < 0)) {
                newValue = Integer.MAX_VALUE;
            }
        }
        setValueForTool(marker, newValue);
        marker.markDirty();
        marker.sync();
        return true;
    }

    private int getValueForTool(TrackMarkerBlockEntity marker) {
        return switch (type) {
            case POWER -> marker.getPower();
            case STRENGTH -> marker.getStrength();
        };
    }

    private void setValueForTool(TrackMarkerBlockEntity marker, int value) {
        switch(type) {
            case POWER -> marker.setPower(value);
            case STRENGTH -> marker.setStrength(value);
        }
    }

    @Override
    protected String writeCurrentState(TrackMarkerBlockEntity marker) {
        return getValueForTool(marker) == Integer.MAX_VALUE ? "Unset" : toFixedPointRepresentation(getValueForTool(marker));
    }

    @Override
    protected int getTextColor() {
        return switch(type) {
            case POWER -> Colors.LIGHT_RED;
            case STRENGTH -> Colors.BLUE;
        };
    }

    public enum Type {
        POWER,
        STRENGTH
        ;
    }

    /**
     *
     * @param val
     * @return the val as a fixed point representation where
     */
    public static String toFixedPointRepresentation(int val) {
        return val / 10 + "." + Math.abs(val % 10);
    }
}
