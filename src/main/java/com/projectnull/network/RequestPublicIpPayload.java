package com.projectnull.network;

import com.projectnull.ProjectNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestPublicIpPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestPublicIpPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ProjectNull.MODID, "request_public_ip")
    );

    public static final StreamCodec<ByteBuf, RequestPublicIpPayload> STREAM_CODEC = StreamCodec.unit(new RequestPublicIpPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
