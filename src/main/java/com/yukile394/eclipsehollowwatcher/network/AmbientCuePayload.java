package com.yukile394.eclipsehollowwatcher.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record AmbientCuePayload(String soundId, float volume, float pitch) implements CustomPayload {

    public static final CustomPayload.Id<AmbientCuePayload> ID =
            new CustomPayload.Id<>(NetworkingConstants.AMBIENT_CUE_ID);

    public static final PacketCodec<RegistryByteBuf, AmbientCuePayload> CODEC = PacketCodec.tuple(
            PacketCodec.of((v, buf) -> buf.writeString(v), RegistryByteBuf::readString), AmbientCuePayload::soundId,
            PacketCodec.of((v, buf) -> buf.writeFloat(v), RegistryByteBuf::readFloat), AmbientCuePayload::volume,
            PacketCodec.of((v, buf) -> buf.writeFloat(v), RegistryByteBuf::readFloat), AmbientCuePayload::pitch,
            AmbientCuePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
