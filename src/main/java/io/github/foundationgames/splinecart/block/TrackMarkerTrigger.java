package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.item.tools.PowerToolItem;
import io.github.foundationgames.splinecart.item.tools.TriggerTool;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrackMarkerTrigger {

    private final BlockPos location;
    private final int power;
    private final int strength;

    public TrackMarkerTrigger(NbtCompound compound) {
        location = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
        power = compound.getInt("power");
        strength = compound.getInt("strength");
    }

    public TrackMarkerTrigger(BlockPos location, int power, int strength) {
        this.location = location;
        this.power = power;
        this.strength = strength;
    }

    public boolean execute(World world) {
        if(world.getBlockEntity(location) instanceof TrackMarkerBlockEntity trackMarker) {
            trackMarker.setPower(power);
            trackMarker.setStrength(strength);
            trackMarker.markDirty();
            trackMarker.sync();
            return true;
        }
        return false;
    }

    public NbtCompound getNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("x", location.getX());
        compound.putInt("y", location.getY());
        compound.putInt("z", location.getZ());
        compound.putInt("power", power);
        compound.putInt("strength", strength);
        return compound;
    }

    public String getDisplayString(String format) {
        return String.format(format,
                location.getX(),
                location.getY(),
                location.getZ(),
                power == Integer.MAX_VALUE ? "unset" : PowerToolItem.toFixedPointRepresentation(power),
                strength == Integer.MAX_VALUE ? "unset" : PowerToolItem.toFixedPointRepresentation(strength)
        );
    }

    public BlockPos getLocation() {
        return location;
    }

    public double getPower() {
        return power;
    }
}
