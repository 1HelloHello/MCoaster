package io.github.foundationgames.splinecart;

import io.github.foundationgames.splinecart.block.TrackMarkerBlock;
import io.github.foundationgames.splinecart.block.TrackMarkerBlockEntity;
import io.github.foundationgames.splinecart.component.OriginComponent;
import io.github.foundationgames.splinecart.entity.TrackFollowerEntity;
import io.github.foundationgames.splinecart.item.ToolItem;
import io.github.foundationgames.splinecart.item.ToolType;
import io.github.foundationgames.splinecart.item.TrackItem;
import io.github.foundationgames.splinecart.util.SUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
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
    public static final Logger LOGGER = LoggerFactory.getLogger("splinecart");

	public static final TrackMarkerBlock TRACK_TIES = SUtil.register(Registries.BLOCK, id("track_ties"),
			(i, k) -> new TrackMarkerBlock(AbstractBlock.Settings.copy(Blocks.RAIL).registryKey(k)));
	public static final BlockEntityType<TrackMarkerBlockEntity> TRACK_TIES_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("track_ties"),
			FabricBlockEntityTypeBuilder.create(TrackMarkerBlockEntity::new, TRACK_TIES).build());

	public static final TrackItem TRACK = SUtil.register(Registries.ITEM, id("track"),
			(i, k) -> new TrackItem(TrackType.DEFAULT, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.track.desc").formatted(Formatting.GRAY))
			).registryKey(k)));
	public static final TrackItem CHAIN_DRIVE_TRACK = SUtil.register(Registries.ITEM, id("chain_drive_track"),
			(i, k) -> new TrackItem(TrackType.CHAIN_DRIVE, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.chain_drive_track.desc").formatted(Formatting.GRAY))
			).registryKey(k)));
	public static final TrackItem MAGNETIC_TRACK = SUtil.register(Registries.ITEM, id("magnetic_track"),
			(i, k) -> new TrackItem(TrackType.MAGNETIC, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.magnetic_track.desc").formatted(Formatting.GRAY))
			).registryKey(k)));

	public static final ToolItem HEADING_TOOL = SUtil.register(Registries.ITEM, id("heading_tool"),
			(i, k) -> new ToolItem(ToolType.HEADING, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.heading_tool.desc").formatted(Formatting.GRAY))
			).registryKey(k)));
	public static final ToolItem PITCHING_TOOL = SUtil.register(Registries.ITEM, id("pitching_tool"),
			(i, k) -> new ToolItem(ToolType.PITCHING, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.pitching_tool.desc").formatted(Formatting.GRAY))
			).registryKey(k)));
	public static final ToolItem BANKING_TOOL = SUtil.register(Registries.ITEM, id("banking_tool"),
			(i, k) -> new ToolItem(ToolType.BANKING, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.banking_tool.desc").formatted(Formatting.GRAY))
			).registryKey(k)));
	public static final ToolItem RELATIVE_ORIENTATION_TOOL = SUtil.register(Registries.ITEM, id("relative_orientation_tool"),
			(i, k) -> new ToolItem(ToolType.RELATIVE_ORIENTATION, new Item.Settings().component(DataComponentTypes.LORE,
					lore(Text.translatable("item.splinecart.relative_orientation_tool.desc").formatted(Formatting.GRAY))
			).registryKey(k)));


	public static final ComponentType<OriginComponent> ORIGIN_POS = Registry.register(Registries.DATA_COMPONENT_TYPE, id("origin"),
			ComponentType.<OriginComponent>builder().codec(OriginComponent.CODEC).build());

	public static final EntityType<TrackFollowerEntity> TRACK_FOLLOWER = SUtil.register(Registries.ENTITY_TYPE, id("track_follower"),
			(i, k) -> EntityType.Builder.<TrackFollowerEntity>create(TrackFollowerEntity::new, SpawnGroup.MISC).trackingTickInterval(2).dimensions(0.25f, 0.25f).build(k));

	public static final TagKey<EntityType<?>> CARTS = TagKey.of(RegistryKeys.ENTITY_TYPE, id("carts"));

	@Override
	public void onInitialize() {
		var tieItem = SUtil.register(Registries.ITEM, id("track_ties"),
				(i, k) -> new BlockItem(TRACK_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE,
								lore(Text.translatable("item.splinecart.track_ties.desc").formatted(Formatting.GRAY))
						).useBlockPrefixedTranslationKey().registryKey(k)));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
			entries.add(tieItem.getDefaultStack());
			entries.add(TRACK.getDefaultStack());
			entries.add(CHAIN_DRIVE_TRACK.getDefaultStack());
			entries.add(MAGNETIC_TRACK.getDefaultStack());
			entries.add(HEADING_TOOL.getDefaultStack());
			entries.add(PITCHING_TOOL.getDefaultStack());
			entries.add(BANKING_TOOL.getDefaultStack());
			entries.add(RELATIVE_ORIENTATION_TOOL.getDefaultStack());
		});
	}

	public static LoreComponent lore(Text lore) {
		return new LoreComponent(List.of(lore));
	}

	public static Identifier id(String path) {
		return Identifier.of("splinecart", path);
	}
}