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

import javax.annotation.Nullable;

import net.minecraft.util.NonNullList;

import mail.api.recipes.IForestryRecipe;
import mail.core.utils.InventoryUtil;

public class RecipePair<R extends IForestryRecipe> {
	
	public static final RecipePair EMPTY = new RecipePair(null, null);
	
	@Nullable
	private final R recipe;
	private final NonNullList<String> oreDictEntries;
	
	public RecipePair(R recipe, String[][] oreDictEntries) {
		this.recipe = recipe;
		this.oreDictEntries = InventoryUtil.getOreDictAsList(oreDictEntries);
	}
	
	public boolean isEmpty(){
		return recipe == null;
	}
	
	public R getRecipe() {
		return recipe;
	}
	
	public NonNullList<String> getOreDictEntries() {
		return oreDictEntries;
	}
}
