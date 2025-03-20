package io.github.foundationgames.splinecart.item;

import io.github.foundationgames.splinecart.Splinecart;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public abstract class RegistrableItem extends Item {

    public final String identifier;

    public RegistrableItem(String identifier, RegistryKey<Item> registryKey) {
        super(new Item.Settings().component(DataComponentTypes.LORE,
                new LoreComponent(loadDescription("item." + Splinecart.MOD_NAME + "." + identifier))).registryKey(registryKey));
        this.identifier = identifier;
    }

    /**
     *
     * @param path the path in the lang file (e.g. item.splinecart.test_item), without .desc in the end.
     * @return
     */
    public static List<Text> loadDescription(String path) {
        List<Text> text = new ArrayList<>();
        text.add(Text.translatable(path + ".desc").formatted(Formatting.GRAY));
        text.add(Text.translatable(path + ".desc1").formatted(Formatting.GRAY));
        return text;
    }

}
