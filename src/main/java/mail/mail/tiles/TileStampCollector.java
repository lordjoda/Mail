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
package mail.mail.tiles;

import mail.api.mail.IStamps;
import mail.api.mail.PostManager;
import mail.core.inventory.IInventoryAdapter;
import mail.core.tiles.TileBase;
import mail.core.utils.InventoryUtil;
import mail.mail.gui.ContainerStampCollector;
import mail.mail.gui.GuiStampCollector;
import mail.mail.inventory.InventoryStampCollector;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileStampCollector extends TileBase implements IInventory {
	public TileStampCollector() {
		setInternalInventory(new InventoryStampCollector(this));
	}

	@Override
	public void updateServerSide() {
		if (!updateOnInterval(20)) {
			return;
		}

		ItemStack stamp = null;

		IInventoryAdapter inventory = getInternalInventory();
		if (inventory.getStackInSlot(InventoryStampCollector.SLOT_FILTER).isEmpty()) {
			stamp = PostManager.postRegistry.getPostOffice(world).getAnyStamp(1);
		} else {
			ItemStack filter = inventory.getStackInSlot(InventoryStampCollector.SLOT_FILTER);
			if (filter.getItem() instanceof IStamps) {
				stamp = PostManager.postRegistry.getPostOffice(world).getAnyStamp(((IStamps) filter.getItem()).getPostage(filter), 1);
			}
		}

		if (stamp == null) {
			return;
		}

		// Store it.
		InventoryUtil.stowInInventory(stamp, inventory, true, InventoryStampCollector.SLOT_BUFFER_1, InventoryStampCollector.SLOT_BUFFER_COUNT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		return new GuiStampCollector(player.inventory, this);
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		return new ContainerStampCollector(player.inventory, this);
	}
}
