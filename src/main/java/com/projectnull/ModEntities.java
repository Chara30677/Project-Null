package com.projectnull;

import com.projectnull.entity.NullEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ProjectNull.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<NullEntity>> NULL = ENTITY_TYPES.register(
            "null",
            () -> EntityType.Builder.of(NullEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .fireImmune()
                    .build("null")
    );

    private ModEntities() {
    }
}
