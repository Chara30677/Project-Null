package com.projectnull.client.horror;

import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeTaskManagerScreen extends SharpHorrorScreen {
    private final PlayerDossier dossier;
    private int ticksOpen;

    public FakeTaskManagerScreen(PlayerDossier dossier) {
        super(Component.literal("Task Manager"));
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD0000000);

        int winWidth = Math.min(620, this.width - 40);
        int winHeight = Math.min(420, this.height - 40);
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF3A3A3A);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFFF3F3F3);
        graphics.fill(x, y, x + winWidth, y + 32, 0xFF0078D4);
        graphics.drawString(this.font, "Task Manager", x + 12, y + 11, 0xFFFFFFFF);

        int tabY = y + 36;
        graphics.fill(x + 8, tabY, x + winWidth - 8, tabY + 28, 0xFFE8E8E8);
        graphics.drawString(this.font, "Processes", x + 14, tabY + 9, HorrorStyle.ON_LIGHT_PRIMARY);
        graphics.drawString(this.font, "Performance", x + 90, tabY + 9, HorrorStyle.ON_LIGHT_MUTED);
        graphics.drawString(this.font, "App history", x + 175, tabY + 9, HorrorStyle.ON_LIGHT_MUTED);

        int listY = tabY + 34;
        int listHeight = winHeight - 110;
        graphics.fill(x + 8, listY, x + winWidth - 8, listY + listHeight, 0xFFFFFFFF);
        graphics.fill(x + 8, listY, x + winWidth - 8, listY + 20, 0xFFF7F7F7);
        graphics.drawString(this.font, "Name", x + 14, listY + 6, HorrorStyle.ON_LIGHT_SECONDARY);
        graphics.drawString(this.font, "Status", x + winWidth - 220, listY + 6, HorrorStyle.ON_LIGHT_SECONDARY);
        graphics.drawString(this.font, "CPU", x + winWidth - 120, listY + 6, HorrorStyle.ON_LIGHT_SECONDARY);
        graphics.fill(x + 8, listY + 20, x + winWidth - 8, listY + 21, 0xFFDDDDDD);

        String[][] processes = {
                {"javaw.exe (Minecraft)", "Not Responding", "99%"},
                {"null.exe", "Running", "0%"},
                {"explorer.exe", "Running", "2%"},
                {"svchost.exe", "Running", "1%"},
                {"System", "Running", "3%"},
                {"dwm.exe", "Running", "4%"},
                {"conhost.exe", "Running", "0%"},
                {"NULL_WATCHER", "Running", "100%"}
        };

        int rowY = listY + 26;
        for (int i = 0; i < processes.length && rowY < listY + listHeight - 18; i++) {
            boolean highlight = i == 0 || i == 1 || i == 7;
            if (highlight) {
                graphics.fill(x + 8, rowY, x + winWidth - 8, rowY + 16, i == 0 ? 0xFFFFE0E0 : 0xFFE8F0FF);
            }

            int nameColor = i == 1 || i == 7 ? 0xFFAA0000 : HorrorStyle.ON_LIGHT_PRIMARY;
            graphics.drawString(this.font, processes[i][0], x + 14, rowY + 4, nameColor);
            graphics.drawString(this.font, processes[i][1], x + winWidth - 220, rowY + 4, HorrorStyle.ON_LIGHT_SECONDARY);
            graphics.drawString(this.font, processes[i][2], x + winWidth - 120, rowY + 4, HorrorStyle.ON_LIGHT_SECONDARY);
            rowY += 16;
        }

        int statusY = y + winHeight - 64;
        graphics.fill(x + 8, statusY, x + winWidth - 8, statusY + 52, 0xFFECECEC);
        graphics.drawString(this.font, "End task", x + 14, statusY + 10, HorrorStyle.ON_LIGHT_MUTED);
        graphics.drawString(this.font, "Remote session: " + dossier.publicIp() + "  |  " + dossier.locationLine(),
                x + 14, statusY + 30, HorrorStyle.ON_LIGHT_SECONDARY);

        if (ticksOpen > 35) {
            graphics.drawString(this.font, "[Click to close]", x + 14, y + winHeight - 18, HorrorStyle.ON_LIGHT_MUTED);
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 420) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 25) {
            HorrorSounds.playPopup();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
