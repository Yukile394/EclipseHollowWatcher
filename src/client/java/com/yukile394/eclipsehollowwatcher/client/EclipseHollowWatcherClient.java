package com.yukile394.eclipsehollowwatcher.client;

import com.yukile394.eclipsehollowwatcher.client.effect.DistantEyesHandler;
import com.yukile394.eclipsehollowwatcher.client.effect.ScreenEffectHandler;
import com.yukile394.eclipsehollowwatcher.client.effect.VignetteHudRenderer;
import com.yukile394.eclipsehollowwatcher.client.render.HerobrineModel;
import com.yukile394.eclipsehollowwatcher.client.render.HerobrineModelLayers;
import com.yukile394.eclipsehollowwatcher.client.render.HerobrineRenderer;
import com.yukile394.eclipsehollowwatcher.entity.ModEntities;
import com.yukile394.eclipsehollowwatcher.network.AmbientCuePayload;
import com.yukile394.eclipsehollowwatcher.network.DistantEyesPayload;
import com.yukile394.eclipsehollowwatcher.network.ScreenEffectPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class EclipseHollowWatcherClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ScreenEffectPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        ScreenEffectHandler.startEffect(payload.effectType(), payload.durationTicks(), payload.intensity())));

        ClientPlayNetworking.registerGlobalReceiver(DistantEyesPayload.ID, (payload, context) ->
                context.client().execute(() ->
                        DistantEyesHandler.spawn(payload.x(), payload.y(), payload.z(), payload.lifetimeTicks(), payload.approaching())));

        ClientPlayNetworking.registerGlobalReceiver(AmbientCuePayload.ID, (payload, context) ->
                context.client().execute(() -> playAmbientCue(payload)));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ScreenEffectHandler.tick();
            DistantEyesHandler.tick();
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> VignetteHudRenderer.render(context));

        EntityModelLayerRegistry.registerModelLayer(HerobrineModelLayers.MAIN, HerobrineModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.HEROBRINE, HerobrineRenderer::new);
    }

    private void playAmbientCue(AmbientCuePayload payload) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        Identifier id = Identifier.of(payload.soundId());
        SoundEvent event = Registries.SOUND_EVENT.get(id);
        if (event == null) return;
        client.player.playSound(event, payload.volume(), payload.pitch());
    }
}
