package com.yukile394.eclipsehollowwatcher.client.render;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class HerobrineRenderer extends LivingEntityRenderer<HerobrineEntity, HerobrineModel> {

    private static final Identifier TEXTURE =
            Identifier.of("eclipsehollowwatcher", "textures/entity/herobrine.png");

    public HerobrineRenderer(EntityRendererFactory.Context context) {
        super(context, new HerobrineModel(context.getPart(HerobrineModelLayers.MAIN)), 0.6f);
        this.addFeature(new GlowingEyesFeature(this));
    }

    @Override
    public Identifier getTexture(HerobrineEntity entity) {
        return TEXTURE;
    }
}
