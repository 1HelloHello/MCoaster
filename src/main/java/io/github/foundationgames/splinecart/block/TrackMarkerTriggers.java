package io.github.foundationgames.splinecart.block;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record TrackMarkerTriggers(List<TrackMarkerTrigger> triggers) {

    public TrackMarkerTriggers() {
        this(new ArrayList<>());
    }

    public TrackMarkerTriggers(NbtList list) {
        this(list.stream()
                .map(e -> e instanceof NbtCompound c ? c : new NbtCompound())
                .map(TrackMarkerTrigger::new)
                .collect(Collectors.toList()));
    }

    public NbtList getNbt() {
        NbtList list = new NbtList();
        for (TrackMarkerTrigger trigger : triggers) {
            list.add(trigger.getNbt());
        }
        return list;
    }

    public void execute(World world) {
        triggers.forEach((t) -> t.execute(world));
    }

    public boolean contains(TrackMarkerTrigger trigger) {
        for(TrackMarkerTrigger other : triggers) {
            if(other.equals(trigger)) {
                return true;
            }
        }
        return false;
    }

    public Optional<TrackMarkerTrigger> containsPos(BlockPos pos) {
        for(TrackMarkerTrigger otherTrigger : triggers) {
            if(otherTrigger.getLocation().equals(pos)) {
                return Optional.of(otherTrigger);
            }
        }
        return Optional.empty();
    }

}
