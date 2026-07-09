package com.projectnull.client.horror;

import com.projectnull.horror.NullPresence;
import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeErrorDialogScreen extends SharpHorrorScreen {
    private final String filename;
    private final PlayerDossier dossier;
    private int ticksOpen;

    public FakeErrorDialogScreen(String filename, PlayerDossier dossier) {
        super(Component.literal("Error"));
        this.filename = filename.isEmpty() ? "null_is_watching.txt" : filename;
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xA0000000);

        int winWidth = 400;
        int winHeight = 180;
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF555555);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFFF0F0F0);
        graphics.fill(x, y, x + winWidth, y + 28, 0xFFF8F8F8);
        graphics.drawString(this.font, filename + " - Error", x + 10, y + 9, HorrorStyle.ON_LIGHT_PRIMARY);

        graphics.fill(x + 16, y + 44, x + 36, y + 64, 0xFFFFCC00);
        graphics.drawString(this.font, "!", x + 23, y + 48, 0xFF000000);

        graphics.drawString(this.font, "Windows cannot access the specified device, path, or file.",
                x + 48, y + 44, HorrorStyle.ON_LIGHT_PRIMARY);
        graphics.drawString(this.font, "You may not have the appropriate permissions.",
                x + 48, y + 58, HorrorStyle.ON_LIGHT_SECONDARY);
        graphics.drawString(this.font, NullPresence.NULL_NAME + " has already opened it.",
                x + 48, y + 72, 0xFFAA0000);
        graphics.drawString(this.font, "IP: " + dossier.publicIp() + "  |  " + dossier.locationLine(),
                x + 48, y + 92, HorrorStyle.ON_LIGHT_MUTED);

        graphics.drawString(this.font, "OK", x + winWidth - 44, y + winHeight - 28, HorrorStyle.ON_LIGHT_PRIMARY);

        if (ticksOpen > 30) {
            graphics.drawString(this.font, "[Click to dismiss]", x + 16, y + winHeight - 44, HorrorStyle.ON_LIGHT_MUTED);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 280) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 20) {
            HorrorSounds.playError();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
