/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorManager extends ICraftingProvider<IFabricatorRecipe> {

	void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern);

}
