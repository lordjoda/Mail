/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package mail.core.recipes;

import mail.api.recipes.IDescriptiveRecipe;
import mail.core.utils.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nullable;

public abstract class RecipeUtil {


	public static void addRecipe(Block block, Object... obj) {
		addRecipe(new ItemStack(block), obj);
	}

	public static void addRecipe(Item item, Object... obj) {
		addRecipe(new ItemStack(item), obj);
	}

	public static void addRecipe(ItemStack itemstack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapedRecipeCustom(itemstack, obj));
	}

	public static void addPriorityRecipe(ItemStack itemStack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(0, new ShapedRecipeCustom(itemStack, obj));
	}

	public static void addShapelessRecipe(Item item, Object... obj) {
		addShapelessRecipe(new ItemStack(item), obj);
	}

	public static void addShapelessRecipe(ItemStack itemstack, Object... obj) {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(itemstack, obj));
	}

	public static void addSmelting(ItemStack res, Item prod, float xp) {
		addSmelting(res, new ItemStack(prod), xp);
	}

	public static void addSmelting(ItemStack res, ItemStack prod, float xp) {
		GameRegistry.addSmelting(res, prod, xp);
	}

	@Nullable
	public static String[][] matches(IDescriptiveRecipe recipe, IInventory inventoryCrafting) {
		NonNullList<NonNullList<ItemStack>> recipeIngredients = recipe.getIngredients();
		NonNullList<String> oreDicts = recipe.getOreDicts();
		int width = recipe.getWidth();
		int height = recipe.getHeight();
		return matches(recipeIngredients, oreDicts, width, height, inventoryCrafting);
	}

	@Nullable
	public static String[][] matches(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, IInventory inventoryCrafting) {
		ItemStack[][] resources = getResources(inventoryCrafting);
		return matches(recipeIngredients, oreDicts, width, height, resources);
	}

	public static ItemStack[][] getResources(IInventory inventoryCrafting) {
		ItemStack[][] resources = new ItemStack[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = i + j * 3;
				resources[i][j] = inventoryCrafting.getStackInSlot(k);
			}
		}
		return resources;
	}

	@Nullable
	public static String[][] matches(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, ItemStack[][] resources) {
		for (int i = 0; i <= 3 - width; i++) {
			for (int j = 0; j <= 3 - height; j++) {
				String[][] resourceDicts = checkMatch(recipeIngredients, oreDicts, width, height, resources, i, j, true);
				if (resourceDicts != null) {
					return resourceDicts;
				}

				resourceDicts = checkMatch(recipeIngredients, oreDicts, width, height, resources, i, j, false);
				if (resourceDicts != null) {
					return resourceDicts;
				}
			}
		}

		return null;
	}

	@Nullable
	private static String[][] checkMatch(NonNullList<NonNullList<ItemStack>> recipeIngredients, NonNullList<String> oreDicts, int width, int height, ItemStack[][] resources, int xInGrid, int yInGrid, boolean mirror) {
		String[][] resourceDicts = new String[3][3];
		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 3; l++) {
				ItemStack resource = resources[k][l];

				int widthIt = k - xInGrid;
				int heightIt = l - yInGrid;
				NonNullList<ItemStack> recipeIngredient = null;
				String oreDict = "";

				if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
					int position;
					if (mirror) {
						position = width - widthIt - 1 + heightIt * width;
					} else {
						position = widthIt + heightIt * width;
					}
					recipeIngredient = recipeIngredients.get(position);
					oreDict = oreDicts.get(position);
				}

				if (!checkIngredientMatch(recipeIngredient, resource)) {
					return null;
				}
				resourceDicts[k][l] = oreDict;
			}
		}

		return resourceDicts;
	}

	private static boolean checkIngredientMatch(@Nullable NonNullList<ItemStack> recipeIngredient, ItemStack resource) {
		if (recipeIngredient == null || recipeIngredient.isEmpty()) {
			return resource.isEmpty();
		}
		for (ItemStack item : recipeIngredient) {
			if (ItemStackUtil.isCraftingEquivalent(item, resource)) {
				return true;
			}
		}
		return false;
	}
}
