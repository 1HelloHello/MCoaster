package io.github.foundationgames.splinecart.block;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class TrackMarkerTrigger {

    private final BlockPos location;
    private final double power;

    public TrackMarkerTrigger(NbtCompound compound) {
        location = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
        power = compound.getDouble("power");
    }

    public TrackMarkerTrigger(BlockPos location, double power) {
        this.location = location;
        this.power = power;
    }

    public boolean execute(World world) {
        System.out.println("trigger execute"); // TODO
        if(world.getBlockEntity(location) instanceof TrackMarkerBlockEntity trackMarker) {
            System.out.println("track marker block found");
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
        compound.putDouble("power", power);
        return compound;
    }

    public BlockPos getLocation() {
        return location;
    }

    public double getPower() {
        return power;
    }
}
