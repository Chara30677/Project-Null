package com.projectnull.client.horror;

import com.projectnull.horror.NullPresence;
import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeBsodScreen extends SharpHorrorScreen {
    private final PlayerDossier dossier;
    private int ticksOpen;

    public FakeBsodScreen(PlayerDossier dossier) {
        super(Component.literal("BSOD"));
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF0078D7);

        int x = 48;
        int y = 56;
        graphics.drawString(this.font, ":(" , x, y, 0xFFFFFFFF);
        graphics.drawString(this.font, "Your PC ran into a problem and needs to restart.", x, y + 40, 0xFFFFFFFF);
        graphics.drawString(this.font, "We're just collecting some error info, and then we'll restart for you.",
                x, y + 56, 0xFFFFFFFF);

        int progress = Math.min(100, ticksOpen * 2);
        graphics.drawString(this.font, progress + "% complete", x, y + 80, 0xFFFFFFFF);

        graphics.drawString(this.font, "Stop code: NULL_ENTITY_EXCEPTION", x, y + 120, 0xFFFFFFFF);
        graphics.drawString(this.font, "What failed: minecraft.exe", x, y + 136, 0xFFFFFFFF);
        graphics.drawString(this.font, "Session IP: " + dossier.publicIp(), x, y + 152, 0xFFE8E8E8);
        graphics.drawString(this.font, "Last known location: " + dossier.locationLine() + ", " + dossier.country(),
                x, y + 168, 0xFFE8E8E8);
        graphics.drawString(this.font, "Watcher: " + NullPresence.NULL_NAME, x, y + 184, 0xFFFFCCCC);

        if (ticksOpen > 80) {
            String hint = "[Click to return to Minecraft]";
            graphics.drawString(this.font, hint, x, this.height - 40, 0xFFDDDDDD);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 360) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 50) {
            HorrorSounds.playPopup();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
