package cjminecraft.multimeters.init;

import cjminecraft.core.energy.EnergyUtils;
import cjminecraft.core.fluid.FluidUtils;
import cjminecraft.core.inventory.InventoryUtils;
import cjminecraft.multimeters.Multimeters;
import cjminecraft.multimeters.client.gui.GuiMultimeterOverlay;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Multimeters.MODID)
public class MultimetersEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemCraft(PlayerEvent.ItemCraftedEvent event) {
        if (EnergyUtils.hasSupport(event.crafting, null))
            event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_energy") });
        if (InventoryUtils.hasSupport(event.crafting, null))
            event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_item") });
        if (FluidUtils.hasSupport(event.crafting, null))
            event.player.unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_fluid") });
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
        TileEntity te = event.getWorld().getTileEntity(event.getPos());
        if(te == null)
            return;
        if(EnergyUtils.hasSupport(te, null))
            event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_energy") });
        if(InventoryUtils.hasSupport(te, null))
            event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_item") });
        if(FluidUtils.hasSupport(te, null))
            event.getPlayer().unlockRecipes(new ResourceLocation[] { new ResourceLocation(Multimeters.MODID, "multimeter_fluid") });
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent event) {
        if (event.getModID().equals(Multimeters.MODID)) {
            ConfigManager.sync(Multimeters.MODID, Config.Type.INSTANCE);
            GuiMultimeterOverlay.INSTANCE.updatePositions();
        }
    }
}
