package io.github.foundationgames.splinecart.block;

import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.ArrayList;

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

}
