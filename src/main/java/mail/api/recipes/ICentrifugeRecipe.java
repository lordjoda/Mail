/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.recipes;

import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ICentrifugeRecipe extends IForestryRecipe {

	/**
	 * The items for this recipe to match against.
	 **/
	ItemStack getInput();

	/**
	 * The time it takes to process one items. Default is 20.
	 **/
	int getProcessingTime();

	/**
	 * Returns the randomized products from processing one input items.
	 **/
	NonNullList<ItemStack> getProducts(Random random);

	/**
	 * Returns a list of all possible products and their estimated probabilities (0.0 to 1.0],
	 * to help mods that display recipes
	 **/
	Map<ItemStack, Float> getAllProducts();
}
