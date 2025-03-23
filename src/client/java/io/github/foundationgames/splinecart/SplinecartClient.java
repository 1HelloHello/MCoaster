package io.github.foundationgames.splinecart;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.foundationgames.splinecart.block.entity.TrackTiesBlockEntityRenderer;
import io.github.foundationgames.splinecart.config.Config;
import io.github.foundationgames.splinecart.config.ConfigOption;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

import java.io.IOException;

public class SplinecartClient implements ClientModInitializer {
	public static final Config CONFIG = new Config("mcoaster_client",
			() -> FabricLoader.getInstance().getConfigDir()
					.resolve("mcoaster").resolve("mcoaster_client.properties"));

	public static final ConfigOption.BooleanOption CFG_ROTATE_CAMERA = CONFIG.optBool("rotate_camera", true);
	public static final ConfigOption.IntOption CFG_TRACK_RESOLUTION = CONFIG.optInt("track_resolution", 3, 1, 16);
	public static final ConfigOption.IntOption CFG_TRACK_RENDER_DISTANCE = CONFIG.optInt("track_render_distance", 8, 4, 32);
	public static final ConfigOption.BooleanOption CFG_SHOW_DEBUG = CONFIG.optBool("show_debug", true);
	public static final ConfigOption.BooleanOption CFG_SUSPENDED_VIEW = CONFIG.optBool("suspended_view", false);
	public static final ConfigOption.BooleanOption CFG_SHOW_SPEED_INFO = CONFIG.optBool("show_speed_info", true);

	@Override
	public void onInitializeClient() {
		try {
			CONFIG.load();
		} catch (IOException e) {
			Splinecart.LOGGER.error("Error loading client config on mod init", e);
		}

		BlockRenderLayerMap.INSTANCE.putBlock(Splinecart.TRACK_TIES, RenderLayer.getCutout());

		BlockEntityRendererFactories.register(Splinecart.TRACK_TIES_BE, TrackTiesBlockEntityRenderer::new);
		EntityRendererRegistry.register(Splinecart.TRACK_FOLLOWER, EmptyEntityRenderer::new);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(
					LiteralArgumentBuilder.<FabricClientCommandSource>literal("mcoaster")
							.then(CONFIG.command(LiteralArgumentBuilder.literal("config"),
									FabricClientCommandSource::sendFeedback))
		));
	}
}