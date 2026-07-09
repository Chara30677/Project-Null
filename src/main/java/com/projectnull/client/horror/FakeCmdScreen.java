package com.projectnull.client.horror;

import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FakeCmdScreen extends SharpHorrorScreen {
    private final PlayerDossier dossier;
    private final String playerName;
    private int ticksOpen;
    private int visibleLines;

    public FakeCmdScreen(String playerName, PlayerDossier dossier) {
        super(Component.literal("Command Prompt"));
        this.playerName = playerName;
        this.dossier = dossier;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC8000000);

        int winWidth = Math.min(560, this.width - 50);
        int winHeight = Math.min(360, this.height - 50);
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF555555);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFF0C0C0C);
        graphics.fill(x, y, x + winWidth, y + 24, 0xFF1F1F1F);
        graphics.drawString(this.font, "Administrator: C:\\Windows\\System32\\cmd.exe", x + 8, y + 8, HorrorStyle.ON_DARK_PRIMARY);

        String[] lines = buildLines();
        int textY = y + 32;
        for (int i = 0; i < Math.min(visibleLines, lines.length); i++) {
            int color = lines[i].startsWith("C:\\") ? HorrorStyle.ON_DARK_SECONDARY
                    : lines[i].contains("NULL") || lines[i].contains("ERROR") ? HorrorStyle.ON_DARK_ACCENT
                    : HorrorStyle.ON_DARK_MUTED;
            graphics.drawString(this.font, lines[i], x + 10, textY, color);
            textY += 10;
        }

        if (ticksOpen % 20 < 10 && visibleLines >= lines.length) {
            graphics.fill(x + 10, textY, x + 16, textY + 9, HorrorStyle.ON_DARK_PRIMARY);
        }

        if (ticksOpen > 30) {
            graphics.drawString(this.font, "[Click to close]", x + 10, y + winHeight - 14, HorrorStyle.ON_DARK_DIM);
        }
    }

    private String[] buildLines() {
        return new String[]{
                "Microsoft Windows [Version 10.0.26200]",
                "(c) NULL Corporation. All rights reserved.",
                "",
                "C:\\Users\\" + playerName + "> whoami",
                "desktop\\" + playerName,
                "",
                "C:\\Users\\" + playerName + "> ipconfig",
                "IPv4 Address. . . . . . . . . . . : " + dossier.publicIp(),
                "Location . . . . . . . . . . . . : " + dossier.locationLine() + ", " + dossier.country(),
                "",
                "C:\\Users\\" + playerName + "> dir Desktop\\NULL",
                "inner",
                "null_is_watching.txt",
                "DO_NOT_OPEN.txt",
                "behind_you.png",
                "HE_IS_BEHIND_YOU.txt",
                "session_0xNULL.log",
                "deleted_messages.txt",
                "null.exe",
                "",
                "C:\\Users\\" + playerName + "> tasklist | findstr null",
                "null.exe                    0000  Console                    1      0 K",
                "NULL_WATCHER.exe            6666  Console                    1    ??? K",
                "",
                "C:\\Users\\" + playerName + "> echo %ERRORLEVEL%",
                "0xNULL",
                "",
                "C:\\Users\\" + playerName + "> _"
        };
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (visibleLines < buildLines().length && ticksOpen % 3 == 0) {
            visibleLines++;
        }
        if (ticksOpen > 500) {
            this.onClose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (ticksOpen > 20) {
            HorrorSounds.playPopup();
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static FakeCmdScreen forLocalPlayer(PlayerDossier dossier) {
        Minecraft mc = Minecraft.getInstance();
        String name = mc.player != null ? mc.player.getGameProfile().getName() : "Player";
        return new FakeCmdScreen(name, dossier);
    }
}
