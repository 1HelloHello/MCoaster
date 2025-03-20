package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.item.CoasterCartItem;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackColorPreset;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.util.Pose;
import io.github.foundationgames.splinecart.util.SUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.Objects;

public class TrackMarkerBlockEntity extends BlockEntity {

    public static final int ORIENTATION_RESOLUTION = 360;

    public float clientTime = 0;

    public TrackType nextType = TrackType.DEFAULT;
    public TrackStyle nextStyle = TrackStyle.DEFAULT;
    public TrackColor nextColor = TrackColorPreset.WHITE.get();

    private @Nullable BlockPos nextTrackMarkerPos = null;
    private @Nullable BlockPos prevTrackMarkerPos = null;

    private Pose pose;

    private int power = Integer.MAX_VALUE;
    private int strength = Integer.MAX_VALUE;
    public TrackMarkerTriggers triggers = new TrackMarkerTriggers();

    private double lastVelocity = CoasterCartItem.INITIAL_VELOCITY;

    public int heading = Integer.MAX_VALUE;
    public int pitching = 0;
    public int banking = 0;
    public int relative_orientation = 0;

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(Splinecart.TRACK_TIES_BE, pos, state);
        updatePose();
    }

    public void updatePose() {
        BlockPos blockPos = getPos();
        updateHeadingFromBlock();
        Vector3d pos = new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        Matrix3d basis = new Matrix3d();

        basis.rotateY(-heading * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateX(-pitching * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateY(-relative_orientation * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateZ(-banking* MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));

        this.pose = new Pose(pos, basis);
    }


    @SuppressWarnings("deprecation")
    @Override
    public void setCachedState(BlockState state) {
        super.setCachedState(state);

        updatePose();
    }

    public static @Nullable TrackMarkerBlockEntity of(World world, @Nullable BlockPos pos) {
        if (pos != null && world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity e) {
            return e;
        }

        return null;
    }

    /**
     * Gets called when this Track Marker gets connected to another Track Marker.
     * @param pos The location of the Track Marker, this is connected with (the next one)
     */
    public void setNext(@Nullable BlockPos pos) {
        removeNextMarker();
        nextTrackMarkerPos = pos;
        TrackMarkerBlockEntity prevMarker = getPrevMarker();
        if(prevMarker != null) {
            nextColor = prevMarker.nextColor;
            nextStyle = prevMarker.nextStyle;
            nextType = prevMarker.nextType;
        }
        TrackMarkerBlockEntity nextMarker = getNextMarker();
        if (nextMarker != null) {
            TrackMarkerBlockEntity nextPrevMarker = nextMarker.getPrevMarker();
            nextMarker.removePrevMarker();
            if(nextPrevMarker != null) {
                nextPrevMarker.sync();
                nextPrevMarker.markDirty();
            }
            nextMarker.prevTrackMarkerPos = getPos();

            nextMarker.sync();
            nextMarker.markDirty();
        }

        sync();
        markDirty();
    }

    public void removePrevMarker() {
        if(getPrevMarker() != null) {
            getPrevMarker().setNextTrackMarkerPos(null);
        }
        prevTrackMarkerPos = null;
    }

    public void removeNextMarker() {
        if(getNextMarker() != null) {
            getNextMarker().setPrevTrackMarkerPos(null);
        }
        nextTrackMarkerPos = null;
    }

    public @Nullable TrackMarkerBlockEntity getNextMarker() {
        assert world != null;
        return nextTrackMarkerPos == null ? null : (TrackMarkerBlockEntity) world.getBlockEntity(nextTrackMarkerPos);
    }

    public @Nullable TrackMarkerBlockEntity getPrevMarker() {
        assert world != null;
        return prevTrackMarkerPos == null ? null : (TrackMarkerBlockEntity) world.getBlockEntity(prevTrackMarkerPos);
    }

    public @Nullable BlockPos getNextTrackMarkerPos() {
        return nextTrackMarkerPos;
    }

    public void setNextTrackMarkerPos(@Nullable BlockPos nextTrackMarkerPos) {
        this.nextTrackMarkerPos = nextTrackMarkerPos;
    }

    public @Nullable BlockPos getPrevTrackMarkerPos() {
        return prevTrackMarkerPos;
    }

    public void setPrevTrackMarkerPos(@Nullable BlockPos prevTrackMarkerPos) {
        this.prevTrackMarkerPos = prevTrackMarkerPos;
    }

    public boolean hasTrackConnected() {
        return prevTrackMarkerPos != null || nextTrackMarkerPos != null;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int computePower() {
        TrackMarkerBlockEntity marker = this;
        int counter = 0;
        do {
            if(marker.power != Integer.MAX_VALUE) {
                return marker.power;
            }
            marker = marker.getPrevMarker();
            counter++;
        }while(marker != null && marker != this && counter < 100);
        return 0;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int computeStrength() {
        TrackMarkerBlockEntity marker = this;
        int counter = 0;
        do {
            if(marker.strength != Integer.MAX_VALUE) {
                return marker.strength;
            }
            marker = marker.getPrevMarker();
            counter++;
        }while(marker != null && marker != this && counter < 100);
        return 100;
    }

    public double getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(double lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public Pose pose() {
        updatePose();
        return this.pose;
    }

    /**
     * Gets called when the Track Marker Block gets broken.
     */
    public void onDestroy() {
        var prevMarker = getPrevMarker();
        if (prevMarker != null) {
            prevMarker.nextTrackMarkerPos = null;
            prevMarker.sync();
            prevMarker.markDirty();
        }
        var nextMarker = getNextMarker();
        if (nextMarker != null) {
            nextMarker.prevTrackMarkerPos = null;
            nextMarker.sync();
            nextMarker.markDirty();
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        assert world != null;
        prevTrackMarkerPos = SUtil.getBlockPos(nbt, "prev");
        nextTrackMarkerPos = SUtil.getBlockPos(nbt, "next");

        nextType = TrackType.read(nbt.getInt("track_type"));
        nextStyle = TrackStyle.read(nbt.getInt("track_style"));
        nextColor = new TrackColor(nbt.getInt("track_color"));

        power = nbt.getInt("power");
        strength = nbt.getInt("strength");
        triggers = new TrackMarkerTriggers((NbtList) nbt.get("triggers"));

        lastVelocity = nbt.getDouble("last_velocity");

        heading = nbt.getInt("heading");
        pitching = nbt.getInt("pitching");
        banking = nbt.getInt("banking");
        relative_orientation = nbt.getInt("relative_orientation");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        SUtil.putBlockPos(nbt, getPrevTrackMarkerPos(), "prev");
        SUtil.putBlockPos(nbt, getNextTrackMarkerPos(), "next");

        nbt.putInt("track_type", this.nextType.ordinal());
        nbt.putInt("track_style", this.nextStyle.ordinal());
        nbt.putInt("track_color", this.nextColor.hex());

        nbt.putInt("power", this.power);
        nbt.putInt("strength", this.strength);
        nbt.put("triggers", triggers.getNbt());

        nbt.putDouble("last_velocity", this.lastVelocity);

        updateHeadingFromBlock();
        nbt.putInt("heading", this.heading);
        nbt.putInt("pitching", this.pitching);
        nbt.putInt("banking", this.banking);
        nbt.putInt("relative_orientation", this.relative_orientation);
    }

    private void updateHeadingFromBlock() {
        if(heading == Integer.MAX_VALUE
                && world != null
                && world.getBlockState(getPos()).getBlock() instanceof TrackMarkerBlock block
                && block.itemPlacementContext != null) {
            if(block.itemPlacementContext.getPlayer() == null)
                return;
            heading = (block.itemPlacementContext.getPlayer().isSneaking()
                    ? round((int) block.itemPlacementContext.getPlayerYaw(), 5) % ORIENTATION_RESOLUTION
                    : round((int) block.itemPlacementContext.getPlayerYaw(), 45) % ORIENTATION_RESOLUTION);
            markDirty();
        }
    }

    /**
     * Rounds the value to the nearest multiple of increment.
     * @param value
     * @param increment
     * @return
     */
    private static int round(int value, int increment) {
        return ((value + ORIENTATION_RESOLUTION) + increment / 2) / increment * increment;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    public void sync() {
        Objects.requireNonNull(getWorld()).updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }

}
