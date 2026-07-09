package com.projectnull.network;

import com.projectnull.ProjectNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HorrorEffectPayload(HorrorEffectType effectType, String data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HorrorEffectPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ProjectNull.MODID, "horror_effect")
    );

    public static final StreamCodec<ByteBuf, HorrorEffectPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.effectType().name(),
            ByteBufCodecs.STRING_UTF8,
            HorrorEffectPayload::data,
            (typeName, data) -> new HorrorEffectPayload(HorrorEffectType.valueOf(typeName), data)
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum HorrorEffectType {
        GLITCH,
        DESKTOP,
        CREATE_FILE,
        WALLPAPER,
        FILE_EXPLORER,
        CHAT,
        SOUND,
        TASK_MANAGER,
        CMD_PROMPT,
        BSOD,
        NOTIFICATION,
        RUN_DIALOG,
        ERROR_DIALOG
    }
}
