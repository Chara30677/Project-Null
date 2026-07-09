package com.projectnull;

import com.projectnull.config.NullClientConfig;
import com.projectnull.config.NullConfig;
import com.projectnull.entity.NullEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(ProjectNull.MODID)
public class ProjectNull {
    public static final String MODID = "projectnull";

    public ProjectNull(IEventBus modEventBus, ModContainer modContainer) {
        ModEntities.ENTITY_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, NullConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, NullClientConfig.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            IConfigScreenFactory configScreenFactory = (container, parent) ->
                    new com.projectnull.client.config.NullConfigScreen(parent);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactory);
        }

        modEventBus.addListener(this::registerAttributes);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NULL.get(), NullEntity.createAttributes().build());
    }
}
