package io.github.foundationgames.splinecart.block.entity;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackColor;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.util.InterpolationResult;
import io.github.foundationgames.splinecart.util.Pose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.awt.*;

import static io.github.foundationgames.splinecart.config.Config.CONFIG;

public class TrackTiesBlockEntityRenderer implements BlockEntityRenderer<TrackMarkerBlockEntity> {
    public static final int WHITE = 0xFFFFFFFF;
    public static final Identifier TRACK_TEXTURE = Splinecart.id("textures/track/track.png");
    public static final Identifier TRACK_ANIMATION_OVERLAY_TEXTURE = Splinecart.id("textures/track/track_animation_overlay.png");
    public static final Identifier TRACK_OVERLAY_TEXTURE = Splinecart.id("textures/track/track_overlay.png");
    public static final Identifier POSE_TEXTURE_DEBUG = Splinecart.id("textures/track/debug.png");

    public TrackTiesBlockEntityRenderer(BlockEntityRendererFactory.Context ignoredCtx) {
    }

    @Override
    public void render(TrackMarkerBlockEntity marker, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var pose = marker.pose();
        var pos = marker.getPos();
        var world = marker.getWorld();

        assert world != null;
        if (!(world.getBlockEntity(pos) instanceof TrackMarkerBlockEntity)) // checks if the Track Marker actually still exists
            return;
        marker.clientTime += tickDelta;
        if (marker.hasNoTrackConnected() || MinecraftClient.isHudEnabled() && (CONFIG.instance().showDebug() || MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud())) {
            renderDebugPre(matrices, vertexConsumers, pose);
        }

        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_TEXTURE));
        var nextMarker = marker.getNextMarker();
        if (nextMarker == null) {
            return;
        }
        var nextMarkerPose = nextMarker.pose();

        matrices.push();

        var o = new Vector3f(pos.getX(), pos.getY(), pos.getZ());

        TrackStyle trackStyle = marker.nextStyle;
        float u0 = trackStyle.ordinal() * TrackStyle.INVERSE_CANVAS_SIZE;
        float u1 = u0 + TrackStyle.INVERSE_CANVAS_SIZE;

        BlockPos playerPos = MinecraftClient.getInstance().cameraEntity.getBlockPos();
        double distanceToPlayerSquared = Math.min(marker.getPos().getSquaredDistance(playerPos), marker.getNextMarker().getPos().getSquaredDistance(playerPos));
        double approximateTrackLength = pose.translation().distance(nextMarkerPose.translation());
        double n = 20; // when n blocks away from marker, segment density is halved
        int segments = Math.max(1, (int) (CONFIG.instance().getTrackResolution() / 8f * (16 + 2 * approximateTrackLength) * (n / (n + Math.sqrt(distanceToPlayerSquared)))));
        InterpolationResult res0 = new InterpolationResult();
        InterpolationResult res1 = new InterpolationResult();
        float totalDist = 0;

        for (int i = 0; i < segments; i++) {
            double t0 = (double) i / segments;
            double t1 = (double) (i + 1) / segments;

            totalDist = renderPart(world, matrices.peek(), buffer, pose, nextMarkerPose, u0, u1, 0, marker.nextColor, t0, t1, totalDist, res0, res1, overlay, o);
        }

        TrackType trackType = marker.nextType;
        u0 = trackType.getTextureStart();
        u1 = trackType.getTextureEnd();
        if (trackType.hasStatic) { // renders the overlay (chain track moving, powered magnetic track
            VertexConsumer olBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_OVERLAY_TEXTURE));

            for (int i = 0; i < segments; i++) {
                double t0 = (double) i / segments;
                double t1 = (double) (i + 1) / segments;

                totalDist = renderPart(world, matrices.peek(), olBuffer, pose, nextMarkerPose, u0, u1, 0, TrackColor.WHITE.color, t0, t1, totalDist, res0, res1, overlay, o);
            }
        }
        if (trackType.hasDynamic) { // animated overlay
            VertexConsumer olBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_ANIMATION_OVERLAY_TEXTURE));

            int power = marker.computePower();
            float offset = trackType.progress.apply(power, marker.clientTime);
            Color trackColor = trackType.color.apply(power);

            for (int i = 0; i < segments; i++) {
                double t0 = (double) i / segments;
                double t1 = (double) (i + 1) / segments;

                totalDist = renderPart(world, matrices.peek(), olBuffer, pose, nextMarkerPose, u0, u1, offset, trackColor, t0, t1, totalDist, res0, res1, overlay, o);
            }
        }


        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(TrackMarkerBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return CONFIG.instance().getTrackRenderDistance() * 16;
    }


    private void renderDebugPre(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Pose start) {
        matrices.push();
        matrices.translate(0.51, 0.511, 0.512); // 0 -> .5 TODO
        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(POSE_TEXTURE_DEBUG));
        renderDebug(start, matrices.peek(), buffer);

        matrices.pop();
    }

    private void renderDebug(Pose pose, MatrixStack.Entry entry, VertexConsumer buffer) {
        var posMat = entry.getPositionMatrix();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                posMat.setRowColumn(x, y, (float) pose.basis().getRowColumn(x, y));
            }
        }

        buffer.vertex(entry, 1, 0, 1).color(WHITE).texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 0, 0, 1).color(WHITE).texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 0, 0, 0).color(WHITE).texture(1, 1)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 1, 0, 0).color(WHITE).texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
    }

    private float renderPart(World world, MatrixStack.Entry entry, VertexConsumer buffer, Pose start, Pose end,
                             float u0, float u1, float vOffset, Color color, double t0, double t1, float blockProgress,
                             InterpolationResult res0, InterpolationResult res1, int overlay, Vector3f o) {
        start.interpolate(end, t0, res0);
        Vector3d origin0 = res0.translation();
        Matrix3d basis0 = res0.basis();
        Vector3d grad0 = res0.gradient();
        var norm0 = new Vector3f(0, 1, 0).mul(basis0);
        start.interpolate(end, t1, res1);
        Vector3d origin1 = res1.translation();
        Matrix3d basis1 = res1.basis();
        Vector3d grad1 = res1.gradient();
        var norm1 = new Vector3f(0, 1, 0).mul(basis1);

        float v0 = blockProgress;
        while (v0 > 1) v0 -= 1;
        float v1 = v0 + (float) (grad0.length() * (t1 - t0));

        float newBlockProgress = v1;

        v1 = 1 - v1 + vOffset;
        v0 = 1 - v0 + vOffset;

        BlockPos pos0 = BlockPos.ofFloored(origin0.x(), origin0.y(), origin0.z());
        BlockPos pos1 = BlockPos.ofFloored(origin1.x(), origin1.y(), origin1.z());

        int light0 = WorldRenderer.getLightmapCoordinates(world, pos0);
        int light1 = WorldRenderer.getLightmapCoordinates(world, pos1);

        var point = new Vector3f();

        point.set(0.5, 0, 0).mul(basis0).add((float) origin0.x(), (float) origin0.y(), (float) origin0.z()).sub(o);
        buffer.vertex(entry, point).color(color.getRGB()).texture(u0, v0).overlay(overlay)
                .light(light0).normal(entry, norm0);
        point.set(-0.5, 0, 0).mul(basis0).add((float) origin0.x(), (float) origin0.y(), (float) origin0.z()).sub(o);
        buffer.vertex(entry, point).color(color.getRGB()).texture(u1, v0).overlay(overlay)
                .light(light0).normal(entry, norm0);

        point.set(-0.5, 0, 0).mul(basis1).add((float) origin1.x(), (float) origin1.y(), (float) origin1.z()).sub(o);
        buffer.vertex(entry, point).color(color.getRGB()).texture(u1, v1).overlay(overlay)
                .light(light1).normal(entry, norm1);
        point.set(0.5, 0, 0).mul(basis1).add((float) origin1.x(), (float) origin1.y(), (float) origin1.z()).sub(o);
        buffer.vertex(entry, point).color(color.getRGB()).texture(u0, v1).overlay(overlay)
                .light(light1).normal(entry, norm1);
        return newBlockProgress;
    }
}
