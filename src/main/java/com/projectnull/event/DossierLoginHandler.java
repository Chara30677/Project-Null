package com.projectnull.event;

import com.projectnull.horror.DossierResolver;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID)
public final class DossierLoginHandler {
    private DossierLoginHandler() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            try {
                DossierResolver.prefetch(player);
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(DossierLoginHandler.class)
                        .warn("[Project Null] Failed to prefetch dossier for {}", player.getGameProfile().getName(), e);
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DossierResolver.clear(player);
        }
    }
}
