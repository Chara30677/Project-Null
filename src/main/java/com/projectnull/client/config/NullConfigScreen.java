package com.projectnull.client.config;

import com.projectnull.client.horror.HorrorStyle;
import com.projectnull.config.NullClientConfig;
import com.projectnull.config.NullConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NullConfigScreen extends Screen {
    private final Screen parent;

    private boolean clientFourthWall;
    private boolean serverFourthWall;
    private boolean nullSpawning;
    private boolean creepySigns;
    private boolean realLocation;
    private int joinDays;

    private EditBox spawnChanceBox;
    private EditBox joinDaysBox;

    public NullConfigScreen(Screen parent) {
        super(Component.literal("NULL Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        loadFromConfig();

        int panelWidth = 340;
        int panelX = (this.width - panelWidth) / 2;
        int y = Math.max(36, (this.height - 320) / 2);
        int row = 0;

        addRenderableWidget(CycleButton.onOffBuilder(clientFourthWall)
                .create(panelX + 180, y + row * 24, 140, 20, Component.literal("Client horror"), (button, value) -> clientFourthWall = value));
        row += 2;

        addRenderableWidget(CycleButton.onOffBuilder(serverFourthWall)
                .create(panelX + 180, y + row * 24, 140, 20, Component.literal("Server horror"), (button, value) -> serverFourthWall = value));
        row++;

        addRenderableWidget(CycleButton.onOffBuilder(nullSpawning)
                .create(panelX + 180, y + row * 24, 140, 20, Component.literal("Null spawning"), (button, value) -> nullSpawning = value));
        row++;

        addRenderableWidget(CycleButton.onOffBuilder(creepySigns)
                .create(panelX + 180, y + row * 24, 140, 20, Component.literal("Creepy signs"), (button, value) -> creepySigns = value));
        row++;

        addRenderableWidget(CycleButton.onOffBuilder(realLocation)
                .create(panelX + 180, y + row * 24, 140, 20, Component.literal("Real location"), (button, value) -> realLocation = value));
        row += 2;

        spawnChanceBox = new EditBox(this.font, panelX + 180, y + row * 24, 140, 20, Component.literal("Spawn chance"));
        spawnChanceBox.setValue(String.format("%.4f", NullConfig.spawnChance()));
        spawnChanceBox.setHint(Component.literal("0.0001 - 1.0"));
        addRenderableWidget(spawnChanceBox);
        row++;

        joinDaysBox = new EditBox(this.font, panelX + 180, y + row * 24, 140, 20, Component.literal("Join days"));
        joinDaysBox.setValue(Integer.toString(joinDays));
        joinDaysBox.setHint(Component.literal("Days until Null joins"));
        addRenderableWidget(joinDaysBox);
        row += 2;

        addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(panelX + 36, y + row * 24 + 8, 120, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(parent))
                .bounds(panelX + 184, y + row * 24 + 8, 120, 20)
                .build());
    }

    private void loadFromConfig() {
        clientFourthWall = NullClientConfig.fourthWallEnabled();
        serverFourthWall = NullConfig.fourthWallEnabled();
        nullSpawning = NullConfig.nullSpawningEnabled();
        creepySigns = NullConfig.creepySignsEnabled();
        realLocation = NullConfig.useRealLocationData();
        joinDays = Math.max(0, NullConfig.nullJoinAfterTicks() / 24000);
    }

    private void saveAndClose() {
        try {
            double spawnChance = Double.parseDouble(spawnChanceBox.getValue().trim());
            spawnChance = Math.max(0.0, Math.min(1.0, spawnChance));
            int days = Integer.parseInt(joinDaysBox.getValue().trim());
            days = Math.max(0, Math.min(1000, days));

            NullClientConfig.ENABLE_FOURTH_WALL.set(clientFourthWall);
            NullConfig.ENABLE_FOURTH_WALL.set(serverFourthWall);
            NullConfig.ENABLE_NULL_SPAWNING.set(nullSpawning);
            NullConfig.ENABLE_CREEPY_SIGNS.set(creepySigns);
            NullConfig.USE_REAL_LOCATION_DATA.set(realLocation);
            NullConfig.SPAWN_CHANCE.set(spawnChance);
            NullConfig.NULL_JOIN_AFTER_TICKS.set(days * 24000);

            NullClientConfig.SPEC.save();
            NullConfig.SPEC.save();
        } catch (NumberFormatException ignored) {
        }

        this.minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xE0101018);

        int panelWidth = 340;
        int panelHeight = 300;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = Math.max(28, (this.height - panelHeight) / 2);

        graphics.fill(panelX - 2, panelY - 2, panelX + panelWidth + 2, panelY + panelHeight + 2, 0xFFAA2222);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1A1A1A);
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + 28, 0xFF2B2B2B);

        graphics.drawCenteredString(this.font, "NULL // Settings", this.width / 2, panelY + 9, HorrorStyle.ON_DARK_PRIMARY);
        graphics.drawString(this.font, "Client", panelX + 16, panelY + 40, HorrorStyle.ON_DARK_ACCENT);
        graphics.drawString(this.font, "Gameplay", panelX + 16, panelY + 88, HorrorStyle.ON_DARK_ACCENT);
        graphics.drawString(this.font, "Numbers", panelX + 16, panelY + 184, HorrorStyle.ON_DARK_ACCENT);
        graphics.drawString(this.font, "Spawn chance", panelX + 16, panelY + 208, HorrorStyle.ON_DARK_SECONDARY);
        graphics.drawString(this.font, "Days until Null joins", panelX + 16, panelY + 232, HorrorStyle.ON_DARK_SECONDARY);
        graphics.drawString(this.font, "Client toggles only affect this machine.", panelX + 16, panelY + panelHeight - 18, HorrorStyle.ON_DARK_DIM);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
