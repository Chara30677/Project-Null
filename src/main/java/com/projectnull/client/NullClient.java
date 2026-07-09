package com.projectnull.client;

import com.projectnull.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = com.projectnull.ProjectNull.MODID, value = Dist.CLIENT)
public final class NullClient {
    private NullClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NULL.get(), NullRenderer::new);
    }
}
