package com.yukile394.eclipsehollowwatcher.client.render;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * White glowing eyes are the one thing meant to still read clearly even in
 * near-total darkness: the base skin texture is almost entirely black/near-
 * black, with only the eye pixels left bright, and an emissive eye layer is
 * added on top so those pixels ignore in-world lighting entirely.
 */
public class HerobrineRenderer extends LivingEntityRenderer<HerobrineEntity, HerobrineRenderState, HerobrineModel> {

    private static final Identifier TEXTURE =
            Identifier.of("eclipsehollowwatcher", "textures/entity/herobrine.png");

    public HerobrineRenderer(EntityRendererFactory.Context context) {
        super(context, new HerobrineModel(context.getPart(HerobrineModelLayers.MAIN)), 0.6f);
        this.addFeature(new GlowingEyesFeature(this));
    }

    @Override
    public Identifier getTexture(HerobrineRenderState state) {
        return TEXTURE;
    }

    @Override
    public HerobrineRenderState createRenderState() {
        return new HerobrineRenderState();
    }

    @Override
    public void updateRenderState(HerobrineEntity entity, HerobrineRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.armLengthScale = entity.getArmLengthScale();
        state.moodLevel = entity.getMood().getLevel();
        state.eyeGlowIntensity = entity.getMood().getEyeGlowIntensity();
    }
}
