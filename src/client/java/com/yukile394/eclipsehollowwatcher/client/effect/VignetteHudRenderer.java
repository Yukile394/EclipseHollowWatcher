package com.yukile394.eclipsehollowwatcher.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class VignetteHudRenderer {

    private VignetteHudRenderer() {
    }

    public static void render(DrawContext context) {
        float alpha = ScreenEffectHandler.getVignetteAlpha();
        float fog = ScreenEffectHandler.getFogIntensity();
        float combined = Math.max(alpha, fog * 0.6f);
        if (combined <= 0.01f) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        int argb = ((int) (combined * 200) << 24);

        int borderThickness = (int) (Math.min(width, height) * 0.18f * combined + 20);
        context.fill(0, 0, width, borderThickness, argb);
        context.fill(0, height - borderThickness, width, height, argb);
        context.fill(0, 0, borderThickness, height, argb);
        context.fill(width - borderThickness, 0, width, height, argb);
    }
}
