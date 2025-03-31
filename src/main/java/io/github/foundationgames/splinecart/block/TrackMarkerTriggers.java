package io.github.foundationgames.splinecart.block;

import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Optional;

public class TrackMarkerTriggers {

    public final ArrayList<TrackMarkerTrigger> triggers;

    public TrackMarkerTriggers(ArrayList<TrackMarkerTrigger> triggers) {
        this.triggers = triggers;
    }

    public TrackMarkerTriggers() {
        triggers = new ArrayList<>();
    }

    public TrackMarkerTriggers(NbtList list) {
        triggers = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            triggers.add(new TrackMarkerTrigger(list.getCompound(i)));
        }
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
