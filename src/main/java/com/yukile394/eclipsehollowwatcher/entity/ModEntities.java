package com.yukile394.eclipsehollowwatcher.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModEntities {

    private static final Identifier HEROBRINE_ID = Identifier.of("eclipsehollowwatcher", "herobrine");

    public static final EntityType<HerobrineEntity> HEROBRINE = Registry.register(
            Registries.ENTITY_TYPE,
            HEROBRINE_ID,
            EntityType.Builder.create(HerobrineEntity::new, SpawnGroup.MISC)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                    .maxTrackingRange(96)
                    .trackingTickInterval(1)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, HEROBRINE_ID))
    );

    private ModEntities() {
    }

    public static void init() {
        // Triggers static registration above.
    }
}
