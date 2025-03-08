package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.Splinecart;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public abstract class RegistrableItem extends Item {

    public RegistrableItem(String identifier, RegistryKey<Item> registryKey) {
        super(new Item.Settings().component(DataComponentTypes.LORE,
                new LoreComponent(List.of((Text.translatable("item." + Splinecart.MOD_NAME + "." + identifier + ".desc").formatted(Formatting.GRAY))))).registryKey(registryKey));
    }

}
