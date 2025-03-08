package io.github.foundationgames.splinecart.block;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrackMarkerTrigger {

    private final BlockPos location;
    private final int power;

    public TrackMarkerTrigger(NbtCompound compound) {
        location = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
        power = compound.getInt("power");
    }

    public TrackMarkerTrigger(BlockPos location, int power) {
        this.location = location;
        this.power = power;
    }

    public boolean execute(World world) {
        if(world.getBlockEntity(location) instanceof TrackMarkerBlockEntity trackMarker) {
            trackMarker.setPower(power);
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
        return compound;
    }

    public BlockPos getLocation() {
        return location;
    }

    public double getPower() {
        return power;
    }
}
