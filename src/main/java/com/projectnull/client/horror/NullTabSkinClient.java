package com.projectnull.client.horror;

import com.google.common.base.Suppliers;
import com.projectnull.ProjectNull;
import com.projectnull.horror.NullPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ProjectNull.MODID, value = Dist.CLIENT)
public final class NullTabSkinClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NullTabSkinClient.class);
    private static final PlayerSkin BLACK_SKIN = new PlayerSkin(
            NullPresence.SKIN_TEXTURE,
            NullPresence.SKIN_TEXTURE.toString(),
            null,
            null,
            PlayerSkin.Model.WIDE,
            false
    );
    private static final Supplier<PlayerSkin> BLACK_SKIN_LOOKUP = Suppliers.ofInstance(BLACK_SKIN);

    private NullTabSkinClient() {
    }

    public static void applyNullSkin() {
        applyNullSkin(Minecraft.getInstance());
    }

    public static void applyNullSkin(Minecraft minecraft) {
        if (minecraft.getConnection() == null) {
            return;
        }

        PlayerInfo info = minecraft.getConnection().getPlayerInfo(NullPresence.NULL_UUID);
        if (info == null) {
            return;
        }

        applyBlackSkin(info);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        applyNullSkin();
    }

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        applyNullSkin();
    }

    private static void applyBlackSkin(PlayerInfo info) {
        if (BLACK_SKIN.equals(info.getSkin())) {
            return;
        }

        try {
            Field skinLookupField = PlayerInfo.class.getDeclaredField("skinLookup");
            skinLookupField.setAccessible(true);
            skinLookupField.set(info, BLACK_SKIN_LOOKUP);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("[Project Null] Failed to apply black tab skin", e);
        }
    }
}
