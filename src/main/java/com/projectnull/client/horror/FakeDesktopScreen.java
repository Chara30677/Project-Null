package com.projectnull.client.horror;

import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeDesktopScreen extends SharpHorrorScreen {
    private final String filename;
    private final PlayerDossier dossier;
    private int ticksOpen;

    public FakeDesktopScreen(String filename, PlayerDossier dossier) {
        super(Component.literal("NULL"));
        this.filename = filename.isEmpty() ? "null_is_watching.txt" : filename;
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xE0000000);

        int boxWidth = 380;
        int boxHeight = 210;
        int x = (this.width - boxWidth) / 2;
        int y = (this.height - boxHeight) / 2;

        int pulse = ticksOpen % 40 < 20 ? 0xFFAA2222 : 0xFF661111;
        graphics.fill(x - 2, y - 2, x + boxWidth + 2, y + boxHeight + 2, pulse);
        graphics.fill(x, y, x + boxWidth, y + boxHeight, 0xFF1A1A1A);
        graphics.fill(x, y, x + boxWidth, y + 28, 0xFF2B2B2B);

        graphics.fill(x + 10, y + 38, x + 30, y + 58, 0xFFFFCC00);
        graphics.fill(x + 14, y + 48, x + 26, y + 54, 0xFF333333);

        graphics.drawString(this.font, "Windows Security Alert", x + 38, y + 8, HorrorStyle.ON_DARK_PRIMARY);
        graphics.drawString(this.font, "Threat detected on your system", x + 38, y + 42, HorrorStyle.ON_DARK_ACCENT);

        graphics.drawString(this.font, "A file was placed on your desktop:", x + 14, y + 68, HorrorStyle.ON_DARK_SECONDARY);
        graphics.drawString(this.font, filename, x + 14, y + 84, HorrorStyle.ON_DARK_ACCENT);
        graphics.drawString(this.font, "IP: " + dossier.publicIp(), x + 14, y + 102, HorrorStyle.ON_DARK_SECONDARY);
        graphics.drawString(this.font, dossier.locationLine() + ", " + dossier.country(), x + 14, y + 116, HorrorStyle.ON_DARK_MUTED);
        graphics.drawString(this.font, "Wallpaper modified. Check Desktop > NULL", x + 14, y + 134, HorrorStyle.ON_DARK_MUTED);
        graphics.drawString(this.font, "NULL is watching.", x + 14, y + 150, HorrorStyle.ON_DARK_DIM);

        if (ticksOpen > 60) {
            float blink = (ticksOpen / 10) % 2 == 0 ? 1.0F : 0.55F;
            int alpha = (int) (blink * 255);
            int color = 0xFF000000 | (alpha << 16) | (alpha << 8) | alpha;
            graphics.drawString(this.font, "[Click anywhere to dismiss]", x + 14, y + 176, color);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 260) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 35) {
            HorrorSounds.playPopup();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
