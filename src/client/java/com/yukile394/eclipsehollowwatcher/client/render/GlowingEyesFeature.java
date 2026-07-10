package com.yukile394.eclipsehollowwatcher.client.render;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GlowingEyesFeature extends FeatureRenderer<HerobrineEntity, HerobrineModel> {

    private static final Identifier EYES_TEXTURE =
            Identifier.of("eclipsehollowwatcher", "textures/entity/herobrine_eyes.png");

    public GlowingEyesFeature(FeatureRendererContext<HerobrineEntity, HerobrineModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                        HerobrineEntity entity, float limbAngle, float limbDistance, float tickDelta,
                        float animationProgress, float headYaw, float headPitch) {
        this.getContextModel().render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(EYES_TEXTURE)),
                light, OverlayTexture.DEFAULT_UV);
    }
}
