package cjminecraft.multimeters.init;

import cjminecraft.core.util.registries.Register;
import cjminecraft.multimeters.Multimeters;
import cjminecraft.multimeters.items.ItemMultimeter;

/**
 * Handles all of {@link Multimeters} items
 * 
 * @author CJMinecraft
 *
 */
@Register(modid = Multimeters.MODID)
public class MultimetersItems {

	@Register.RegisterItem(registryName = "multimeter", setUnlocalizedName = true, unlocalizedName = "multimeter")
	@Register.RegisterRender(hasVariants = true, variants = { "multimeter_energy", "multimeter_item", "multimeter_fluid" })
	public static ItemMultimeter multimeter;

}
