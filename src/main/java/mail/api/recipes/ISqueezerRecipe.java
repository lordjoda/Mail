/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public interface ISqueezerRecipe extends IForestryRecipe {

	/**
	 * @return items stacks representing the required resources for one process. Stack size will be taken into account.
	 */
	NonNullList<ItemStack> getResources();

	/**
	 * @return Number of work cycles required to squeeze one set of resources.
	 */
	int getProcessingTime();

	/**
	 * @return Item stack representing the possible remnants from this recipe. (i.e. tin left over from tin cans)
	 */
	ItemStack getRemnants();

	/**
	 * @return Chance remnants will be produced by a single recipe cycle, from 0 to 1.
	 */
	float getRemnantsChance();

	/**
	 * @return {@link FluidStack} representing the output of this recipe.
	 */
	FluidStack getFluidOutput();

}
