package com.yukile394.eclipsehollowwatcher.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ModNetworking {

    private ModNetworking() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ScreenEffectPayload.ID, ScreenEffectPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(DistantEyesPayload.ID, DistantEyesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AmbientCuePayload.ID, AmbientCuePayload.CODEC);
    }

    public static void sendScreenEffect(ServerPlayerEntity player, String effectType, int durationTicks, float intensity) {
        if (ServerPlayNetworking.canSend(player, ScreenEffectPayload.ID)) {
            ServerPlayNetworking.send(player, new ScreenEffectPayload(effectType, durationTicks, intensity));
        }
    }

    public static void sendDistantEyes(ServerPlayerEntity player, double x, double y, double z, int lifetimeTicks, boolean approaching) {
        if (ServerPlayNetworking.canSend(player, DistantEyesPayload.ID)) {
            ServerPlayNetworking.send(player, new DistantEyesPayload(x, y, z, lifetimeTicks, approaching));
        }
    }

    public static void sendAmbientCue(ServerPlayerEntity player, String soundId, float volume, float pitch) {
        if (ServerPlayNetworking.canSend(player, AmbientCuePayload.ID)) {
            ServerPlayNetworking.send(player, new AmbientCuePayload(soundId, volume, pitch));
        }
    }
}
