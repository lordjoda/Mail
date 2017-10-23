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
package mail.core;

import mail.core.config.Constants;
import mail.mail.PluginMail;
import mail.mail.blocks.BlockRegistryMail;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CreativeTabForestry extends CreativeTabs {


	public static final CreativeTabs tabForestry = new CreativeTabForestry(0, Constants.MOD_ID);

	private final int icon;

	private CreativeTabForestry(int icon, String label) {
		super(label);
		this.icon = icon;
	}

	@Override
	public ItemStack getIconItemStack() {
		Item iconItem;
		switch (icon) {
			case 1:
				iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "ffarm"));
				break;
			default:
				iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "fertilizerCompound"));
				break;
		}
		if (iconItem == null) {
			iconItem = Item.getItemFromBlock(PluginMail.getBlocks().mailbox);
		}
		return new ItemStack(iconItem);
	}

	@Override
	public ItemStack getTabIconItem() {
		return getIconItemStack();
	}
}
