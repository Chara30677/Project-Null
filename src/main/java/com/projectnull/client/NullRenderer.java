package com.projectnull.client;

import com.projectnull.entity.NullEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NullRenderer extends HumanoidMobRenderer<NullEntity, HumanoidModel<NullEntity>> {
    private static final ResourceLocation NULL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("projectnull", "textures/entity/null.png");

    public NullRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(NullEntity entity) {
        return NULL_TEXTURE;
    }

    @Override
    protected boolean isShaking(NullEntity entity) {
        return false;
    }

    @Override
    protected float getWhiteOverlayProgress(NullEntity entity, float partialTick) {
        return 0.0F;
    }
}
