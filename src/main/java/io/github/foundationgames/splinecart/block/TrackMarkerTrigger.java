package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.item.tools.PowerToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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
        if(world.getBlockEntity(location) instanceof TrackMarkerBlockEntity marker) {
            if(marker.getPower() == power && marker.getStrength() == strength) {
                sendWarning(world);
                return true;
            }
            marker.setPower(power);
            marker.setStrength(strength);
            marker.markDirty();
            marker.sync();
            sendStatus(world);
            return true;
        }
        sendError(world);
        return false;
    }

    private void sendError(World world) {
        broadcastMessage(world, Text.of("[TRIGGER ERROR] No marker at location " + location.toShortString()));
    }

    private void sendWarning(World world) {
        broadcastMessage(world, Text.of("[TRIGGER WARNING] Nothing changed at " + location.toShortString()));
    }

    private void sendStatus(World world) {
        broadcastMessage(world, Text.of("[TRIGGER STATUS] Successfully updated " + location.toShortString()));
    }

    /**
     * Broadcasts the message to all players in the world, if the world is a ServerWorld and the gamerule "triggerOutput" is set to true
     * @param world
     * @param text
     */
    private static void broadcastMessage(World world, Text text) {
        if(world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(Splinecart.TRIGGER_OUTPUT)) {
            serverWorld.getPlayers().forEach(player -> player.sendMessage(text));
        }
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
