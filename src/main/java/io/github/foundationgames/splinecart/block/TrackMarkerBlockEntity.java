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
import org.joml.Matrix3d;
import org.joml.Matrix3dc;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrackMarkerBlockEntity extends BlockEntity {

    public static final int ORIENTATION_RESOLUTION = 360;

    public float clientTime = 0;

    public TrackType nextType = TrackType.DEFAULT;
    public TrackStyle nextStyle = TrackStyle.DEFAULT;
    public Color nextColor = TrackColor.WHITE.color;

    private BlockPos nextTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
    private BlockPos prevTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;

    private final Matrix3d pose = new Matrix3d();

    private int power = Integer.MAX_VALUE;
    private int strength = Integer.MAX_VALUE;
    public List<TrackMarkerTrigger> triggers = new ArrayList<>();

    private double lastVelocity = 0;

    private int heading;
    private int pitching = 0;
    private int banking = 0;
    private int relativeOrientation = 0;

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 0);
    }

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state, int heading) {
        super(Splinecart.TRACK_TIES_BE, pos, state);
        setHeading(heading);
    }

    private void updatePose() {
        pose.identity();
        pose.rotateY(-heading * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        pose.rotateX(-pitching * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        pose.rotateY(-relativeOrientation * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        pose.rotateZ(-banking* MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
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
                nextPrevMarker.updatePose();
                nextPrevMarker.markDirty();
            }
            nextMarker.prevTrackMarkerPos = getPos();

            nextMarker.updatePose();
            nextMarker.markDirty();
        }

        updatePose();
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

    public Matrix3dc pose() {
        return this.pose;
    }

    /**
     * Gets called when the Track Marker Block gets broken.
     */
    public void onDestroy() {
        TrackMarkerBlockEntity prevMarker = getPrevMarker();
        if (prevMarker != null) {
            prevMarker.nextTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
            prevMarker.sync();
            prevMarker.markDirty();
        }
        TrackMarkerBlockEntity nextMarker = getNextMarker();
        if (nextMarker != null) {
            nextMarker.prevTrackMarkerPos = Splinecart.OUT_OF_BOUNDS;
            nextMarker.sync();
            nextMarker.markDirty();
        }
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

        setHeading(nbt.getInt("heading"));
        setPitching(nbt.getInt("pitching"));
        setBanking(nbt.getInt("banking"));
        setRelativeOrientation(nbt.getInt("relative_orientation"));
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

        nbt.putInt("heading", this.heading);
        nbt.putInt("pitching", this.pitching);
        nbt.putInt("banking", this.banking);
        nbt.putInt("relative_orientation", this.relativeOrientation);

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

    public void interpolate(TrackMarkerBlockEntity other, double t, InterpolationResult res) {
        Matrix3dc pose0 = this.pose();
        Matrix3dc pose1 = other.pose();
        double factor = Math.sqrt(this.pos.getSquaredDistance(other.pos));
        var grad0 = new Vector3d(0, 0, 1).mul(pose0);
        var grad1 = new Vector3d(0, 0, 1).mul(pose1);

        Pose.cubicHermiteSpline(t, factor, new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()) , grad0, new Vector3d(other.pos.getX(), other.pos.getY(), other.pos.getZ()), grad1, res);
        var ngrad = res.gradient().normalize(new Vector3d());

        var rot0 = pose0.getNormalizedRotation(new Quaterniond());
        var rot1 = pose1.getNormalizedRotation(new Quaterniond());

        var rotT = rot0.nlerp(rot1, t, new Quaterniond());
        res.basis().set(rotT);

        var basisGrad = new Vector3d(0, 0, 1).mul(res.basis());
        var axis = ngrad.cross(basisGrad, new Vector3d());

        if (axis.length() > 0) {
            axis.normalize();
            double angleToNewBasis = basisGrad.angleSigned(ngrad, axis);
            if (angleToNewBasis != 0) {
                new Matrix3d().identity().rotate(angleToNewBasis, axis)
                        .mul(res.basis(), res.basis()).normal();
            }
        }
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
        updatePose();
    }

    public int getPitching() {
        return pitching;
    }

    public void setPitching(int pitching) {
        this.pitching = pitching;
        updatePose();
    }

    public int getBanking() {
        return banking;
    }

    public void setBanking(int banking) {
        this.banking = banking;
        updatePose();
    }

    public int getRelativeOrientation() {
        return relativeOrientation;
    }

    public void setRelativeOrientation(int relativeOrientation) {
        this.relativeOrientation = relativeOrientation;
        updatePose();
    }
}
