package com.projectnull.horror;

import com.projectnull.ProjectNull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public final class NullPresence {
    public static final UUID NULL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String NULL_NAME = "Null";
    public static final int JOIN_AFTER_TICKS = 24000;

    public static final ResourceLocation SKIN_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ProjectNull.MODID, "textures/entity/null_skin.png");

    private NullPresence() {
    }

    public static Component tabListDisplayName() {
        return Component.literal(NULL_NAME)
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0xB8B8B8))
                        .withBold(true));
    }

    public static Component joinMessageName() {
        return Component.literal(NULL_NAME)
                .withStyle(style -> style
                        .withColor(TextColor.fromRgb(0xB8B8B8))
                        .withBold(true));
    }

    public static Component teamDisplayName() {
        return Component.literal(NULL_NAME).withStyle(ChatFormatting.DARK_GRAY);
    }
}
