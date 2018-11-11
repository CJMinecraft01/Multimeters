package cjminecraft.multimeters.proxy;

import cjminecraft.multimeters.client.gui.GuiMultimeterOverlay;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(GuiMultimeterOverlay.INSTANCE = new GuiMultimeterOverlay());
    }

    @Override
    public void postInit() {
        super.postInit();
    }
}
