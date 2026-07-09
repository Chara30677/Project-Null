package com.projectnull.client.horror;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FakeNotepadScreen extends SharpHorrorScreen {
    private final String filename;
    private final String content;
    private final Screen parent;
    private int ticksOpen;
    private int visibleChars;

    public FakeNotepadScreen(String filename, String content, Screen parent) {
        super(Component.literal(filename));
        this.filename = filename;
        this.content = content;
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0000000);

        int winWidth = Math.min(480, this.width - 50);
        int winHeight = Math.min(320, this.height - 50);
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF555555);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFFFDFDFD);
        graphics.fill(x, y, x + winWidth, y + 26, 0xFFF0F0F0);
        graphics.drawString(this.font, filename + " - Notepad", x + 10, y + 9, HorrorStyle.ON_LIGHT_PRIMARY);

        String display = content.substring(0, Math.min(visibleChars, content.length()));
        String[] lines = display.split("\n", -1);
        int textY = y + 34;
        for (int i = 0; i < lines.length && textY < y + winHeight - 24; i++) {
            graphics.drawString(this.font, lines[i], x + 12, textY, HorrorStyle.ON_LIGHT_SECONDARY);
            textY += 10;
        }

        if (ticksOpen % 20 < 10 && visibleChars < content.length()) {
            int cursorX = x + 12 + this.font.width(lines.length > 0 ? lines[lines.length - 1] : "");
            graphics.fill(cursorX, textY - 10, cursorX + 6, textY, 0xFF000000);
        }

        if (ticksOpen > 25) {
            graphics.drawString(this.font, "[Click to close]", x + 12, y + winHeight - 16, HorrorStyle.ON_LIGHT_MUTED);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (visibleChars < content.length() && ticksOpen % 2 == 0) {
            visibleChars += 2;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 18) {
            HorrorSounds.playPopup();
            this.minecraft.setScreen(parent);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
