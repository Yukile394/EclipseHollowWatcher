package com.yukile394.eclipsehollowwatcher.client.render;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GlowingEyesFeature extends FeatureRenderer<HerobrineRenderState, HerobrineModel> {

    private static final Identifier EYES_TEXTURE =
            Identifier.of("eclipsehollowwatcher", "textures/entity/herobrine_eyes.png");

    public GlowingEyesFeature(FeatureRendererContext<HerobrineRenderState, HerobrineModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                        HerobrineRenderState state, float limbAngle, float limbDistance) {
        // getEyes() renders fully bright regardless of world light level and
        // additively blends onto the base layer - the same technique vanilla
        // uses for Enderman eyes - which is exactly the "only the eyes are
        // visible in total darkness" look this mod wants.
        this.getContextModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(EYES_TEXTURE)),
                light, OverlayTexture.DEFAULT_UV);
    }
}
