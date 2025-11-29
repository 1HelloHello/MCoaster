package io.github.foundationgames.splinecart.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public class Pose {
    public static final int ORIENTATION_RESOLUTION = 360;
    private final Matrix3f pose = new Matrix3f();
    private int heading = 0;
    private int pitching = 0;
    private int banking = 0;
    private int relativeOrientation = 0;

    public Matrix3fc pose() {
        return this.pose;
    }

    private void updatePose() {
        pose.identity();
        pose.rotateY((float) (-heading * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION)));
        pose.rotateX((float) (-pitching * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION)));
        pose.rotateY((float) (-relativeOrientation * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION)));
        pose.rotateZ((float) (-banking * MathHelper.PI * ((double) 2 / ORIENTATION_RESOLUTION)));
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

    public void readNbt(NbtCompound nbt) {
        heading = nbt.getInt("heading");
        pitching = nbt.getInt("pitching");
        banking = nbt.getInt("banking");
        relativeOrientation = nbt.getInt("relative_orientation");
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("heading", heading);
        nbt.putInt("pitching", pitching);
        nbt.putInt("banking", banking);
        nbt.putInt("relative_orientation", relativeOrientation);
    }
}
