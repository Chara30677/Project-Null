package com.projectnull.horror;

import com.projectnull.config.NullConfig;
import com.projectnull.network.RequestPublicIpPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class DossierResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(DossierResolver.class);
    private static final ExecutorService RESOLVER_EXECUTOR = Executors.newFixedThreadPool(2, runnable -> {
        Thread thread = new Thread(runnable, "projectnull-dossier");
        thread.setDaemon(true);
        return thread;
    });

    private static final Map<UUID, PlayerDossier> CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, CompletableFuture<String>> PENDING_CLIENT_IP = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> IN_FLIGHT = new ConcurrentHashMap<>();

    private DossierResolver() {
    }

    public static PlayerDossier resolve(ServerPlayer player) {
        if (!NullConfig.useRealLocationData()) {
            return PlayerDossier.fallback(player);
        }

        UUID id = player.getUUID();
        PlayerDossier cached = CACHE.get(id);
        if (cached != null) {
            return cached;
        }

        prefetch(player);
        return PlayerDossier.fallback(player);
    }

    public static void prefetch(ServerPlayer player) {
        if (!NullConfig.useRealLocationData()) {
            CACHE.put(player.getUUID(), PlayerDossier.fallback(player));
            return;
        }

        UUID id = player.getUUID();
        if (CACHE.containsKey(id) || IN_FLIGHT.putIfAbsent(id, Boolean.TRUE) != null) {
            return;
        }

        RESOLVER_EXECUTOR.execute(() -> {
            try {
                resolveAsync(player);
            } finally {
                IN_FLIGHT.remove(id);
            }
        });
    }

    public static void completeClientIp(ServerPlayer player, String publicIp) {
        if (publicIp == null || publicIp.isBlank()) {
            return;
        }

        String trimmed = publicIp.trim();
        CompletableFuture<String> pending = PENDING_CLIENT_IP.remove(player.getUUID());
        if (pending != null) {
            pending.complete(trimmed);
        }

        storeDossierAsync(player.getUUID(), player.getGameProfile().getName(), trimmed);
    }

    public static void clear(ServerPlayer player) {
        UUID id = player.getUUID();
        CACHE.remove(id);
        IN_FLIGHT.remove(id);
        CompletableFuture<String> pending = PENDING_CLIENT_IP.remove(id);
        if (pending != null) {
            pending.cancel(true);
        }
    }

    private static void resolveAsync(ServerPlayer player) {
        UUID id = player.getUUID();
        MinecraftServer server = player.getServer();
        if (server == null || !player.isAlive()) {
            CACHE.putIfAbsent(id, PlayerDossier.fallback(player));
            return;
        }

        try {
            String connectionIp = extractConnectionIp(player);
            if (!IpAddressUtil.requiresClientLookup(connectionIp)) {
                storeDossierAsync(id, player.getGameProfile().getName(), connectionIp);
                return;
            }

            CompletableFuture<String> future = new CompletableFuture<>();
            PENDING_CLIENT_IP.put(id, future);
            server.execute(() -> {
                if (player.isAlive()) {
                    PacketDistributor.sendToPlayer(player, new RequestPublicIpPayload());
                } else {
                    future.completeExceptionally(new IllegalStateException("Player disconnected"));
                }
            });

            String ip = future.orTimeout(8, TimeUnit.SECONDS).get();
            storeDossierAsync(id, player.getGameProfile().getName(), ip);
        } catch (Exception e) {
            LOGGER.warn("[Project Null] Failed to resolve dossier for {}", player.getGameProfile().getName(), e);
            CACHE.putIfAbsent(id, PlayerDossier.fallback(player));
        } finally {
            PENDING_CLIENT_IP.remove(id);
        }
    }

    private static void storeDossierAsync(UUID playerId, String playerName, String ip) {
        RESOLVER_EXECUTOR.execute(() -> {
            try {
                PlayerDossier dossier = GeoIpService.lookup(ip);
                CACHE.put(playerId, dossier);
                LOGGER.info("[Project Null] Resolved dossier for {} -> {} ({})",
                        playerName, dossier.publicIp(), dossier.locationLine());
            } catch (Exception e) {
                LOGGER.warn("[Project Null] Failed to store dossier for {}", playerName, e);
            }
        });
    }

    private static String extractConnectionIp(ServerPlayer player) {
        SocketAddress remote = player.connection.getRemoteAddress();
        if (remote instanceof InetSocketAddress address && address.getAddress() != null) {
            return address.getAddress().getHostAddress();
        }
        return "0.0.0.0";
    }
}
