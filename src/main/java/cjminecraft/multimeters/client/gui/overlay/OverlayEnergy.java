package cjminecraft.multimeters.client.gui.overlay;

import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementEnergyBar;
import cjminecraft.core.config.CJCoreConfig;
import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.multimeters.Multimeters;
import cjminecraft.multimeters.config.MultimetersConfig;
import cjminecraft.multimeters.init.MultimetersItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OverlayEnergy extends OverlayMultimeterBase {

    public ElementEnergyBar energyBar;

    /**
     * Initialise a new {@link OverlayMultimeterBase}
     *
     * @param gui  The parent gui
     * @param posX The x position of this overlay
     * @param posY The y position of this overlay
     */
    public OverlayEnergy(GuiOverlay gui, int posX, int posY) {
        super(gui, posX, posY);
        addElement(this.energyBar = new ElementEnergyBar(gui, 0, 0, MultimetersConfig.GUI.ENERGY_BAR_WIDTH, MultimetersConfig.GUI.ENERGY_BAR_HEIGHT));
    }

    @Override
    public void updateOverlay(ItemStack inventory) {
        super.updateOverlay(inventory);
        if (inventory.isEmpty()) {
            // Targeting a block
            if (this.energyBar.getPos() != this.targetPos || this.energyBar.getSide() != this.targetSide) {
                this.energyBar.shouldSync(this.targetPos, this.targetSide);
            }
        } else {
            // Targeting an item
            this.energyBar.shouldntSync();
            this.energyBar.setEnergy(EnergyUtils.getEnergyStored(inventory, null, CJCoreConfig.ENERGY.DEFAULT_ENERGY_UNIT), EnergyUtils.getCapacity(inventory, null, CJCoreConfig.ENERGY.DEFAULT_ENERGY_UNIT), CJCoreConfig.ENERGY.DEFAULT_ENERGY_UNIT);
        }
        this.targetedBlock.setPosition(0, this.energyBar.getHeight());
    }

    @Override
    public boolean hasSupport(World world, BlockPos pos, EnumFacing side) {
        return EnergyUtils.hasSupport(world.getTileEntity(pos), side);
    }

    @Override
    public boolean hasSupport(ItemStack stack) {
        return EnergyUtils.hasSupport(stack, null);
    }

    @Override
    public boolean hasMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.hasInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 0), player, true, false);
    }

    @Override
    public ItemStack findMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.findInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 0), player, true, false);
    }
}
