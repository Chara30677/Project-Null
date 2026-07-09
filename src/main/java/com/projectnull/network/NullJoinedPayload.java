package com.projectnull.network;

import com.projectnull.ProjectNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NullJoinedPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NullJoinedPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ProjectNull.MODID, "null_joined")
    );

    public static final StreamCodec<ByteBuf, NullJoinedPayload> STREAM_CODEC = StreamCodec.unit(new NullJoinedPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
