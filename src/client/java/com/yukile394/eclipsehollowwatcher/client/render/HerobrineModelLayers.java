package com.yukile394.eclipsehollowwatcher.client.render;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class HerobrineModelLayers {

    public static final EntityModelLayer MAIN =
            new EntityModelLayer(Identifier.of("eclipsehollowwatcher", "herobrine"), "main");

    private HerobrineModelLayers() {
    }
}
