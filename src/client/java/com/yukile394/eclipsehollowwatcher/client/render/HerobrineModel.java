package com.yukile394.eclipsehollowwatcher.client.render;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * Not a re-skinned player model. Slim, hunched torso; long thin legs;
 * disproportionately long arms that stretch further as Herobrine's mood
 * escalates (via {@link HerobrineRenderState#armLengthScale}); a head that
 * can swivel past normal biped limits for the "turns its head at wrong
 * angles" effect.
 */
public class HerobrineModel extends EntityModel<HerobrineRenderState> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public HerobrineModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Standard 64x64 Minecraft player-skin UV layout (new format, with
        // separate left arm/leg regions). Any ready-made skin PNG in this
        // format drops in with zero edits. At CALM mood these render at
        // normal vanilla-ish proportions; setAngles() below only stretches
        // arms/deepens the hunch once mood escalates.
        root.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        root.addChild("body", ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        root.addChild("right_arm", ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-5.0f, 2.0f, 0.0f));

        root.addChild("left_arm", ModelPartBuilder.create()
                        .uv(32, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(5.0f, 2.0f, 0.0f));

        root.addChild("right_leg", ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(-1.9f, 12.0f, 0.0f));

        root.addChild("left_leg", ModelPartBuilder.create()
                        .uv(16, 48).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.pivot(1.9f, 12.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(HerobrineRenderState state) {
        super.setAngles(state);

        this.head.yaw = state.relativeHeadYaw * (float) (Math.PI / 180.0);
        this.head.pitch = state.pitch * (float) (Math.PI / 180.0);
        // Occasional unnatural over-rotation is layered on top of this by
        // HerobrineRenderer using the entity's mood level.

        float swing = MathHelper.cos(state.limbFrequency * 0.6662f) * 1.1f * state.limbAmplitudeMultiplier;
        this.rightArm.pitch = swing;
        this.leftArm.pitch = -swing;
        this.rightLeg.pitch = -swing;
        this.leftLeg.pitch = swing;

        // Stretch the arms downward based on current mood. yScale on
        // ModelPart lengthens the cuboid along its local Y axis without
        // needing a second model or animation file.
        float armStretch = 1.0f + (state.armLengthScale - 1.0f) * 1.4f;
        this.rightArm.yScale = armStretch;
        this.leftArm.yScale = armStretch;

        // Slight hunch that deepens with mood.
        this.body.pitch = 0.05f + state.moodLevel * 0.015f;
        this.head.pivotY = -2.0f - state.moodLevel * 0.5f;
    }
}
