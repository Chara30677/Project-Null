package com.projectnull.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class NullConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_FOURTH_WALL = BUILDER
            .comment("Enable fourth-wall horror effects (fake desktop popups, wallpaper, file explorer).")
            .define("enableFourthWallEffects", true);

    public static final ModConfigSpec.BooleanValue ENABLE_NULL_SPAWNING = BUILDER
            .comment("Enable random Null entity spawning.")
            .define("enableNullSpawning", true);

    public static final ModConfigSpec.DoubleValue SPAWN_CHANCE = BUILDER
            .comment("Chance per check for Null to appear near a player (0.0 - 1.0).")
            .defineInRange("spawnChance", 0.008, 0.0, 1.0);

    public static final ModConfigSpec.BooleanValue ENABLE_CREEPY_SIGNS = BUILDER
            .comment("Place creepy signs near players when Null appears.")
            .define("enableCreepySigns", true);

    public static final ModConfigSpec.IntValue NULL_JOIN_AFTER_TICKS = BUILDER
            .comment("Ticks after world creation before Null joins (24000 = 1 in-game day).")
            .defineInRange("nullJoinAfterTicks", 24000, 0, 1000000);

    public static final ModConfigSpec.BooleanValue USE_REAL_LOCATION_DATA = BUILDER
            .comment("Use real public IP and geolocation data (city, state, country) for signs and horror effects.")
            .define("useRealLocationData", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private NullConfig() {
    }

    public static boolean fourthWallEnabled() {
        return ENABLE_FOURTH_WALL.get();
    }

    public static boolean nullSpawningEnabled() {
        return ENABLE_NULL_SPAWNING.get();
    }

    public static float spawnChance() {
        return SPAWN_CHANCE.get().floatValue();
    }

    public static boolean creepySignsEnabled() {
        return ENABLE_CREEPY_SIGNS.get();
    }

    public static int nullJoinAfterTicks() {
        return NULL_JOIN_AFTER_TICKS.get();
    }

    public static boolean useRealLocationData() {
        return USE_REAL_LOCATION_DATA.get();
    }
}
