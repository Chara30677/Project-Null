package com.projectnull.client.horror;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

/**
 * Horror screens that render crisply without Minecraft's default menu blur.
 */
public abstract class SharpHorrorScreen extends Screen {
    @Nullable
    private Screen returnScreen;

    protected SharpHorrorScreen(Component title) {
        this(title, null);
    }

    protected SharpHorrorScreen(Component title, @Nullable Screen returnScreen) {
        super(title);
        this.returnScreen = returnScreen;
    }

    @Nullable
    public Screen getReturnScreen() {
        return returnScreen;
    }

    public void setReturnScreen(@Nullable Screen returnScreen) {
        this.returnScreen = returnScreen;
    }

    @Override
    public void onClose() {
        if (returnScreen != null) {
            this.minecraft.setScreen(returnScreen);
        } else {
            super.onClose();
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void renderMenuBackground(GuiGraphics graphics) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
