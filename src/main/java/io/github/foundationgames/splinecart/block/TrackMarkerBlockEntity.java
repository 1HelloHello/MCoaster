package io.github.foundationgames.splinecart.block;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.TrackType;
import io.github.foundationgames.splinecart.item.ToolType;
import io.github.foundationgames.splinecart.item.TrackItem;
import io.github.foundationgames.splinecart.util.Pose;
import io.github.foundationgames.splinecart.util.SUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class TrackMarkerBlockEntity extends BlockEntity {

    public static final int ORIENTATION_RESOLUTION = 360;
    public static final boolean DROP_TRACK = false;

    public float clientTime = 0;

    private TrackType nextType = TrackType.DEFAULT;
    private TrackType prevType = TrackType.DEFAULT;

    private @Nullable TrackMarkerBlockEntity nextTrackMarker;
    private @Nullable TrackMarkerBlockEntity prevTrackMarker;

    private Pose pose;

    private int power = -1;

    private int heading = 0;
    private int pitching = 0;
    private int banking = 0;
    private int relative_orientation = 0;

    public TrackMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(Splinecart.TRACK_TIES_BE, pos, state);
        updatePose(pos);
    }

    public void updatePose(BlockPos blockPos) {
        Vector3d pos = new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        Matrix3d basis = new Matrix3d();

        basis.rotateY(heading * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateX(pitching * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateY(relative_orientation * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));
        basis.rotateZ(banking* MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION));

        this.pose = new Pose(pos, basis);
    }

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

    private void dropTrack(TrackType type) {
        if(!DROP_TRACK) {
            return;
        }
        var world = getWorld();
        var pos = Vec3d.ofCenter(getPos());
        var item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TrackItem.ITEMS_BY_TYPE.get(type)));

        world.spawnEntity(item);
    }

    /**
     * Gets called when this Track Marker gets connected to another Track Marker.
     * @param pos The location of the Track Marker, this is connected with (the next one)
     * @param type The Track Type this is connected with.
     */
    public void setNext(@Nullable BlockPos pos, @Nullable TrackType type) {
        if (pos == null) { // not sure how that would happen
            var oldNextE = nextMarker();
            this.nextTrackMarker = null;
            if (oldNextE != null) {
                oldNextE.prevTrackMarker = null;
                oldNextE.sync();
                oldNextE.markDirty();
            }
        } else {
            nextTrackMarker = (TrackMarkerBlockEntity) world.getBlockEntity(pos);
            if (type != null) {
                this.nextType = type;
            }
            var nextE = nextMarker();
            if (nextE != null) {
                nextE.prevTrackMarker = this;
                if (type != null) {
                    nextE.prevType = type;
                }
                nextE.sync();
                nextE.markDirty();
            }
        }

        sync();
        markDirty();
    }

    public @Nullable TrackMarkerBlockEntity nextMarker() {
        return nextTrackMarker;
    }

    public @Nullable TrackMarkerBlockEntity prevMarker() {
        return prevTrackMarker;
    }

    public @Nullable BlockPos nextMarkerPos() {
        if(nextTrackMarker == null)
            return null;
        return nextTrackMarker.getPos();
    }

    public @Nullable BlockPos prevMarkerPos() {
        if(prevTrackMarker == null)
            return null;
        return prevTrackMarker.getPos();
    }

    public @Nullable TrackType nextType() {
        return this.nextType;
    }

    public Pose pose() {
        updatePose(super.getPos());
        return this.pose;
    }

    public void updatePower() {
        int oldPower = this.power;
        this.power = getWorld().getReceivedRedstonePower(getPos());

        if (oldPower != this.power) {
            sync();
            markDirty();
        }
    }

    public int power() {
        if (this.power < 0) {
            updatePower();
        }

        return this.power;
    }

    /**
     * Gets called when the Track Marker Block gets broken.
     */
    public void onDestroy() {
        if (prevTrackMarker != null) {
            this.dropTrack(this.prevType);
        }
        if (prevTrackMarker != null) {
            this.dropTrack(this.nextType);
        }

        var prevE = prevMarker();
        if (prevE != null) {
            prevE.nextTrackMarker = null;
            prevE.sync();
            prevE.markDirty();
        }
        var nextE = nextMarker();
        if (nextE != null) {
            nextE.prevTrackMarker = null;
            nextE.sync();
            nextE.markDirty();
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        assert world != null;
        BlockPos prevMarkerPos = SUtil.getBlockPos(nbt, "prev");
        if(prevMarkerPos == null)
            prevTrackMarker = null;
        else
            prevTrackMarker = (TrackMarkerBlockEntity) world.getBlockEntity(prevMarkerPos);

        BlockPos nextMarkerPos = SUtil.getBlockPos(nbt, "next");
        if(nextMarkerPos == null)
            nextTrackMarker = null;
        else
            nextTrackMarker = (TrackMarkerBlockEntity) world.getBlockEntity(nextMarkerPos);

        this.nextType = TrackType.read(nbt.getInt("track_type"));

        this.power = nbt.getInt("power");

        this.heading = nbt.getInt("heading");
        this.pitching = nbt.getInt("pitching");
        this.banking = nbt.getInt("banking");
        this.relative_orientation = nbt.getInt("relative_orientation");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        SUtil.putBlockPos(nbt, prevMarkerPos(), "prev");
        SUtil.putBlockPos(nbt, nextMarkerPos(), "next");

        nbt.putInt("track_type", this.nextType.write());

        nbt.putInt("power", this.power);

        nbt.putInt("heading", this.heading);
        nbt.putInt("pitching", this.pitching);
        nbt.putInt("banking", this.banking);
        nbt.putInt("relative_orientation", this.relative_orientation);
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
        getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), 3);
    }

    public int getValueForTool(ToolType toolType) {
        return switch(toolType) {
            case HEADING -> heading;
            case PITCHING -> pitching;
            case BANKING -> banking;
            case RELATIVE_ORIENTATION -> relative_orientation;
        };
    }

    /**
     * Need to update after setting the values
     * @param toolType
     * @param value
     */
    public void setValueForTool(ToolType toolType, int value) {
        switch (toolType) {
            case HEADING -> heading = value;
            case PITCHING -> pitching = value;
            case BANKING -> banking = value;
            case RELATIVE_ORIENTATION -> relative_orientation = value;
        }
    }

}
