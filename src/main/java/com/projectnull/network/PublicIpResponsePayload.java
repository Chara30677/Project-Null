package com.projectnull.network;

import com.projectnull.ProjectNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PublicIpResponsePayload(String publicIp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PublicIpResponsePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ProjectNull.MODID, "public_ip_response")
    );

    public static final StreamCodec<ByteBuf, PublicIpResponsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PublicIpResponsePayload::publicIp,
            PublicIpResponsePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
