package cjminecraft.multimeters.client.gui;

import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.ISpecialOverlayElement;
import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.multimeters.Multimeters;
import cjminecraft.multimeters.client.gui.overlay.OverlayEnergy;
import cjminecraft.multimeters.client.gui.overlay.OverlayFluid;
import cjminecraft.multimeters.client.gui.overlay.OverlayInventory;
import cjminecraft.multimeters.client.gui.overlay.OverlayMultimeterBase;
import cjminecraft.multimeters.config.MultimetersConfig;
import cjminecraft.multimeters.init.MultimetersItems;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;

import java.util.*;

public class GuiMultimeterOverlay extends GuiOverlay {

    public static GuiMultimeterOverlay INSTANCE;

    private OverlayInventory inventoryOverlay;
    private OverlayEnergy energyOverlay;
    private OverlayFluid fluidOverlay;

    public GuiMultimeterOverlay() {
        super();
        addOverlay(this.inventoryOverlay = new OverlayInventory(this, MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y));
        addOverlay(this.energyOverlay = new OverlayEnergy(this, MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y));
        addOverlay(this.fluidOverlay = new OverlayFluid(this, MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y));
    }

    @Override
    protected void updateElementInformation() {
        super.updateElementInformation();
        updatePositions();
    }

    @Override
    public void drawScreen() {
        if (!InventoryUtils.hasInHotbar(new ItemStack(MultimetersItems.multimeter), this.player, true, true))
            return; // Don't draw if the player doesn't have the item in their hotbar
        super.drawScreen();
    }

    public void updatePositions() {
        if (this.energyOverlay.isEnabled()) {
            this.energyOverlay.setPosition(MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y - this.energyOverlay.getHeight());
            this.fluidOverlay.setPosition(MultimetersConfig.GUI.OFFSET_X + this.energyOverlay.getWidth() + 10, this.height - MultimetersConfig.GUI.OFFSET_Y - this.fluidOverlay.getHeight());
            this.inventoryOverlay.setPosition(this.fluidOverlay.isEnabled() ? MultimetersConfig.GUI.OFFSET_X + this.energyOverlay.getWidth() + this.fluidOverlay.getWidth() + 20 : MultimetersConfig.GUI.OFFSET_X + this.energyOverlay.getWidth() + 10, this.height - MultimetersConfig.GUI.OFFSET_Y - this.inventoryOverlay.getHeight());
            if (this.inventoryOverlay.getPosX() + this.inventoryOverlay.getWidth() > this.width / 3)
                this.inventoryOverlay.setMaxColumns(MultimetersConfig.GUI.ITEM_MAX_COLUMNS - ((this.width / 3) - this.inventoryOverlay.getPosX() + this.inventoryOverlay.getWidth()) / 18);
        } else {
            this.fluidOverlay.setPosition(MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y - this.fluidOverlay.getHeight());
            this.inventoryOverlay.setPosition(this.fluidOverlay.isEnabled() ? MultimetersConfig.GUI.OFFSET_X + this.fluidOverlay.getWidth() + 10 : MultimetersConfig.GUI.OFFSET_X, this.height - MultimetersConfig.GUI.OFFSET_Y - this.inventoryOverlay.getHeight());
            if (this.inventoryOverlay.getPosX() + this.inventoryOverlay.getWidth() > this.width / 3)
                this.inventoryOverlay.setMaxColumns(MultimetersConfig.GUI.ITEM_MAX_COLUMNS - ((this.width / 3) - this.inventoryOverlay.getPosX() + this.inventoryOverlay.getWidth()) / 18);
        }
        this.energyOverlay.energyBar.setSize(MultimetersConfig.GUI.ENERGY_BAR_WIDTH, MultimetersConfig.GUI.ENERGY_BAR_HEIGHT);
    }

    private int getOverlayTextX() {
        int width = 0;
        for (OverlayBase overlay : this.overlays)
            if (overlay.isEnabled() && overlay.isVisible())
                width = Math.max(width, overlay.getPosX() + overlay.getWidth());
        return width;
    }

    @Override
    protected void drawForegroundLayer() {
        super.drawForegroundLayer();

        List<String> overlayText = new ArrayList<>();
        for (OverlayBase overlay : this.overlays) {
            if (overlay.isEnabled() && overlay.isVisible()) {
                for (ElementBase element : overlay.getElements()) {
                    if (element instanceof ISpecialOverlayElement && element.isVisible()) {
                        ((ISpecialOverlayElement) element).drawSpecialLayer();
                        ((ISpecialOverlayElement) element).addOverlayText(overlayText);
                    }
                }
            }
        }
        HashSet<String> newText = Sets.newHashSet(overlayText);
        Iterator<String> iterator = newText.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            this.fontRenderer.drawStringWithShadow(iterator.next(), getOverlayTextX(), this.height - MultimetersConfig.GUI.OFFSET_Y - ((index + 1) * 10), 0xFFFFFF);
            index++;
        }
    }
}
