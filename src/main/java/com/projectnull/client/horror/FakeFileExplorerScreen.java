package com.projectnull.client.horror;

import com.projectnull.horror.CreepyMessages;
import com.projectnull.horror.PlayerDossier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FakeFileExplorerScreen extends SharpHorrorScreen {
    private static final int ROW_HEIGHT = 18;
    private static final List<String> BUILTIN_FILES = List.of(
            "null_is_watching.txt",
            "DO_NOT_OPEN.txt",
            "NULL_README.txt",
            "your_location.txt",
            "connection_log.txt",
            "behind_you.png",
            "desktop.ini",
            "NULL"
    );

    private final PlayerDossier dossier;
    private final List<ExplorerEntry> entries = new ArrayList<>();
    private int scrollOffset;
    private int ticksOpen;
    private int selectedIndex = -1;

    public FakeFileExplorerScreen(PlayerDossier dossier) {
        super(Component.literal("Desktop"));
        this.dossier = dossier;
        loadEntries();
    }

    private void loadEntries() {
        entries.clear();
        entries.add(ExplorerEntry.folder(".."));
        entries.add(ExplorerEntry.folder("NULL"));

        Path desktopSim = Minecraft.getInstance().gameDirectory.toPath()
                .resolve("projectnull").resolve("desktop_sim");
        if (Files.isDirectory(desktopSim)) {
            try (Stream<Path> paths = Files.list(desktopSim)) {
                paths.filter(Files::isRegularFile)
                        .map(path -> path.getFileName().toString())
                        .sorted()
                        .map(ExplorerEntry::file)
                        .forEach(entries::add);
            } catch (IOException ignored) {
            }
        }

        for (String builtin : BUILTIN_FILES) {
            if (entries.stream().noneMatch(e -> e.name.equals(builtin))) {
                entries.add(ExplorerEntry.file(builtin));
            }
        }

        entries.sort(Comparator.comparing((ExplorerEntry e) -> !e.folder).thenComparing(e -> e.name.toLowerCase()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF101018);

        int winWidth = Math.min(560, this.width - 30);
        int winHeight = Math.min(380, this.height - 30);
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;

        graphics.fill(x - 1, y - 1, x + winWidth + 1, y + winHeight + 1, 0xFF404040);
        graphics.fill(x, y, x + winWidth, y + winHeight, 0xFFF5F5F5);
        graphics.fill(x, y, x + winWidth, y + 30, 0xFF0078D4);
        graphics.drawString(this.font, "File Explorer", x + 10, y + 10, 0xFFFFFFFF);

        int toolbarY = y + 34;
        graphics.fill(x + 8, toolbarY, x + winWidth - 8, toolbarY + 24, 0xFFECECEC);
        graphics.drawString(this.font, "This PC > Desktop > NULL", x + 14, toolbarY + 8, 0xFF222222);

        int listY = toolbarY + 30;
        int listHeight = winHeight - 96;
        graphics.fill(x + 8, listY, x + winWidth - 8, listY + listHeight, 0xFFFFFFFF);

        graphics.fill(x + 8, listY, x + winWidth - 8, listY + 18, 0xFFF0F0F0);
        graphics.drawString(this.font, "Name", x + 14, listY + 5, 0xFF444444);
        graphics.drawString(this.font, "Date modified", x + winWidth - 130, listY + 5, 0xFF444444);
        graphics.fill(x + 8, listY + 18, x + winWidth - 8, listY + 19, 0xFFCCCCCC);

        int visibleRows = (listHeight - 24) / ROW_HEIGHT;
        for (int i = 0; i < visibleRows; i++) {
            int index = scrollOffset + i;
            if (index >= entries.size()) {
                break;
            }

            ExplorerEntry entry = entries.get(index);
            int rowY = listY + 22 + i * ROW_HEIGHT;
            boolean hovered = mouseX >= x + 8 && mouseX <= x + winWidth - 8
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;

            if (hovered || index == selectedIndex) {
                graphics.fill(x + 8, rowY, x + winWidth - 8, rowY + ROW_HEIGHT, 0xFFDCEEFF);
            }

            int color = entry.folder ? 0xFFE6A800 : HorrorStyle.ON_LIGHT_PRIMARY;
            String prefix = entry.folder ? "[DIR] " : "[FILE] ";
            graphics.drawString(this.font, prefix + entry.name, x + 14, rowY + 5, color);
            graphics.drawString(this.font, entry.date, x + winWidth - 130, rowY + 5, 0xFF666666);
        }

        int statusY = y + winHeight - 30;
        graphics.fill(x + 8, statusY, x + winWidth - 8, statusY + 22, 0xFFECECEC);
        graphics.drawString(this.font, entries.size() + " items  |  " + dossier.publicIp() + "  |  " + dossier.locationLine(),
                x + 12, statusY + 7, HorrorStyle.ON_LIGHT_SECONDARY);

        if (ticksOpen > 40) {
            graphics.drawString(this.font, "Double-click a file to open  |  ESC to close", x + 12, y + winHeight - 48, HorrorStyle.ON_LIGHT_MUTED);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int winWidth = Math.min(560, this.width - 30);
        int winHeight = Math.min(380, this.height - 30);
        int x = (this.width - winWidth) / 2;
        int y = (this.height - winHeight) / 2;
        int listY = y + 34 + 30;
        int listHeight = winHeight - 96;
        int visibleRows = (listHeight - 24) / ROW_HEIGHT;

        for (int i = 0; i < visibleRows; i++) {
            int index = scrollOffset + i;
            if (index >= entries.size()) {
                break;
            }
            int rowY = listY + 22 + i * ROW_HEIGHT;
            if (mouseX >= x + 8 && mouseX <= x + winWidth - 8
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT) {
                ExplorerEntry entry = entries.get(index);
                selectedIndex = index;
                if (!entry.folder) {
                    openFile(entry.name);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, entries.size() - 1);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) scrollY));
        return true;
    }

    private void openFile(String filename) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        HorrorSounds.playFileOpen();
        String content = readFileContent(filename);
        mc.setScreen(new FakeNotepadScreen(filename, content, this));
    }

    private String readFileContent(String filename) {
        Path file = Minecraft.getInstance().gameDirectory.toPath()
                .resolve("projectnull").resolve("desktop_sim").resolve(filename);
        if (Files.isRegularFile(file)) {
            try {
                return Files.readString(file);
            } catch (IOException ignored) {
            }
        }

        if (filename.endsWith(".png") || filename.endsWith(".ini")) {
            return "[BINARY FILE]\n\nNULL embedded data in " + filename + "\nIP: " + dossier.publicIp()
                    + "\nLocation: " + dossier.locationLine() + ", " + dossier.country();
        }

        return CreepyMessages.buildFileContent(
                Minecraft.getInstance().player.getGameProfile().getName(),
                dossier
        );
    }

    @Override
    public void tick() {
        ticksOpen++;
        if (ticksOpen > 500) {
            this.onClose();
        }
    }

    private record ExplorerEntry(String name, boolean folder, String date) {
        static ExplorerEntry file(String name) {
            return new ExplorerEntry(name, false, "7/8/2026 9:00 PM");
        }

        static ExplorerEntry folder(String name) {
            return new ExplorerEntry(name, true, "7/8/2026 8:59 PM");
        }
    }
}
