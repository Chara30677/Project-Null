package com.projectnull.network;

import com.projectnull.ProjectNull;
import com.projectnull.client.horror.ClientPublicIpFetcher;
import com.projectnull.client.horror.NullHorrorHandler;
import com.projectnull.horror.DossierResolver;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ProjectNull.MODID)
public final class NullNetwork {
    private NullNetwork() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                HorrorEffectPayload.TYPE,
                HorrorEffectPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        context.enqueueWork(() -> NullHorrorHandler.handleClient(payload));
                    }
                }
        );
        registrar.playToClient(
                NullJoinedPayload.TYPE,
                NullJoinedPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        context.enqueueWork(NullHorrorHandler::handleNullJoined);
                    }
                }
        );
        registrar.playToClient(
                RequestPublicIpPayload.TYPE,
                RequestPublicIpPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) {
                        context.enqueueWork(ClientPublicIpFetcher::fetchAndRespond);
                    }
                }
        );
        registrar.playToServer(
                PublicIpResponsePayload.TYPE,
                PublicIpResponsePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof ServerPlayer player) {
                        DossierResolver.completeClientIp(player, payload.publicIp());
                    }
                })
        );
    }
}
