package com.projectnull.event;

import com.mojang.authlib.GameProfile;
import com.projectnull.ProjectNull;
import com.projectnull.config.NullConfig;
import com.projectnull.horror.NullHorrorEffects;
import com.projectnull.horror.NullPresence;
import com.projectnull.network.HorrorEffectPayload;
import com.projectnull.network.NullJoinedPayload;
import com.projectnull.world.NullWorldData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.List;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID)
public final class NullJoinHandler {
    private static final String NULL_TEAM = ProjectNull.MODID + "_null";
    private static final GameProfile NULL_PROFILE = new GameProfile(NullPresence.NULL_UUID, NullPresence.NULL_NAME);
    private static int tickCounter;

    private NullJoinHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        tickCounter++;
        if (tickCounter < 20) {
            return;
        }
        tickCounter = 0;

        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.overworld();
        NullWorldData data = NullWorldData.get(overworld);

        if (data.hasNullJoined()) {
            return;
        }

        if (overworld.getGameTime() < NullConfig.nullJoinAfterTicks()) {
            return;
        }

        data.setNullJoined();
        addNullToTabList(server);
        broadcastNullJoin(server);
        PacketDistributor.sendToAllPlayers(new NullJoinedPayload());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            NullHorrorEffects.triggerEffect(player, HorrorEffectPayload.HorrorEffectType.GLITCH);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel overworld = player.server.overworld();
        if (!NullWorldData.get(overworld).hasNullJoined()) {
            return;
        }

        syncNullTabListTo(player);
    }

    private static void addNullToTabList(MinecraftServer server) {
        ensureNullTeam(server);
        ClientboundPlayerInfoUpdatePacket tabPacket = createTabListPacket(server);
        ClientboundSetPlayerTeamPacket teamPacket = createTeamPacket(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(teamPacket);
            player.connection.send(tabPacket);
        }
    }

    private static void syncNullTabListTo(ServerPlayer player) {
        ensureNullTeam(player.server);
        player.connection.send(createTeamPacket(player.server));
        player.connection.send(createTabListPacket(player.server));
    }

    private static void ensureNullTeam(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(NULL_TEAM);
        if (team == null) {
            team = scoreboard.addPlayerTeam(NULL_TEAM);
            team.setColor(net.minecraft.ChatFormatting.DARK_GRAY);
            team.setDisplayName(NullPresence.teamDisplayName());
        }

        if (!team.getPlayers().contains(NullPresence.NULL_NAME)) {
            scoreboard.addPlayerToTeam(NullPresence.NULL_NAME, team);
        }
    }

    private static ClientboundSetPlayerTeamPacket createTeamPacket(MinecraftServer server) {
        PlayerTeam team = server.getScoreboard().getPlayerTeam(NULL_TEAM);
        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
    }

    private static ClientboundPlayerInfoUpdatePacket createTabListPacket(MinecraftServer server) {
        ServerPlayer nullPlayer = FakePlayerFactory.get(server.overworld(), NULL_PROFILE);
        nullPlayer.setCustomName(NullPresence.tabListDisplayName());
        return new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(
                        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME
                ),
                List.of(nullPlayer)
        );
    }

    private static void broadcastNullJoin(MinecraftServer server) {
        Component message = Component.translatable("multiplayer.player.joined", NullPresence.joinMessageName());
        server.getPlayerList().broadcastSystemMessage(message, false);
    }

    public static void forceJoin(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        NullWorldData data = NullWorldData.get(overworld);
        if (data.hasNullJoined()) {
            return;
        }
        data.setNullJoined();
        addNullToTabList(server);
        broadcastNullJoin(server);
        PacketDistributor.sendToAllPlayers(new NullJoinedPayload());
    }
}
