package com.projectnull.client.horror;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class HorrorSounds {
    private HorrorSounds() {
    }

    public static void playPopup() {
        play(SoundEvents.UI_BUTTON_CLICK.value(), 0.35F, 0.55F);
        play(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F, 0.35F);
    }

    public static void playFileOpen() {
        play(SoundEvents.BOOK_PAGE_TURN, 0.4F, 0.7F);
    }

    public static void playGlitch() {
        play(SoundEvents.ENDERMAN_STARE, 0.25F, 0.4F);
        play(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.08F, 1.8F);
    }

    public static void playWallpaper() {
        play(SoundEvents.WARDEN_HEARTBEAT, 0.2F, 0.5F);
        play(SoundEvents.AMBIENT_CAVE.value(), 0.35F, 0.25F);
    }

    public static void playNullJoin() {
        play(SoundEvents.WITHER_SPAWN, 0.15F, 0.6F);
        play(SoundEvents.AMBIENT_CAVE.value(), 0.5F, 0.35F);
    }

    public static void playStinger() {
        play(SoundEvents.SCULK_SHRIEKER_SHRIEK, 0.12F, 0.55F);
    }

    public static void playError() {
        play(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.2F, 0.35F);
        play(SoundEvents.NOTE_BLOCK_BASS.value(), 0.35F, 0.5F);
    }

    public static void playNotification() {
        play(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.25F, 0.55F);
        play(SoundEvents.UI_BUTTON_CLICK.value(), 0.2F, 0.8F);
    }

    public static void playBsod() {
        play(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.06F, 0.7F);
        play(SoundEvents.ANVIL_LAND, 0.08F, 0.5F);
    }

    private static void play(SoundEvent sound, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.player.playSound(sound, volume, pitch);
    }
}
