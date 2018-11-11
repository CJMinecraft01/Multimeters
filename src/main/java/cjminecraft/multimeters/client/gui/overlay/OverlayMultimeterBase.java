package cjminecraft.multimeters.client.gui.overlay;

import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementItemSlot;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * The base multimeter overlay class
 *
 * @author CJMinecraft
 */
public abstract class OverlayMultimeterBase extends OverlayBase {

    ElementItemSlot targetedBlock;
    BlockPos targetPos = null;
    EnumFacing targetSide = null;
    private ItemStack multimeter = ItemStack.EMPTY;

    /**
     * Initialise a new {@link OverlayMultimeterBase}
     *
     * @param gui  The parent gui
     * @param posX The x position of this overlay
     * @param posY The y position of this overlay
     */
    public OverlayMultimeterBase(GuiOverlay gui, int posX, int posY) {
        super(gui, posX, posY);
        setEnabled(false);
        addElement(this.targetedBlock = new ElementItemSlot(gui, 0, 0));
    }

    @Override
    public void updateElementInformation() {
        setEnabled(hasMultimeterInHotbar(this.gui.mc.player));
        if (this.isEnabled()) {
            this.multimeter = findMultimeterInHotbar(this.gui.mc.player);
            if (this.multimeter.hasTagCompound() && this.multimeter.getTagCompound().hasKey("targetPos") && this.multimeter.getTagCompound().hasKey("targetSide")) {
                NBTTagCompound nbt = this.multimeter.getTagCompound();
                this.targetPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("targetPos"));
                this.targetSide = EnumFacing.byName(nbt.getString("targetSide"));
            } else {
                RayTraceResult result = this.gui.mc.objectMouseOver;
                this.targetPos = result.getBlockPos();
                if (this.gui.mc.player.isSneaking()) // TODO Possibly remove?
                    this.targetSide = null; // When sneaking, ignore the targetSide
                else
                    this.targetSide = result.sideHit;
            }

            if (hasSupport(this.gui.mc.player.getHeldItemMainhand()))
                updateOverlay(this.gui.mc.player.getHeldItemMainhand());
            else if (hasSupport(this.gui.mc.player.getHeldItemOffhand()))
                updateOverlay(this.gui.mc.player.getHeldItemOffhand());
            else if (hasSupport(this.gui.mc.world.getTileEntity(this.targetPos), this.targetSide))
                updateOverlay(ItemStack.EMPTY);
            else
                setEnabled(false);

            if (this.isEnabled())
                super.updateElementInformation();
        }
    }

    /**
     * Update the overlay data: e.g. whether to sync with an item or a block
     *
     * @param inventory The item which has the correct support for this multimeter. Will be empty if we are targeting a block
     */
    public void updateOverlay(ItemStack inventory) {
        if (inventory.isEmpty())
            // Targeting a block
            this.targetedBlock.setStack(getStackFromBlock(this.targetPos, this.targetSide));
        else
            // Targeting an item
            this.targetedBlock.setStack(inventory);
    }

    /**
     * Is the given {@link TileEntity} supported by this multimeter?
     *
     * @param te   The {@link TileEntity} to test
     * @param side The side of the {@link TileEntity} to test
     * @return whether the given {@link TileEntity} is supported
     */
    public abstract boolean hasSupport(TileEntity te, EnumFacing side);

    /**
     * Is the given {@link ItemStack} supported by this multimeter?
     *
     * @param stack The {@link ItemStack} to test
     * @return whether the given {@link ItemStack} is supported
     */
    public abstract boolean hasSupport(ItemStack stack);

    /**
     * Does the player have the the multimeter for this gui in their hotbar
     *
     * @param player The {@link EntityPlayer} to test
     * @return whether the player has the right multimeter for this gui in their hotbar
     */
    public abstract boolean hasMultimeterInHotbar(EntityPlayer player);

    /**
     * Find the multimeter for this gui in the player's hotbar (get the actual multimeter item)
     *
     * @param player The {@link EntityPlayer} to test
     * @return the multimeter for this gui from the player's hotbar
     */
    public abstract ItemStack findMultimeterInHotbar(EntityPlayer player);

    /**
     * Get the correct {@link ItemStack} from the given block at the
     * position provided
     *
     * @param pos  The position of the block
     * @param side The targetSide of the block (for use with
     *             {@link Block#getPickBlock(net.minecraft.block.state.IBlockState, RayTraceResult, World, BlockPos, EntityPlayer)}
     * @return The {@link ItemStack} of the block at the position provided
     */
    private ItemStack getStackFromBlock(BlockPos pos, EnumFacing side) {
        Block block = this.gui.mc.world.getBlockState(pos).getBlock();
        return block.getPickBlock(this.gui.mc.world.getBlockState(pos),
                new RayTraceResult(RayTraceResult.Type.BLOCK, new Vec3d(pos), side, pos), this.gui.mc.world, pos, this.gui.mc.player);
    }

    @Override
    public void drawForeground() {
    }
}
