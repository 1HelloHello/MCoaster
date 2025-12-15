package io.github.foundationgames.splinecart.block.entity;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.track.TrackStyle;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.util.Interpolator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.*;

import java.awt.*;
import java.lang.Math;

import static io.github.foundationgames.splinecart.config.Config.CONFIG;

public class TrackTiesBlockEntityRenderer implements BlockEntityRenderer<TrackMarkerBlockEntity> {
    public static final Identifier TRACK_TEXTURE = Splinecart.id("textures/track/track.png");
    public static final Identifier TRACK_ANIMATION_OVERLAY_TEXTURE = Splinecart.id("textures/track/track_animation_overlay.png");
    public static final Identifier TRACK_OVERLAY_TEXTURE = Splinecart.id("textures/track/track_overlay.png");
    public static final Identifier POSE_TEXTURE_DEBUG = Splinecart.id("textures/track/debug.png");

    public TrackTiesBlockEntityRenderer(BlockEntityRendererFactory.Context ignoredCtx) {
    }

    @Override
    public void render(TrackMarkerBlockEntity marker, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = marker.getWorld();
        assert world != null;
        if (!(world.getBlockEntity(marker.getPos()) instanceof TrackMarkerBlockEntity)) // checks if the Track Marker actually still exists
            return;
        marker.clientTime += tickDelta;
        if (marker.hasNoTrackConnected() || MinecraftClient.isHudEnabled() && (CONFIG.instance().showTrackOverlay() || MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud())) {
            renderDebugPre(matrices, vertexConsumers, marker.pose.pose());
        }

        TrackMarkerBlockEntity nextMarker = marker.getNextMarker();
        if (nextMarker == null) {
            return;
        }
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        renderTrack(marker, nextMarker, vertexConsumers, overlay, matrices, world);
        matrices.pop();
        TrackMarkerBlockEntity prevMarker = marker.getPrevMarker();
        if(prevMarker != null && prevMarker.getNextMarker() != marker) {
            matrices.push();
            matrices.translate(prevMarker.getPos().subtract(marker.getPos()).toCenterPos());
            renderTrack(prevMarker, marker, vertexConsumers, overlay, matrices, world);
            matrices.pop();
        }
    }

    private static void renderTrack(TrackMarkerBlockEntity startMarker, TrackMarkerBlockEntity endMarker, VertexConsumerProvider vertexConsumers, int overlay, MatrixStack matrices, World world) {
        BlockPos playerPos = MinecraftClient.getInstance().cameraEntity.getBlockPos();
        int segments = getSegments(startMarker.getPos(), endMarker.getPos(), playerPos);
        TrackRenderer renderer = new TrackRenderer(world, matrices.peek(), startMarker, endMarker, segments, overlay);

        float u0 = startMarker.nextStyle.ordinal() * TrackStyle.INVERSE_CANVAS_SIZE;
        float u1 = u0 + TrackStyle.INVERSE_CANVAS_SIZE;
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_TEXTURE));
        renderer.renderTrackTexture(0, buffer, startMarker.nextColor, u0, u1);

        TrackType trackType = startMarker.nextType;
        u0 = trackType.getTextureStart();
        u1 = trackType.getTextureEnd();
        if (trackType.hasStatic) { // renders the overlay (chain track moving, powered magnetic track
            VertexConsumer olBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_OVERLAY_TEXTURE));
            renderer.renderTrackTexture(0, olBuffer, startMarker.nextColor, u0, u1);
        }
        if (trackType.hasDynamic) { // animated overlay
            VertexConsumer olBuffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TRACK_ANIMATION_OVERLAY_TEXTURE));
            int power = startMarker.computePower();
            float offset = trackType.progress.apply(power, startMarker.clientTime);
            Color trackColor = trackType.color.apply(power);
            renderer.renderTrackTexture(offset, olBuffer, trackColor, u0, u1);
        }
    }

    private static int getSegments(BlockPos start, BlockPos end, BlockPos playerPos) {
        double distanceToPlayerSquared = Math.min(start.getSquaredDistance(playerPos), end.getSquaredDistance(playerPos));
        double approximateTrackLength = Math.sqrt(start.getSquaredDistance(end));
        double n = 20; // when n blocks away from marker, segment density is halved
        return Math.max(1, (int) (CONFIG.instance().getTrackResolution() / 8f * (16 + 2 * approximateTrackLength) * (n / (n + Math.sqrt(distanceToPlayerSquared)))));
    }

    @Override
    public boolean rendersOutsideBoundingBox(TrackMarkerBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return CONFIG.instance().getTrackRenderDistance() * 16;
    }


    private void renderDebugPre(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Matrix3fc pose) {
        matrices.push();
        matrices.translate(0.51, 0.511, 0.512); // 0 -> .5 TODO
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(POSE_TEXTURE_DEBUG));
        MatrixStack.Entry entry = matrices.peek();
        entry.getPositionMatrix().set3x3(pose);
        buffer.vertex(entry, 1, 0, 1).color(Color.WHITE.getRGB()).texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 0, 0, 1).color(Color.WHITE.getRGB()).texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 0, 0, 0).color(Color.WHITE.getRGB()).texture(1, 1)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        buffer.vertex(entry, 1, 0, 0).color(Color.WHITE.getRGB()).texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0, 1, 0);
        matrices.pop();
    }

    private record TrackRenderer(World world, MatrixStack.Entry entry, Interpolator interpolator, int segments, int overlay, BlockPos pos0) {

        public TrackRenderer(World world, MatrixStack.Entry entry, TrackMarkerBlockEntity start, TrackMarkerBlockEntity end, int segments, int overlay) {
            this(world, entry, Interpolator.create(start, end), segments, overlay, start.getPos());
        }

        public void renderTrackTexture(float offset, VertexConsumer buffer, Color color, float u0, float u1) {
            float totalDist = offset;
            for (int i = 0; i < segments; i++) {
                totalDist = renderPart(buffer, u0, u1, color, (float) i / segments, (float) (i + 1) / segments, totalDist);
            }
        }

        private float renderPart(VertexConsumer buffer, float u0, float u1, Color color, float t0, float t1, float v0) {
            v0 = MathHelper.fractionalPart(v0);
            renderSide(buffer, u0, u1, color, t0, v0, true);
            float v1 = v0 + interpolator.res().gradient().length() * (t1 - t0);
            renderSide(buffer, u0, u1, color, t1, v1, false);
            return v1;
        }

        private void renderSide(VertexConsumer buffer, float u0, float u1, Color color, float t0, float v0, boolean positiveFirst) {
            interpolator.interpolate(t0);
            Vector3f origin0 = interpolator.res().translation();
            Matrix3f basis0 = interpolator.res().basis();
            Vector3f norm0 = new Vector3f(0, 1, 0).mul(basis0);

            BlockPos pos0 = BlockPos.ofFloored(origin0.x() + 0.5, origin0.y() + 0.5, origin0.z() + 0.5);
            pos0 = pos0.add(this.pos0);

            int light0 = WorldRenderer.getLightmapCoordinates(world, pos0);

            var point = new Vector3f();

            if (positiveFirst) {
                point.set(0.5, 0, 0).mul(basis0).add(origin0);
                buffer.vertex(entry, point).color(color.getRGB()).texture(u0, v0).overlay(overlay)
                        .light(light0).normal(entry, norm0);
            }
            point.set(-0.5, 0, 0).mul(basis0).add(origin0);
            buffer.vertex(entry, point).color(color.getRGB()).texture(u1, v0).overlay(overlay)
                    .light(light0).normal(entry, norm0);
            if (!positiveFirst) {
                point.set(0.5, 0, 0).mul(basis0).add(origin0);
                buffer.vertex(entry, point).color(color.getRGB()).texture(u0, v0).overlay(overlay)
                        .light(light0).normal(entry, norm0);
            }
        }
    }
}
