package io.github.foundationgames.splinecart.util;

import io.github.foundationgames.splinecart.Splinecart;
import io.github.foundationgames.splinecart.item.tools.ToolType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiFunction;

public enum SUtil {;
    public static final Vector3f[] REDSTONE_COLOR_LUT = Util.make(new Vector3f[16], colors -> {
        for (int i = 0; i <= 15; i++) {
            float strength = (float)i / 15.0F;
            colors[i] = new Vector3f(
                    strength * 0.6f + (strength > 0.0f ? 0.4f : 0.3f),
                    MathHelper.clamp((strength * strength * 0.7f) - 0.5f, 0, 1),
                    MathHelper.clamp((strength * strength * 0.6f) - 0.7f, 0, 1)
            );
        }
    });

    public static void putBlockPos(NbtCompound nbt, @Nullable BlockPos pos, String key) {
        if (pos == null) {
            nbt.putIntArray(key, new int[0]);
        } else nbt.putIntArray(key, new int[] {pos.getX(), pos.getY(), pos.getZ()});
    }

    public static BlockPos getBlockPos(NbtCompound nbt, String key) {
        var arr = nbt.getIntArray(key);
        if (arr.length < 3) return null;

        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    public static <V, T extends V> T register(Registry<V> registry, String id, BiFunction<Identifier, RegistryKey<V>, T> obj) {
        return register(registry, Identifier.of(Splinecart.MOD_NAME, id), obj);
    }

    public static <V, T extends V> T register(Registry<V> registry, Identifier id, BiFunction<Identifier, RegistryKey<V>, T> obj) {
        RegistryKey<V> key = RegistryKey.of(registry.getKey(), id);
        return Registry.register(registry, id, obj.apply(id, key));
    }

    public static Vec3d toCenteredVec3d(Vec3i vec) {
        return new Vec3d(vec.getX() + .5, vec.getY() + .5, vec.getZ() + .5);
    }

}
