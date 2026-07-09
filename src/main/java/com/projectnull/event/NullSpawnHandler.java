package com.projectnull.event;

import com.projectnull.config.NullConfig;
import com.projectnull.entity.NullEntity;
import com.projectnull.horror.NullHorrorEffects;
import com.projectnull.horror.SignPlacer;
import com.projectnull.world.NullWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID)
public final class NullSpawnHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NullSpawnHandler.class);
    private static final int TICK_INTERVAL = 100;
    private static final int MIN_DARK_LIGHT = 7;
    private static final int COOLDOWN_TICKS = 6000;

    private static int tickCounter;
    private static final Map<UUID, Integer> playerCooldowns = new HashMap<>();

    private NullSpawnHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!NullConfig.nullSpawningEnabled()) {
            return;
        }

        try {
            handleSpawnTick(event.getServer().overworld());
        } catch (Exception e) {
            LOGGER.error("[Project Null] Spawn handler failed", e);
        }
    }

    private static void handleSpawnTick(ServerLevel level) {
        tickCounter++;
        if (tickCounter < TICK_INTERVAL) {
            return;
        }
        tickCounter = 0;

        if (!NullWorldData.get(level).hasNullJoined()) {
            return;
        }

        if (!level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.isCreative() || player.isSpectator()) {
                continue;
            }

            ServerLevel playerLevel = player.serverLevel();
            UUID playerId = player.getUUID();
            int cooldown = playerCooldowns.getOrDefault(playerId, 0);
            if (cooldown > 0) {
                playerCooldowns.put(playerId, cooldown - TICK_INTERVAL);
                continue;
            }

            if (hasActiveNull(playerLevel, playerId)) {
                continue;
            }

            if (playerLevel.random.nextFloat() > NullConfig.spawnChance()) {
                continue;
            }

            if (!isDarkEnough(playerLevel, player.blockPosition())) {
                continue;
            }

            BlockPos spawnPos = findSpawnPosition(playerLevel, player);
            if (spawnPos == null) {
                continue;
            }

            NullEntity nullEntity = NullEntity.createForPlayer(playerLevel, player, spawnPos);
            if (nullEntity == null) {
                continue;
            }

            playerLevel.addFreshEntity(nullEntity);

            if (NullConfig.creepySignsEnabled()) {
                SignPlacer.placeCreepySign(playerLevel, player, playerLevel.random);
                if (playerLevel.random.nextFloat() < 0.35F) {
                    SignPlacer.placeCreepySign(playerLevel, player, playerLevel.random);
                }
            }

            NullHorrorEffects.triggerRandomEffect(player);
            playerCooldowns.put(playerId, COOLDOWN_TICKS);
        }
    }

    private static boolean hasActiveNull(ServerLevel level, UUID playerId) {
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof NullEntity nullEntity
                    && nullEntity.getTargetPlayerId().map(playerId::equals).orElse(false)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDarkEnough(ServerLevel level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return Math.max(blockLight, skyLight) <= MIN_DARK_LIGHT || level.isNight();
    }

    private static BlockPos findSpawnPosition(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 16; attempt++) {
            int distance = 32 + level.random.nextInt(48);
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            int x = player.getBlockX() + (int) (Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int) (Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos pos = new BlockPos(x, y, z);
            BlockPos below = pos.below();
            if (!level.getBlockState(below).isAir()
                    && level.getBlockState(pos).isAir()
                    && level.getBlockState(pos.above()).isAir()) {
                return pos;
            }
        }
        return null;
    }
}
