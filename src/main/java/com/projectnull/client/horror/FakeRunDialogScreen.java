package com.projectnull.client.horror;

import com.projectnull.horror.NullPresence;
import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeRunDialogScreen extends SharpHorrorScreen {
    private final PlayerDossier dossier;
    private int ticksOpen;

    public FakeRunDialogScreen(PlayerDossier dossier) {
        super(Component.literal("Run"));
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x90000000);

        int winWidth = 360;
        int winHeight = 150;
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF555555);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFFF0F0F0);
        graphics.fill(x, y, x + winWidth, y + 28, 0xFFF8F8F8);
        graphics.drawString(this.font, "Run", x + 10, y + 9, HorrorStyle.ON_LIGHT_PRIMARY);
        graphics.drawString(this.font, "Type the name of a program, folder, document, or Internet resource, and Windows will open it for you.",
                x + 12, y + 38, HorrorStyle.ON_LIGHT_SECONDARY);

        int fieldY = y + 72;
        graphics.fill(x + 12, fieldY, x + winWidth - 12, fieldY + 20, 0xFFFFFFFF);
        graphics.fill(x + 12, fieldY, x + winWidth - 12, fieldY + 1, 0xFF888888);
        graphics.fill(x + 12, fieldY + 19, x + winWidth - 12, fieldY + 20, 0xFF888888);
        String command = "C:\\Users\\Public\\Desktop\\NULL\\null_is_watching.exe";
        graphics.drawString(this.font, command, x + 16, fieldY + 6, 0xFFAA0000);

        graphics.drawString(this.font, "OK", x + winWidth - 92, y + winHeight - 28, HorrorStyle.ON_LIGHT_PRIMARY);
        graphics.drawString(this.font, "Cancel", x + winWidth - 48, y + winHeight - 28, HorrorStyle.ON_LIGHT_MUTED);

        if (ticksOpen > 25) {
            graphics.drawString(this.font, dossier.publicIp() + "  |  " + NullPresence.NULL_NAME,
                    x + 12, y + winHeight - 44, HorrorStyle.ON_LIGHT_MUTED);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 300) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 18) {
            HorrorSounds.playPopup();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
