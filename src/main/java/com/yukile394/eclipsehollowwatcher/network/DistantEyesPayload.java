package com.yukile394.eclipsehollowwatcher.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DistantEyesPayload(double x, double y, double z, int lifetimeTicks, boolean approaching)
        implements CustomPayload {

    public static final CustomPayload.Id<DistantEyesPayload> ID =
            new CustomPayload.Id<>(NetworkingConstants.DISTANT_EYES_ID);

    public static final PacketCodec<RegistryByteBuf, DistantEyesPayload> CODEC = PacketCodec.tuple(
            PacketCodec.of((v, buf) -> buf.writeDouble(v), RegistryByteBuf::readDouble), DistantEyesPayload::x,
            PacketCodec.of((v, buf) -> buf.writeDouble(v), RegistryByteBuf::readDouble), DistantEyesPayload::y,
            PacketCodec.of((v, buf) -> buf.writeDouble(v), RegistryByteBuf::readDouble), DistantEyesPayload::z,
            PacketCodec.of((v, buf) -> buf.writeVarInt(v), RegistryByteBuf::readVarInt), DistantEyesPayload::lifetimeTicks,
            PacketCodec.of((v, buf) -> buf.writeBoolean(v), RegistryByteBuf::readBoolean), DistantEyesPayload::approaching,
            DistantEyesPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
