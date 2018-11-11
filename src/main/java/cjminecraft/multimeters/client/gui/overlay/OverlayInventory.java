package cjminecraft.multimeters.client.gui.overlay;

import cjminecraft.core.CJCore;
import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementBase;
import cjminecraft.core.client.gui.element.ElementItemSlot;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.multimeters.config.MultimetersConfig;
import cjminecraft.multimeters.init.MultimetersItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class OverlayInventory extends OverlayMultimeterBase {

    private List<ElementItemSlot> itemSlots;

    private int sync = 0;

    private boolean shouldSync;
    private BlockPos pos;
    private EnumFacing side;
    private boolean stacked;

    private int excessColumns = 0;
    private int rows = 0;
    private int columns = 0;

    private int maxColumns = MultimetersConfig.GUI.ITEM_MAX_COLUMNS;

    public OverlayInventory(GuiOverlay gui, int posX, int posY) {
        super(gui, posX, posY);
        this.itemSlots = new ArrayList<>();
    }

    @Override
    public void updateOverlay(ItemStack inventory) {
        super.updateOverlay(inventory);
        if (inventory.isEmpty()) {
            // Targeting a block
            if ((this.pos != null && !this.pos.equals(this.targetPos)) || this.side != this.targetSide) {
                shouldSync(this.targetPos, this.targetSide, true);
            }
        } else {
            // Targeting an item
            shouldntSync();
            setInventory(this.stacked ? InventoryUtils.getInventoryStacked(inventory, null) : InventoryUtils.getInventory(inventory, null));
        }
        this.targetedBlock.setPosition(0, this.rows * 18 + (this.excessColumns > 0 ? 18 : 0));
    }

    @Override
    public boolean hasSupport(TileEntity te, EnumFacing side) {
        return InventoryUtils.hasSupport(te, side);
    }

    @Override
    public boolean hasSupport(ItemStack stack) {
        return InventoryUtils.hasSupport(stack, null);
    }

    @Override
    public boolean hasMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.hasInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 1), player, true, false);
    }

    @Override
    public ItemStack findMultimeterInHotbar(EntityPlayer player) {
        return InventoryUtils.findInHotbar(new ItemStack(MultimetersItems.multimeter, 1, 1), player, true, false);
    }

    /**
     * Set the inventory to be drawn. Typically get the inventory from
     * {@link InventoryUtils}. If not, use
     * {@link ImmutableList#copyOf(java.util.Collection)}
     *
     * @param inventory The inventory to be drawn
     * @return The updated overlay
     */
    public OverlayInventory setInventory(@Nullable ImmutableList<ItemStack> inventory) {
        if (inventory == null)
            return this;
        if (inventory.size() != 0) {
            int maxColumns = Math.min(MultimetersConfig.GUI.ITEM_MAX_COLUMNS, this.maxColumns);

            setVisible(true);
            this.itemSlots.clear();
            this.rows = inventory.size() / maxColumns;
            if (inventory.size() <= maxColumns) {
                this.rows = 1;
                this.columns = inventory.size();
            } else {
                this.columns = maxColumns;
            }
            this.excessColumns = inventory.size() - (this.rows * this.columns);
            for (int row = 0; row < this.rows; row++)
                for (int column = 0; column < this.columns; column++)
                    this.itemSlots.add((new ElementItemSlot(this.gui, column * 18, row * 18)
                            .setStack(inventory.get(Math.min(row * maxColumns + column, inventory.size() - 1)))));
            for (int column = 0; column < this.excessColumns; column++)
                this.itemSlots.add((new ElementItemSlot(this.gui, column * 18, this.rows * 18)
                        .setStack(inventory.get(Math.min(this.rows * maxColumns + column, inventory.size() - 1)))));
        } else {
            setVisible(false);
        }
        return this;
    }

    /**
     * States that the overlay should sync with the server
     *
     * @param pos     The position of the {@link TileEntity}
     * @param side    The targetSide of the {@link TileEntity} the inventory is found. For
     *                use with {@link ISidedInventory} and {@link Capability}
     * @param stacked Whether the inventory should be "stacked". See
     *                {@link InventoryUtils#getInventoryStacked(TileEntity, EnumFacing)}
     * @return The updated overlay
     */
    public OverlayInventory shouldSync(BlockPos pos, EnumFacing side, boolean stacked) {
        this.shouldSync = true;
        this.pos = pos;
        this.side = side;
        this.stacked = stacked;
        return this;
    }

    /**
     * States that the overlay shouldn't sync with the server
     *
     * @return The updated overlay
     */
    public OverlayInventory shouldntSync() {
        this.shouldSync = false;
        return this;
    }

    @Override
    public void update() {
        if (this.shouldSync) {
            this.sync++;
            this.sync %= 10;
            setInventory(InventoryUtils.getCachedInventoryData(CJCore.MODID));
            if (this.sync == 0) {
                if (this.stacked)
                    InventoryUtils.syncInventoryStacked(this.pos, this.side, CJCore.MODID);
                else
                    InventoryUtils.syncInventory(this.pos, this.side, CJCore.MODID);
            }
        }

        showOverlayText(this.fontRenderer.getStringWidth(this.targetedBlock.getStack().getDisplayName()) + 20 > this.columns * 18);

        super.update();
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.posX, this.posY, 0);

        for (int i = this.itemSlots.size(); i-- > 0; ) {
            ElementBase element = this.itemSlots.get(i);
            if (element.isVisible() && element.isEnabled())
                element.drawBackground(0, 0, 0);
        }

        if (this.fontRenderer.getStringWidth(this.targetedBlock.getStack().getDisplayName()) + 20 <= this.columns * 18)
            this.fontRenderer.drawStringWithShadow(this.targetedBlock.getStack().getDisplayName(), 20, this.rows * 18 + (this.excessColumns > 0 ? 18 : 0) + 4.5F, 0xFFFFFF);

        GlStateManager.popMatrix();
    }

    @Override
    public OverlayBase calculateSize() {
        super.calculateSize();
        for (ElementItemSlot element : this.itemSlots) {
            this.width = Math.max(this.width, element.getPosX() + element.getWidth() + 8);
            this.height = Math.max(this.height, element.getPosY() + element.getHeight() + 8);
        }
        return this;
    }

    /**
     * @return the targetPos in which the energy bar is syncing with if provided
     */
    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }

    /**
     * @return the targetSide of which the energy bar is syncing with if provided
     */
    @Nullable
    public EnumFacing getSide() {
        return this.side;
    }

    /**
     * @return the number of rows of items
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * @return the number of columns of items
     */
    public int getColumns() {
        return this.columns;
    }

    /**
     * @return the number of excess columns of items
     */
    public int getExcessColumns() {
        return this.excessColumns;
    }

    public int getMaxColumns() {
        return this.maxColumns;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }
}
