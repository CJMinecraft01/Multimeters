package cjminecraft.multimeters.init;

import cjminecraft.core.util.registries.AutomaticRegistrar;
import cjminecraft.core.util.registries.Register;
import cjminecraft.multimeters.Multimeters;
import cjminecraft.multimeters.enums.EnumMultimeterType;
import cjminecraft.multimeters.items.ItemMultimeter;

/**
 * Handles all of {@link Multimeters} items
 * 
 * @author CJMinecraft
 *
 */
@Register(modid = Multimeters.MODID)
public class MultimetersItems {

	@Register.RegisterItem(registryName = "multimeter")
	@Register.RegisterRender(variantPrefix = "multimeter_", variantEnum = EnumMultimeterType.class)
	public static ItemMultimeter multimeter;

}
