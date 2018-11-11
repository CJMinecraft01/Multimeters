package cjminecraft.multimeters.config;

import cjminecraft.core.client.gui.element.ElementEnergyBar;
import cjminecraft.core.client.gui.element.ElementFluidBar;
import cjminecraft.multimeters.Multimeters;
import net.minecraftforge.common.config.Config;

@Config(modid = Multimeters.MODID, category = "")
public class MultimetersConfig {

    @Config.LangKey("gui.config.category.gui")
    public static Gui GUI = new Gui();

    public static class Gui {

        @Config.Name("OffsetX")
        @Config.LangKey("gui.config.gui.offset_x")
        @Config.Comment({"The offset of the multimeter gui", "from the left hand side of the screen"})
        @Config.RangeInt(min = 0)
        public int OFFSET_X = 6;
        @Config.Name("OffsetY")
        @Config.LangKey("gui.config.gui.offset_y")
        @Config.Comment({"The offset of the multimeter gui", "from the bottom side of the screen"})
        @Config.RangeInt(min = 0)
        public int OFFSET_Y = 7;
        @Config.Name("ItemMaxColumns")
        @Config.LangKey("gui.config.gui.item_max_columns")
        @Config.Comment({"The maximum number of columns per row", "for the item multimeter"})
        @Config.RangeInt(min = 1)
        public int ITEM_MAX_COLUMNS = 9;
        @Config.Name("EnergyBarWidth")
        @Config.LangKey("gui.config.gui.energy_bar_width")
        @Config.Comment({"The width of the energy bar", "for the energy multimeter"})
        @Config.RangeInt(min = 1, max = ElementEnergyBar.DEFAULT_WIDTH)
        public int ENERGY_BAR_WIDTH = ElementEnergyBar.DEFAULT_WIDTH;
        @Config.Name("EnergyBarHeight")
        @Config.LangKey("gui.config.gui.energy_bar_height")
        @Config.Comment({"The height of the energy bar", "for the energy multimeter"})
        @Config.RangeInt(min = 1, max = ElementEnergyBar.DEFAULT_HEIGHT)
        public int ENERGY_BAR_HEIGHT = ElementEnergyBar.DEFAULT_HEIGHT;
        @Config.Name("FluidBarWidth")
        @Config.LangKey("gui.config.gui.fluid_bar_width")
        @Config.Comment({"The width of the fluid bar", "for the fluid multimeter"})
        @Config.RangeInt(min = 1, max = ElementFluidBar.DEFAULT_WIDTH)
        public int FLUID_BAR_WIDTH = ElementFluidBar.DEFAULT_WIDTH;
        @Config.Name("FluidBarHeight")
        @Config.LangKey("gui.config.gui.fluid_bar_height")
        @Config.Comment({"The height of the fluid bar", "for the fluid multimeter"})
        @Config.RangeInt(min = 1, max = ElementFluidBar.DEFAULT_HEIGHT)
        public int FLUID_BAR_HEIGHT = ElementFluidBar.DEFAULT_HEIGHT;
    }

}
