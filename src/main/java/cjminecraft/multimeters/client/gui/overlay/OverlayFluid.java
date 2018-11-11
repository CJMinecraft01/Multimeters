package cjminecraft.multimeters.client.gui.overlay;

import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.client.gui.element.ElementFluidBar;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.multimeters.config.MultimetersConfig;
import cjminecraft.multimeters.init.MultimetersItems;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.ArrayList;
import java.util.List;

public class OverlayFluid extends OverlayMultimeterBase {

    private List<ElementFluidBar> fluidBars;

    private BlockPos pos;
    private EnumFacing side;

    /**
     * Initialise a new {@link OverlayMultimeterBase}
     *
     * @param gui  The parent gui
     * @param posX The x position of this overlay
     * @param posY The y position of this overlay
     */
    public OverlayFluid(GuiOverlay gui, int posX, int posY) {
        super(gui, posX, posY);
        this.fluidBars = new ArrayList<>();
    }

    @Override
    public void updateOverlay(ItemStack inventory) {
        super.updateOverlay(inventory);
        if (inventory.isEmpty()) {
            // Targeting a block

            if ((this.fluidBars.size() > 0 && !this.fluidBars.get(0).shouldSync()) || this.pos != null && !this.pos.equals(this.targetPos) || this.side != this.targetSide) {
                this.pos = this.targetPos;
                this.side = this.targetSide;

                int numberOfTanks = FluidUtils.getNumberOfTanks(this.gui.mc.world.getTileEntity(this.pos), this.side);
                if (this.fluidBars.size() == numberOfTanks) {
                    for (int i = 0; i < numberOfTanks; i++) {
                        this.fluidBars.set(i, this.fluidBars.get(i).shouldSync(this.pos, this.side));
                    }
                } else {
                    this.fluidBars.clear();
                    for (int i = 0; i < numberOfTanks; i++)
                        this.fluidBars.add(new ElementFluidBar(this.gui, i * MultimetersConfig.GUI.FLUID_BAR_WIDTH, 0, MultimetersConfig.GUI.FLUID_BAR_WIDTH, MultimetersConfig.GUI.FLUID_BAR_HEIGHT, i).shouldSync(this.pos, this.side));
                }
            }
        } else {
            // Targeting an item
            int numberOfTanks = FluidUtils.getNumberOfTanks(inventory, null);
            if (this.fluidBars.size() == numberOfTanks) {
                for (int i = 0; i < numberOfTanks; i++)
                    this.fluidBars.set(i, this.fluidBars.get(i).shouldntSync().setFluidTankInfo(new FluidTankInfo(FluidUtils.getFluidStack(inventory, null, i),
                            FluidUtils.getCapacity(inventory, null, i))));
            } else {
                this.fluidBars.clear();
                for (int i = 0; i < numberOfTanks; i++)
                    this.fluidBars.add(new ElementFluidBar(this.gui, i * MultimetersConfig.GUI.FLUID_BAR_WIDTH, 0, MultimetersConfig.GUI.FLUID_BAR_WIDTH, MultimetersConfig.GUI.FLUID_BAR_HEIGHT, i).setFluidTankInfo(new FluidTankInfo(FluidUtils.getFluidStack(inventory, null, i),
                            FluidUtils.getCapacity(inventory, null, i))));
            }
        }
        this.targetedBlock.setPosition(0, MultimetersConfig.GUI.FLUID_BAR_HEIGHT);
    }

    @Override
    public boolean hasSupport(TileEntity te, EnumFacing side) {
        return FluidUtils.hasSupport(te, side);
    }

    @Override
    public boolean hasSupport(ItemStack stack) {
        return FluidUtils.hasSupport(stack, null);
    }

    @Override
    public boolean hasMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.hasInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 2), player, true, false);
    }

    @Override
    public ItemStack findMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.findInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 2), player, true, false);
    }

    @Override
    public void update() {
        showOverlayText(this.fontRenderer.getStringWidth(this.targetedBlock.getStack().getDisplayName()) + 20 > this.fluidBars.size() * MultimetersConfig.GUI.FLUID_BAR_WIDTH);

        for (ElementFluidBar fluidBar : this.fluidBars)
            fluidBar.update();

        super.update();
    }

    @Override
    public void drawBackground() {
        super.drawBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0);

        for (int i = this.fluidBars.size(); i-- > 0; ) {
            ElementBase element = this.fluidBars.get(i);
            if (element.isVisible() && element.isEnabled())
                element.drawBackground(0, 0, 0);
        }

        if (this.fontRenderer.getStringWidth(this.targetedBlock.getStack().getDisplayName()) + 20 <= this.fluidBars.size() * MultimetersConfig.GUI.FLUID_BAR_WIDTH)
            this.fontRenderer.drawStringWithShadow(this.targetedBlock.getStack().getDisplayName(), 20, this.fluidBars.size() * MultimetersConfig.GUI.FLUID_BAR_WIDTH + 4.5F, 0xFFFFFF);

        GlStateManager.popMatrix();
    }

    @Override
    public OverlayBase calculateSize() {
        super.calculateSize();
        for (ElementFluidBar element : this.fluidBars) {
            this.width = Math.max(this.width, element.getPosX() + element.getWidth() + 8);
            this.height = Math.max(this.height, element.getPosY() + element.getHeight() + 8);
        }
        return this;
    }

    @Override
    public List<ElementBase> getElements() {
        List<ElementBase> elements = new ArrayList<>(super.getElements());
        elements.addAll(this.fluidBars);
        return elements;
    }
}
