package io.github.foundationgames.splinecart.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class RegistrableItem extends Item {

    public final Identifier identifier;

    public RegistrableItem(RegistryKey<Item> registryKey, int maxStackSize) {
        super(new Item.Settings()
                .component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable(
                        registryKey.getValue().toTranslationKey("item", "desc"),
                        registryKey.getValue().toTranslationKey("item", "desc1")))))
                .registryKey(registryKey)
                .component(DataComponentTypes.MAX_STACK_SIZE, maxStackSize));
        this.identifier = registryKey.getValue();
    }
}
