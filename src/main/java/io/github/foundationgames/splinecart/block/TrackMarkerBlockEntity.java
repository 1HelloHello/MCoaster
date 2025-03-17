package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.item.CoasterCartItem;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackColorPreset;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.item.tools.ToolType;
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

    private TrackType nextType = TrackType.DEFAULT;
    private TrackStyle nextStyle = TrackStyle.DEFAULT;
    private TrackColor nextColor = TrackColorPreset.WHITE.get();

    private @Nullable BlockPos nextTrackMarkerPos = null;
    private @Nullable BlockPos prevTrackMarkerPos = null;

    private Pose pose;

    private int power = Integer.MAX_VALUE;
    private int strength = Integer.MAX_VALUE;
    public TrackMarkerTriggers triggers = TrackMarkerTriggers.EMPTY;

    private double lastVelocity = CoasterCartItem.INITIAL_VELOCITY;

    private int heading = Integer.MAX_VALUE;
    private int pitching = 0;
    private int banking = 0;
    private int relative_orientation = 0;

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(Splinecart.TRACK_TIES_BE, pos, state);
        updatePose(pos);
    }

    public void updatePose(BlockPos blockPos) {
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

        updatePose(this.getPos());
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
     * @param type The Track Type this is connected with.
     */
    public void setNext(@Nullable BlockPos pos, @Nullable TrackType type) {
        if (pos == null) { // not sure how that would happen
            var oldNextE = getNextMarker();
            this.nextTrackMarkerPos = null;
            if (oldNextE != null) {
                oldNextE.prevTrackMarkerPos = null;
                oldNextE.sync();
                oldNextE.markDirty();
            }
        } else {
            nextTrackMarkerPos = pos;
            if (type != null) {
                this.nextType = type;
            }
            TrackMarkerBlockEntity prevMarker = getPrevMarker();
            if(prevMarker != null) {
                nextColor = prevMarker.getNextColor();
                nextStyle = prevMarker.getNextStyle();
                nextType = prevMarker.getNextType();
            }
            var nextE = getNextMarker();
            if (nextE != null) {
                nextE.prevTrackMarkerPos = getPos();

                nextE.sync();
                nextE.markDirty();
            }
        }

        sync();
        markDirty();
    }

    public @Nullable TrackMarkerBlockEntity getNextMarker() {
        assert world != null;
        return nextTrackMarkerPos == null ? null : (TrackMarkerBlockEntity) world.getBlockEntity(nextTrackMarkerPos);
    }

    public @Nullable TrackMarkerBlockEntity getPrevMarker() {
        assert world != null;
        return prevTrackMarkerPos == null ? null : (TrackMarkerBlockEntity) world.getBlockEntity(prevTrackMarkerPos);
    }

    public @Nullable BlockPos nextMarkerPos() {
        return nextTrackMarkerPos;
    }

    public @Nullable BlockPos prevMarkerPos() {
        return prevTrackMarkerPos;
    }

    public boolean hasTrackConnected() {
        return prevTrackMarkerPos != null || nextTrackMarkerPos != null;
    }

    public TrackType getNextType() {
        return this.nextType;
    }

    public TrackStyle getNextStyle() {
        return this.nextStyle;
    }

    public TrackColor getNextColor() {
        return this.nextColor;
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
        return 10;
    }

    public double getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(double lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public void setNextColor(TrackColor color) {
        nextColor = color;
    }

    public Pose pose() {
        updatePose(super.getPos());
        return this.pose;
    }

    /**
     * Gets called when the Track Marker Block gets broken.
     */
    public void onDestroy() {
        var prevE = getPrevMarker();
        if (prevE != null) {
            prevE.nextTrackMarkerPos = null;
            prevE.sync();
            prevE.markDirty();
        }
        var nextE = getNextMarker();
        if (nextE != null) {
            nextE.prevTrackMarkerPos = null;
            nextE.sync();
            nextE.markDirty();
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

        SUtil.putBlockPos(nbt, prevMarkerPos(), "prev");
        SUtil.putBlockPos(nbt, nextMarkerPos(), "next");

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

    public int getValueForTool(ToolType toolType) {
        return switch(toolType) {
            case HEADING -> heading;
            case PITCHING -> pitching;
            case BANKING -> banking;
            case RELATIVE_ORIENTATION -> relative_orientation;
            case TRACK_STYLE -> nextStyle.ordinal();
            case TRACK_TYPE -> nextType.ordinal();
            case POWER -> power;
            case STRENGTH -> strength;
        };
    }

    /**
     * Need to update after setting the values
     */
    public void setValueForTool(ToolType toolType, int value) {
        switch (toolType) {
            case HEADING -> heading = value;
            case PITCHING -> pitching = value;
            case BANKING -> banking = value;
            case RELATIVE_ORIENTATION -> relative_orientation = value;
            case TRACK_STYLE -> nextStyle = TrackStyle.values()[value];
            case TRACK_TYPE -> nextType = TrackType.values()[value];
            case POWER -> power = value;
            case STRENGTH -> strength = value;
        }
    }

}
