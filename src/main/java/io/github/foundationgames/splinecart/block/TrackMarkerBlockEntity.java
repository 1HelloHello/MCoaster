package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.util.InterpolationResult;
import io.github.foundationgames.splinecart.util.Pose;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrackMarkerBlockEntity extends BlockEntity {
    public float clientTime = 0;

    public TrackType nextType = TrackType.DEFAULT;
    public TrackStyle nextStyle = TrackStyle.DEFAULT;
    public Color nextColor = TrackColor.WHITE.color;

    private BlockPos nextTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
    private BlockPos prevTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;

    private int power = Integer.MAX_VALUE;
    private int strength = Integer.MAX_VALUE;
    public List<TrackMarkerTrigger> triggers = new ArrayList<>(0);

    private double lastVelocity = 0;

    public final Pose pose = new Pose();

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(Splinecart.TRACK_TIES_BE, pos, state);
    }

    /**
     * Gets called when this Track Marker gets connected to another Track Marker.
     * @param pos The location of the Track Marker, this is connected with (the next one)
     */
    public void setNext(BlockPos pos) {
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
                nextPrevMarker.markDirty();
            }
            nextMarker.prevTrackMarkerPos = getPos();

            nextMarker.markDirty();
        }

        markDirty();
    }

    public void removePrevMarker() {
        TrackMarkerBlockEntity trackMarkerBlock = getPrevMarker();
        if(trackMarkerBlock != null) {
            trackMarkerBlock.nextTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
        }
        prevTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
    }

    public void removeNextMarker() {
        TrackMarkerBlockEntity trackMarkerBlock = getNextMarker();
        if(trackMarkerBlock != null) {
            trackMarkerBlock.prevTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
        }
        nextTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
    }

    public @Nullable TrackMarkerBlockEntity getNextMarker() {
        assert world != null;
        return world.getBlockEntity(nextTrackMarkerPos) instanceof TrackMarkerBlockEntity t ? t : null;
    }

    public @Nullable TrackMarkerBlockEntity getPrevMarker() {
        assert world != null;
        return world.getBlockEntity(prevTrackMarkerPos) instanceof TrackMarkerBlockEntity t ? t : null;
    }

    public BlockPos getNextTrackMarkerPos() {
        return nextTrackMarkerPos;
    }

    public BlockPos getPrevTrackMarkerPos() {
        return prevTrackMarkerPos;
    }

    public boolean hasNoTrackConnected() {
        return prevTrackMarkerPos == Splinecart.OUT_OF_BOUNDS && nextTrackMarkerPos == Splinecart.OUT_OF_BOUNDS;
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

    public int computeStrength() { // TODO: code duplication computePower()
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

    public double computeLastVelocity() {
        return (lastVelocity > 0 && nextTrackMarkerPos != Splinecart.OUT_OF_BOUNDS)
                || (lastVelocity < 0 && prevTrackMarkerPos != Splinecart.OUT_OF_BOUNDS) ? lastVelocity : 0;
    }

    public void setLastVelocity(double lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        prevTrackMarkerPos = NbtHelper.toBlockPos(nbt, "prev").orElse(Splinecart.OUT_OF_BOUNDS);
        nextTrackMarkerPos = NbtHelper.toBlockPos(nbt, "next").orElse(Splinecart.OUT_OF_BOUNDS);

        nextType = TrackType.read(nbt.getInt("track_type"));
        nextStyle = TrackStyle.read(nbt.getInt("track_style"));
        nextColor = new Color(nbt.getInt("track_color"));

        power = nbt.getInt("power");
        strength = nbt.getInt("strength");
        triggers = nbt.getList("triggers", NbtElement.COMPOUND_TYPE).stream()
                        .map(e -> (NbtCompound)e)
                        .map(TrackMarkerTrigger::new)
                        .collect(Collectors.toList());

        lastVelocity = nbt.getDouble("last_velocity");

        pose.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if(prevTrackMarkerPos != Splinecart.OUT_OF_BOUNDS)
            nbt.put("prev", NbtHelper.fromBlockPos(prevTrackMarkerPos));
        if(nextTrackMarkerPos != Splinecart.OUT_OF_BOUNDS)
            nbt.put("next", NbtHelper.fromBlockPos(nextTrackMarkerPos));

        nbt.putInt("track_type", this.nextType.ordinal());
        nbt.putInt("track_style", this.nextStyle.ordinal());
        nbt.putInt("track_color", this.nextColor.getRGB());

        nbt.putInt("power", this.power);
        nbt.putInt("strength", this.strength);
        nbt.put("triggers", triggers.stream()
                .map(TrackMarkerTrigger::getNbt)
                .collect(Collectors.toCollection(NbtList::new)));

        nbt.putDouble("last_velocity", this.lastVelocity);

        pose.writeNbt(nbt);
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

    public void interpolate(TrackMarkerBlockEntity other, float t, InterpolationResult res) {
        Matrix3fc pose0 = this.pose.pose();
        Matrix3fc pose1 = other.pose.pose();
        float factor = MathHelper.sqrt((float) this.pos.getSquaredDistance(other.pos));
        var grad0 = new Vector3f(0, 0, 1).mul(pose0);
        var grad1 = new Vector3f(0, 0, 1).mul(pose1);

        cubicHermiteSpline(t, factor, new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()) , grad0, new Vector3d(other.pos.getX(), other.pos.getY(), other.pos.getZ()), grad1, res);
        var ngrad = res.gradient().normalize(new Vector3f());

        var rot0 = pose0.getNormalizedRotation(new Quaternionf());
        var rot1 = pose1.getNormalizedRotation(new Quaternionf());

        res.basis().set(rot0.nlerp(rot1, t));

        var basisGrad = new Vector3f(0, 0, 1).mul(res.basis());
        var axis = ngrad.cross(basisGrad, new Vector3f());

        if (axis.length() > 0) {
            axis.normalize();
            float angleToNewBasis = basisGrad.angleSigned(ngrad, axis);
            if (angleToNewBasis != 0) {
                new Matrix3f().identity().rotate(angleToNewBasis, axis)
                        .mul(res.basis(), res.basis()).normal();
            }
        }
    }

    public static void cubicHermiteSpline(float t, float distance, Vector3dc clientPos, Vector3fc clientVelocity, Vector3dc serverPos, Vector3fc serverVelocity, InterpolationResult res) {
        var diff = new Vector3f(new Vector3d(serverPos).sub(clientPos));

        diff.mul(6*t - 6*t*t, res.gradient());
        clientVelocity.mulAdd((3*t*t - 4*t + 1) * distance, res.gradient(),  res.gradient());
        serverVelocity.mulAdd((3*t*t - 2*t) * distance, res.gradient(), res.gradient());

        clientVelocity.mul((t*t*t - 2*t*t + t) * distance, res.translation());
        diff.mulAdd(-2*t*t*t + 3*t*t, res.translation(), res.translation());
        serverVelocity.mulAdd((t*t*t - t*t) * distance, res.translation(), res.translation());
    }
}
