package cjminecraft.multimeters.items;

import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.core.items.ItemMeta;
import cjminecraft.multimeters.enums.EnumMultimeterType;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The multimeter item class
 * @author CJMinecraft
 */
public class ItemMultimeter extends ItemMeta<EnumMultimeterType> {

    /**
     * Initialise the item
     */
    public ItemMultimeter() {
        super(EnumMultimeterType.class); // The enum class the item uses
        setMaxStackSize(1); // Set the max stack size
    }

    /**
     * Clears the multimeter data from the given {@link NBTTagCompound}
     * @param nbt The {@link NBTTagCompound} to remove the data from
     * @return The updated {@link NBTTagCompound}
     */
    @Nullable
    private NBTTagCompound clearNBT(@Nullable NBTTagCompound nbt) {
        if (nbt != null) {
            if (nbt.hasKey("targetPos"))
                nbt.removeTag("targetPos");
            if (nbt.hasKey("targetSide"))
                nbt.removeTag("targetSide");
        }
        return nbt;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) { // If they are sneaking and are not looking at a block
            // Clear the multimeter data from the item
            player.getHeldItem(hand).setTagCompound(clearNBT(player.getHeldItem(hand).getTagCompound()));
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) { // If the player is sneaking and is looking at a block
            TileEntity te = world.getTileEntity(pos); // Get the tile entity at the block they have sneak right-clicked on
            boolean supported = te != null; // Says whether the block has support
            if (!supported) { // If the tile entity is null
                player.getHeldItem(hand).setTagCompound(clearNBT(player.getHeldItem(hand).getTagCompound())); // Clear the multimeter data
                return EnumActionResult.PASS;
            }
            switch (player.getHeldItem(hand).getItemDamage()) {
                case 0:
                    supported = EnergyUtils.hasSupport(te, side);
                    break;
                case 1:
                    supported = InventoryUtils.hasSupport(te, side);
                    break;
                case 2:
                    supported = FluidUtils.hasSupport(te, side);
                    break;
            }
            if (supported) { // If the block is supported
                if (!player.getHeldItem(hand).hasTagCompound()) // If the item has no nbt
                    player.getHeldItem(hand).setTagCompound(new NBTTagCompound()); // Give it some nbt
                NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound(); // Get the item's nbt
                nbt.setTag("targetPos", NBTUtil.createPosTag(pos)); // Set the target pos
                nbt.setString("targetSide", side.getName2()); // Set the target side
                String blockName = world.getBlockState(pos)
                        .getBlock().getPickBlock(world.getBlockState(pos),
                                new RayTraceResult(RayTraceResult.Type.BLOCK, new Vec3d(pos), side, pos), world, pos, player)
                        .getDisplayName(); // Get the name of the block used by the status message
                player.sendStatusMessage(new TextComponentTranslation("item.multimeter.tooltip.blockpos", blockName, pos.getX(), pos.getY(), pos.getZ(), side.getName2()), true);
                return EnumActionResult.SUCCESS;
            }
            else { // If the block was not supported
                player.getHeldItem(hand).setTagCompound(clearNBT(player.getHeldItem(hand).getTagCompound())); // Clear the multimeter data
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        // Add the tooltip
        if (stack.hasTagCompound() && !stack.getTagCompound().isEmpty() && stack.getTagCompound().hasKey("targetPos") && stack.getTagCompound().hasKey("targetSide")) {
            BlockPos pos = NBTUtil.getPosFromTag(stack.getTagCompound().getCompoundTag("targetPos"));
            EnumFacing side = EnumFacing.byName(stack.getTagCompound().getString("targetSide"));
            String blockName = world.getBlockState(pos)
                    .getBlock().getPickBlock(world.getBlockState(pos),
                            new RayTraceResult(RayTraceResult.Type.BLOCK, new Vec3d(pos), side, pos), world, pos, world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false))
                    .getDisplayName();
            tooltip.add(TextFormatting.GREEN + I18n.format("item.multimeter.tooltip.blockpos", blockName, pos.getX(), pos.getY(), pos.getZ(), side.getName2()));
        }
        tooltip.add(I18n.format("item.multimeter.tooltip." + EnumMultimeterType.values()[Math.min(stack.getItemDamage(), EnumMultimeterType.values().length - 1)].getName()));
    }
}
