package com.yukile394.eclipsehollowwatcher.mixin.client;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraInvokerMixin {

    @Invoker("setRotation")
    void eclipsehollowwatcher$invokeSetRotation(float yaw, float pitch);
}
