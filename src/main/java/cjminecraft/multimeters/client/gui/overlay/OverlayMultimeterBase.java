package cjminecraft.multimeters.client.gui.overlay;

import cjminecraft.core.client.gui.GuiOverlay;
import cjminecraft.core.client.gui.element.ElementItemSlot;
import cjminecraft.core.client.gui.overlay.OverlayBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class OverlayMultimeterBase extends OverlayBase {

    protected ElementItemSlot targetedBlock;
    protected BlockPos targetPos = null;
    protected EnumFacing targetSide = null;
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
            else if (hasSupport(this.gui.mc.world, this.targetPos, this.targetSide))
                updateOverlay(ItemStack.EMPTY);
            else
                setEnabled(false);

            if (this.isEnabled())
                super.updateElementInformation();
        }
    }

    public void updateOverlay(ItemStack inventory) {
        if (inventory.isEmpty())
            // Targeting a block
            this.targetedBlock.setStack(getStackFromBlock(this.targetPos, this.targetSide));
        else
            // Targeting an item
            this.targetedBlock.setStack(inventory);
    }

    public abstract boolean hasSupport(World world, BlockPos pos, EnumFacing side);

    public abstract boolean hasSupport(ItemStack stack);

    public abstract boolean hasMultimeterInHotbar(EntityPlayer player);

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
