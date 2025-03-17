package io.github.foundationgames.splinecart;

import io.github.foundationgames.splinecart.block.TrackMarkerBlock;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.event.DyeItemUseEvent;
import io.github.foundationgames.splinecart.item.*;
import io.github.foundationgames.splinecart.item.tools.*;
import io.github.foundationgames.splinecart.track.TrackType;
import io.github.foundationgames.splinecart.util.SUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Splinecart implements ModInitializer {

	public static final String MOD_NAME = "splinecart";

    public static final Logger LOGGER = LoggerFactory.getLogger("splinecart");

	public static final TrackMarkerBlock TRACK_TIES = SUtil.register(Registries.BLOCK, "track_ties",
			(i, k) -> new TrackMarkerBlock(AbstractBlock.Settings.copy(Blocks.RAIL).registryKey(k)));
	public static final BlockEntityType<TrackMarkerBlockEntity> TRACK_TIES_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, "track_ties",
			FabricBlockEntityTypeBuilder.create(TrackMarkerBlockEntity::new, TRACK_TIES).build());

	public static final TrackItem TRACK = SUtil.register(Registries.ITEM, "track",
			(i, k) -> new TrackItem(TrackType.DEFAULT, "track", k));

	public static final ToolItem HEADING_TOOL = SUtil.register(Registries.ITEM, "heading_tool",
			(i, k) -> new OrientationToolItem(ToolType.HEADING, "heading_tool", k));
	public static final ToolItem PITCHING_TOOL = SUtil.register(Registries.ITEM, "pitching_tool",
			(i, k) -> new OrientationToolItem(ToolType.PITCHING, "pitching_tool", k));
	public static final ToolItem BANKING_TOOL = SUtil.register(Registries.ITEM, "banking_tool",
			(i, k) -> new OrientationToolItem(ToolType.BANKING, "banking_tool", k));
	public static final ToolItem RELATIVE_ORIENTATION_TOOL = SUtil.register(Registries.ITEM, "relative_orientation_tool",
			(i, k) -> new OrientationToolItem(ToolType.RELATIVE_ORIENTATION, "relative_orientation_tool", k));

	public static final ToolItem TRACK_STYLE_TOOL = SUtil.register(Registries.ITEM, "track_style_tool",
			(i, k) -> new TrackToolItem(ToolType.TRACK_STYLE, "track_style_tool", k));
	public static final ToolItem TRACK_TYPE_TOOL = SUtil.register(Registries.ITEM, "track_type_tool",
			(i, k) -> new TrackToolItem(ToolType.TRACK_TYPE, "track_type_tool", k));
	public static final PowerToolItem TRACK_POWER_TOOL_ITEM = SUtil.register(Registries.ITEM, "track_power_tool",
			(i, k) -> new PowerToolItem(ToolType.POWER, "track_power_tool", k));
	public static final PowerToolItem TRACK_STRENGTH_TOOL_ITEM = SUtil.register(Registries.ITEM, "track_strength_tool",
			(i, k) -> new PowerToolItem(ToolType.STRENGTH, "track_strength_tool", k));

	public static final CoasterCartItem COASTER_CART_ITEM = SUtil.register(Registries.ITEM, "coaster_cart",
			(i, k) -> new CoasterCartItem("coaster_cart", k));

	public static final EntityType<TrackFollowerEntity> TRACK_FOLLOWER = SUtil.register(Registries.ENTITY_TYPE, "track_follower",
			(i, k) -> EntityType.Builder.<TrackFollowerEntity>create(TrackFollowerEntity::new, SpawnGroup.MISC).trackingTickInterval(2).dimensions(0.25f, 0.25f).build(k));

	public static final TagKey<EntityType<?>> CARTS = TagKey.of(RegistryKeys.ENTITY_TYPE, id("carts"));

	@Override
	public void onInitialize() {
		var tieItem = SUtil.register(Registries.ITEM, "track_ties",
				(i, k) -> new BlockItem(TRACK_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE,
								new LoreComponent(List.of(Text.translatable("item.splinecart.track_ties.desc").formatted(Formatting.GRAY)))
						).useBlockPrefixedTranslationKey().registryKey(k)));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
			entries.add(tieItem.getDefaultStack());
			entries.add(TRACK.getDefaultStack());
			entries.add(COASTER_CART_ITEM.getDefaultStack());
			entries.add(TRACK_TYPE_TOOL.getDefaultStack());
			entries.add(HEADING_TOOL.getDefaultStack());
			entries.add(PITCHING_TOOL.getDefaultStack());
			entries.add(BANKING_TOOL.getDefaultStack());
			entries.add(RELATIVE_ORIENTATION_TOOL.getDefaultStack());
			entries.add(TRACK_STYLE_TOOL.getDefaultStack());
			entries.add(TRACK_POWER_TOOL_ITEM.getDefaultStack());
			entries.add(TRACK_STRENGTH_TOOL_ITEM.getDefaultStack());
		});
		UseBlockCallback.EVENT.register(new DyeItemUseEvent());
	}

	public static Identifier id(String id) {
		return Identifier.of(MOD_NAME, id);
	}

}