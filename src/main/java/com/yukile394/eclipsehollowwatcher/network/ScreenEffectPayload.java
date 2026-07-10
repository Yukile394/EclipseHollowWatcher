package com.yukile394.eclipsehollowwatcher.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ScreenEffectPayload(String effectType, int durationTicks, float intensity) implements CustomPayload {

    public static final CustomPayload.Id<ScreenEffectPayload> ID =
            new CustomPayload.Id<>(NetworkingConstants.SCREEN_EFFECT_ID);

    public static final PacketCodec<RegistryByteBuf, ScreenEffectPayload> CODEC = PacketCodec.tuple(
            PacketCodec.of(
                    (value, buf) -> buf.writeString(value),
                    RegistryByteBuf::readString
            ),
            ScreenEffectPayload::effectType,
            PacketCodec.of(
                    (value, buf) -> buf.writeVarInt(value),
                    RegistryByteBuf::readVarInt
            ),
            ScreenEffectPayload::durationTicks,
            PacketCodec.of(
                    (value, buf) -> buf.writeFloat(value),
                    RegistryByteBuf::readFloat
            ),
            ScreenEffectPayload::intensity,
            ScreenEffectPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // Recognized effectType values: "FOG", "VIGNETTE", "SCREEN_SHAKE", "BLINDNESS_PULSE"
}
