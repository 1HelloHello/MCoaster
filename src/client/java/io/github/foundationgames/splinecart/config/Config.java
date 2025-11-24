package io.github.foundationgames.splinecart.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.foundationgames.splinecart.Splinecart;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Config {
    public static final ConfigClassHandler<Config> CONFIG;

    static {
        CONFIG = ConfigClassHandler.createBuilder(Config.class)
                .id(Splinecart.id("config"))
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                        .setPath(FabricLoader.getInstance().getConfigDir().resolve("mcoaster.json"))
                        .build())
                .build();
        CONFIG.load();
    }

    @SerialEntry
    boolean rotateCamera = true;
    @SerialEntry
    int trackResolution = 3;
    @SerialEntry
    int trackRenderDistance = 8;
    @SerialEntry
    boolean showDebug = true;
    @SerialEntry
    boolean suspendedView = false;
    @SerialEntry
    boolean showSpeedInfo = true;
    @SerialEntry
    boolean showSpeedInfoPeak = false;
    @SerialEntry
    boolean showSpeedInfoForce = false;
    @SerialEntry
    boolean showImperial = false;

    public boolean rotateCamera() {
        return rotateCamera;
    }

    public int getTrackResolution() {
        return trackResolution;
    }

    public int getTrackRenderDistance() {
        return trackRenderDistance;
    }

    public boolean showDebug() {
        return showDebug;
    }

    public boolean suspendedView() {
        return suspendedView;
    }

    public boolean showSpeedInfo() {
        return showSpeedInfo;
    }

    public boolean showSpeedInfoPeak() {
        return showSpeedInfoPeak;
    }

    public boolean showSpeedInfoForce() {
        return showSpeedInfoForce;
    }

    public boolean showImperial() {
        return showImperial;
    }

    static YetAnotherConfigLib getConfigScreen() {
        var rotate_camera = boolopt("rotate_camera", true, () -> CONFIG.instance().rotateCamera, newVal -> CONFIG.instance().rotateCamera = newVal);
        var track_resolution = slideropt("track_resolution", 3, 1, 16, () -> CONFIG.instance().trackResolution, newVal -> CONFIG.instance().trackResolution = newVal);
        var track_render_distance = slideropt("track_render_distance", 8, 4, 32, () -> CONFIG.instance().trackRenderDistance, newVal -> CONFIG.instance().trackRenderDistance = newVal);
        var show_debug = boolopt("show_debug", false, () -> CONFIG.instance().showDebug, newVal -> CONFIG.instance().showDebug = newVal);
        var suspended_view = boolopt("suspended_view", false, () -> CONFIG.instance().suspendedView, newVal -> CONFIG.instance().suspendedView = newVal);
        var show_speed_info = boolopt("show_speed_info", true, () -> CONFIG.instance().showSpeedInfo, newVal -> CONFIG.instance().showSpeedInfo = newVal);
        var show_speed_info_peak = boolopt("show_speed_info_peak", false, () -> CONFIG.instance().showSpeedInfoPeak, newVal -> CONFIG.instance().showSpeedInfoPeak = newVal);
        var show_speed_info_force = boolopt("show_speed_info_force", false, () -> CONFIG.instance().showSpeedInfoForce, newVal -> CONFIG.instance().showSpeedInfoForce = newVal);
        var show_imperial = boolopt("show_imperial", false, () -> CONFIG.instance().showImperial, newVal -> CONFIG.instance().showImperial = newVal);
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("MCoaster client Options"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .option(rotate_camera)
                        .option(track_resolution)
                        .option(track_render_distance)
                        .option(show_debug)
                        .option(suspended_view)
                        .option(show_speed_info)
                        .option(show_speed_info_peak)
                        .option(show_speed_info_force)
                        .option(show_imperial)
                        .build())
                .save(Config.CONFIG::save)
                .build();

    }

    private static Option<Boolean> boolopt(String name, boolean def, Supplier<Boolean> get, Consumer<Boolean> set) {
        var id = Splinecart.id(name).toTranslationKey();
        return Option.<Boolean>createBuilder()
                .name(Text.translatable("config." + id + ".name"))
                .description(OptionDescription.of(Text.translatable("config." + id + ".desc")))
                .binding(def, get, set)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<Integer> slideropt(String name, int def, int min, int max, Supplier<Integer> get, Consumer<Integer> set) {
        var id = Splinecart.id(name).toTranslationKey();
        return Option.<Integer>createBuilder()
                .name(Text.translatable("config." + id + ".name"))
                .description(OptionDescription.of(Text.translatable("config." + id + ".desc")))
                .binding(def, get, set)
                .controller(o -> IntegerSliderControllerBuilder.create(o).range(min, max).step(1))
                .build();
    }
}
