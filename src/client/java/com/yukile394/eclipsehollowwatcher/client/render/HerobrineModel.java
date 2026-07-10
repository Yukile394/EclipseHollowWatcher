package com.yukile394.eclipsehollowwatcher.client.render;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

public class HerobrineModel extends SinglePartEntityModel<HerobrineEntity> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public HerobrineModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("head", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        root.addChild("body", ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-4.0f, 0.0f, -2.5f, 8.0f, 20.0f, 5.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        root.addChild("right_arm", ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 24.0f, 3.0f),
                ModelTransform.pivot(-5.5f, 1.0f, 0.0f));

        root.addChild("left_arm", ModelPartBuilder.create()
                        .uv(40, 44).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 24.0f, 3.0f),
                ModelTransform.pivot(5.5f, 1.0f, 0.0f));

        root.addChild("right_leg", ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 20.0f, 4.0f),
                ModelTransform.pivot(-2.0f, 20.0f, 0.0f));

        root.addChild("left_leg", ModelPartBuilder.create()
                        .uv(0, 44).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 20.0f, 4.0f),
                ModelTransform.pivot(2.0f, 20.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(HerobrineEntity entity, float limbAngle, float limbDistance, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yaw = netHeadYaw * (float) (Math.PI / 180.0);
        this.head.pitch = headPitch * (float) (Math.PI / 180.0);

        float swing = MathHelper.cos(limbAngle * 0.6662f) * 1.1f * limbDistance;
        this.rightArm.pitch = swing;
        this.leftArm.pitch = -swing;
        this.rightLeg.pitch = -swing;
        this.leftLeg.pitch = swing;

        float armLengthScale = entity.getArmLengthScale();
        float armStretch = 1.0f + (armLengthScale - 1.0f) * 1.4f;
        this.rightArm.yScale = armStretch;
        this.leftArm.yScale = armStretch;

        int moodLevel = entity.getMood().getLevel();
        this.body.pitch = 0.05f + moodLevel * 0.015f;
        this.head.pivotY = -2.0f - moodLevel * 0.5f;
    }
}
