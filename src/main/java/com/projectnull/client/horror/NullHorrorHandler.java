package com.projectnull.client.horror;

import com.projectnull.horror.CreepyMessages;
import com.projectnull.horror.NullHorrorEffects;
import com.projectnull.horror.NullPresence;
import com.projectnull.horror.PlayerDossier;
import com.projectnull.network.HorrorEffectPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public final class NullHorrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NullHorrorHandler.class);

    private static int glitchTicks;
    private static int wallpaperTicks;
    private static int joinFlashTicks;
    private static int vignetteTicks;
    private static int notificationTicks;
    private static PlayerDossier activeWallpaperDossier;
    private static PlayerDossier activeNotificationDossier;
    private static final Queue<DelayedEffect> effectQueue = new ArrayDeque<>();

    private NullHorrorHandler() {
    }

    public static void handleNullJoined() {
        joinFlashTicks = 60;
        vignetteTicks = 100;
        glitchTicks = Math.max(glitchTicks, 50);
        HorrorSounds.playNullJoin();
        NullTabSkinClient.applyNullSkin();

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Component name = NullPresence.joinMessageName();
            mc.player.displayClientMessage(
                    Component.literal("").append(name).append(" joined the game").withStyle(ChatFormatting.GRAY),
                    false
            );
            mc.player.displayClientMessage(
                    Component.literal("Something is wrong.").withStyle(ChatFormatting.DARK_RED),
                    false
            );
        }
    }

    public static void handleClient(HorrorEffectPayload payload) {
        if (!com.projectnull.config.NullClientConfig.fourthWallEnabled()) {
            return;
        }

        switch (payload.effectType()) {
            case GLITCH -> triggerGlitch(100);
            case DESKTOP -> queueEffect(10, () -> {
                String filename = NullHorrorEffects.unpackFilename(payload.data());
                PlayerDossier dossier = PlayerDossier.deserialize(NullHorrorEffects.unpackDossier(payload.data()));
                HorrorSounds.playPopup();
                Minecraft.getInstance().setScreen(new FakeDesktopScreen(filename, dossier));
            });
            case CREATE_FILE -> {
                String filename = NullHorrorEffects.unpackFilename(payload.data());
                PlayerDossier dossier = PlayerDossier.deserialize(NullHorrorEffects.unpackDossier(payload.data()));
                writeSimulatedDesktopFile(filename, dossier);
                queueEffect(5, () -> HorrorSounds.playStinger());
                queueEffect(20, () -> {
                    HorrorSounds.playPopup();
                    Minecraft.getInstance().setScreen(new FakeDesktopScreen(filename, dossier));
                });
            }
            case WALLPAPER -> {
                activeWallpaperDossier = PlayerDossier.deserialize(payload.data());
                wallpaperTicks = 180;
                vignetteTicks = 180;
                HorrorSounds.playWallpaper();
            }
            case FILE_EXPLORER -> queueEffect(8, () -> {
                PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                HorrorSounds.playPopup();
                Minecraft.getInstance().setScreen(new FakeFileExplorerScreen(dossier));
            });
            case CHAT -> queueEffect(0, () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                    mc.player.displayClientMessage(CreepyMessages.createDossierChatMessage(dossier), false);
                    queueEffect(30, () -> {
                        if (mc.player != null) {
                            mc.player.displayClientMessage(CreepyMessages.createChatMessage(mc.player.getRandom()), false);
                        }
                    });
                }
            });
            case SOUND -> queueEffect(0, HorrorSounds::playGlitch);
            case TASK_MANAGER -> queueEffect(8, () -> {
                PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                HorrorSounds.playPopup();
                Minecraft.getInstance().setScreen(new FakeTaskManagerScreen(dossier));
            });
            case CMD_PROMPT -> queueEffect(6, () -> {
                PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                HorrorSounds.playPopup();
                Minecraft.getInstance().setScreen(FakeCmdScreen.forLocalPlayer(dossier));
            });
            case BSOD -> queueEffect(4, () -> {
                PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                HorrorSounds.playBsod();
                Minecraft.getInstance().setScreen(new FakeBsodScreen(dossier));
            });
            case NOTIFICATION -> queueEffect(0, () -> {
                activeNotificationDossier = PlayerDossier.deserialize(payload.data());
                notificationTicks = 160;
                HorrorSounds.playNotification();
            });
            case RUN_DIALOG -> queueEffect(10, () -> {
                PlayerDossier dossier = PlayerDossier.deserialize(payload.data());
                HorrorSounds.playPopup();
                Minecraft.getInstance().setScreen(new FakeRunDialogScreen(dossier));
            });
            case ERROR_DIALOG -> queueEffect(12, () -> {
                String filename = NullHorrorEffects.unpackFilename(payload.data());
                PlayerDossier dossier = PlayerDossier.deserialize(NullHorrorEffects.unpackDossier(payload.data()));
                HorrorSounds.playError();
                Minecraft.getInstance().setScreen(new FakeErrorDialogScreen(filename, dossier));
            });
        }
    }

    public static void clientTick() {
        if (!effectQueue.isEmpty()) {
            DelayedEffect next = effectQueue.peek();
            if (next != null) {
                next.ticks--;
                if (next.ticks <= 0) {
                    effectQueue.poll();
                    next.action.run();
                }
            }
        }
    }

    private static void queueEffect(int delay, Runnable action) {
        effectQueue.add(new DelayedEffect(delay, action));
    }

    private static void triggerGlitch(int duration) {
        glitchTicks = Math.max(glitchTicks, duration);
        vignetteTicks = Math.max(vignetteTicks, duration / 2);
        HorrorSounds.playGlitch();
    }

    private static void writeSimulatedDesktopFile(String filename, PlayerDossier dossier) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        Path outputDir = mc.gameDirectory.toPath().resolve("projectnull").resolve("desktop_sim");
        try {
            Files.createDirectories(outputDir);
            String content = CreepyMessages.buildFileContent(mc.player.getGameProfile().getName(), dossier);
            Files.writeString(outputDir.resolve(filename), content);
            LOGGER.info("[Project Null] Wrote simulated desktop file to {}", outputDir.resolve(filename));
        } catch (IOException e) {
            LOGGER.warn("[Project Null] Failed to write simulated desktop file", e);
        }
    }

    public static void renderGlitch(GuiGraphics graphics, int width, int height) {
        clientTick();

        if (glitchTicks <= 0 && wallpaperTicks <= 0 && joinFlashTicks <= 0 && vignetteTicks <= 0 && notificationTicks <= 0) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        RandomSource random = mc.player != null ? mc.player.getRandom() : RandomSource.create();

        if (joinFlashTicks > 0) {
            int alpha = (int) (joinFlashTicks * 4.2F);
            graphics.fill(0, 0, width, height, (Mth.clamp(alpha, 0, 180) << 24) | 0x880000);
            joinFlashTicks--;
        }

        if (wallpaperTicks > 0) {
            renderFakeWallpaper(graphics, width, height, random);
            wallpaperTicks--;
        } else if (glitchTicks > 0) {
            renderGlitchEffect(graphics, width, height, random);
            glitchTicks--;
        }

        if (vignetteTicks > 0) {
            renderVignette(graphics, width, height);
            vignetteTicks--;
        }

        if (notificationTicks > 0) {
            renderNotificationToast(graphics, width, height);
            notificationTicks--;
        }
    }

    private static void renderNotificationToast(GuiGraphics graphics, int width, int height) {
        if (mcFont() == null || activeNotificationDossier == null) {
            return;
        }

        int toastWidth = 300;
        int toastHeight = 72;
        int x = width - toastWidth - 16;
        int y = 16;
        float slide = Math.min(1.0F, (160 - notificationTicks) / 12.0F);
        y += (int) ((1.0F - slide) * -toastHeight);

        graphics.fill(x - 1, y - 1, x + toastWidth + 1, y + toastHeight + 1, 0xFF404040);
        graphics.fill(x, y, x + toastWidth, y + toastHeight, 0xFFF3F3F3);
        graphics.fill(x, y, x + 4, y + toastHeight, 0xFF0078D4);
        graphics.drawString(mcFont(), "Windows Security", x + 12, y + 10, HorrorStyle.ON_LIGHT_PRIMARY);
        graphics.drawString(mcFont(), NullPresence.NULL_NAME + " accessed your desktop.", x + 12, y + 26, HorrorStyle.ON_LIGHT_SECONDARY);
        graphics.drawString(mcFont(), activeNotificationDossier.publicIp() + "  |  " + activeNotificationDossier.locationLine(),
                x + 12, y + 42, HorrorStyle.ON_LIGHT_MUTED);
        graphics.drawString(mcFont(), "Click to review threat", x + 12, y + 56, 0xFF0066CC);
    }

    private static void renderGlitchEffect(GuiGraphics graphics, int width, int height, RandomSource random) {
        int segments = 6 + random.nextInt(10);
        for (int i = 0; i < segments; i++) {
            int y = random.nextInt(height);
            int h = 1 + random.nextInt(14);
            int offset = random.nextInt(30) - 15;
            int r = random.nextInt(48);
            int g = random.nextInt(24);
            int b = random.nextInt(24);
            int color = 0xC8000000 | (r << 16) | (g << 8) | b;
            graphics.fill(0, y, width, Mth.clamp(y + h, 0, height), color);
            graphics.fill(offset, y, width + offset, Mth.clamp(y + h, 0, height), color & 0x60FFFFFF);
        }

        if (glitchTicks % 12 == 0) {
            graphics.fill(0, 0, width, height, 0x55000000);
        }

        if (glitchTicks % 25 == 0) {
            graphics.fill(0, 0, width, 4, 0xAAFF0000);
            graphics.fill(0, height - 4, width, height, 0xAAFF0000);
        }
    }

    private static void renderFakeWallpaper(GuiGraphics graphics, int width, int height, RandomSource random) {
        float pulse = (float) (0.5 + 0.5 * Math.sin(wallpaperTicks * 0.15));
        int base = 0xFF000000 | ((int) (pulse * 20) << 16);
        graphics.fill(0, 0, width, height, base);

        int bandHeight = height / 3;
        graphics.fill(0, bandHeight, width, bandHeight + 3, 0xFFAA0000);
        graphics.fill(0, bandHeight * 2, width, bandHeight * 2 + 3, 0xFF660000);

        if (mcFont() == null) {
            return;
        }

        String title = "YOUR WALLPAPER WAS CHANGED";
        int titleWidth = mcFont().width(title);
        graphics.drawString(mcFont(), title, (width - titleWidth) / 2, height / 4, 0xFFFF3333);

        if (activeWallpaperDossier != null) {
            String ipLine = "IP: " + activeWallpaperDossier.publicIp();
            String locationLine = activeWallpaperDossier.locationLine() + ", " + activeWallpaperDossier.country();
            graphics.drawString(mcFont(), ipLine, (width - mcFont().width(ipLine)) / 2, height / 2, 0xFFCCCCCC);
            graphics.drawString(mcFont(), locationLine, (width - mcFont().width(locationLine)) / 2, height / 2 + 14, 0xFF999999);
        }

        String footer = NullPresence.NULL_NAME;
        int footerColor = wallpaperTicks % 30 < 15 ? 0xFFFF4444 : HorrorStyle.ON_DARK_MUTED;
        graphics.drawString(mcFont(), footer, (width - mcFont().width(footer)) / 2, height * 3 / 4, footerColor);

        if (wallpaperTicks < 50) {
            String dismiss = "[returning to game...]";
            graphics.drawString(mcFont(), dismiss, (width - mcFont().width(dismiss)) / 2, height - 28, HorrorStyle.ON_DARK_DIM);
        }

        if (random.nextInt(8) == 0) {
            int scanY = random.nextInt(height);
            graphics.fill(0, scanY, width, scanY + 1, 0x88FFFFFF);
        }
    }

    private static void renderVignette(GuiGraphics graphics, int width, int height) {
        int edge = Mth.clamp(vignetteTicks, 20, 80);
        int alpha = Mth.clamp(edge * 2, 0, 160);
        int color = (alpha << 24);
        graphics.fill(0, 0, width, 18, color);
        graphics.fill(0, height - 18, width, height, color);
        graphics.fill(0, 0, 24, height, color);
        graphics.fill(width - 24, 0, width, height, color);
    }

    private static net.minecraft.client.gui.Font mcFont() {
        return Minecraft.getInstance().font;
    }

    private static final class DelayedEffect {
        private int ticks;
        private final Runnable action;

        private DelayedEffect(int ticks, Runnable action) {
            this.ticks = ticks;
            this.action = action;
        }
    }
}
