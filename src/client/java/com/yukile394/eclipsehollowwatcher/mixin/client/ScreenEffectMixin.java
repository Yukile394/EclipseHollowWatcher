package com.yukile394.eclipsehollowwatcher.mixin.client;

import com.yukile394.eclipsehollowwatcher.client.effect.ScreenEffectHandler;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class ScreenEffectMixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void eclipsehollowwatcher$applyShake(CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        float shakeIntensity = ScreenEffectHandler.getCurrentShakeIntensity();
        if (shakeIntensity > 0.0f) {
            float yawJitter = (ScreenEffectHandler.RANDOM.nextFloat() - 0.5f) * shakeIntensity * 6.0f;
            float pitchJitter = (ScreenEffectHandler.RANDOM.nextFloat() - 0.5f) * shakeIntensity * 4.0f;
            ((CameraInvokerMixin) (Object) self)
                    .eclipsehollowwatcher$invokeSetRotation(self.getYaw() + yawJitter, self.getPitch() + pitchJitter);
        }
    }
}
