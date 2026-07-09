package com.projectnull.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class NullClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_FOURTH_WALL = BUILDER
            .comment("Show fourth-wall horror effects on this client (fake desktop, wallpaper, file explorer).")
            .define("enableFourthWallEffects", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private NullClientConfig() {
    }

    public static boolean fourthWallEnabled() {
        return ENABLE_FOURTH_WALL.get();
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
    }
}
