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
package mail.mail.inventory;

import java.util.ArrayList;
import java.util.List;

import mail.api.mail.IStamps;
import mail.api.mail.ITradeStation;
import mail.core.inventory.InventoryAdapter;
import mail.core.utils.ItemStackUtil;
import mail.core.utils.SlotUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class InventoryTradeStation extends InventoryAdapter {

	public InventoryTradeStation() {
		super(ITradeStation.SLOT_SIZE, "INV");
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		List<Integer> slots = new ArrayList<>();

		for (int i = ITradeStation.SLOT_LETTERS_1; i < ITradeStation.SLOT_LETTERS_1 + ITradeStation.SLOT_LETTERS_COUNT; i++) {
			slots.add(i);
		}
		for (int i = ITradeStation.SLOT_STAMPS_1; i < ITradeStation.SLOT_STAMPS_1 + ITradeStation.SLOT_STAMPS_COUNT; i++) {
			slots.add(i);
		}
		for (int i = ITradeStation.SLOT_RECEIVE_BUFFER; i < ITradeStation.SLOT_RECEIVE_BUFFER + ITradeStation.SLOT_RECEIVE_BUFFER_COUNT; i++) {
			slots.add(i);
		}
		for (int i = ITradeStation.SLOT_SEND_BUFFER; i < ITradeStation.SLOT_SEND_BUFFER + ITradeStation.SLOT_SEND_BUFFER_COUNT; i++) {
			slots.add(i);
		}

		int[] slotsInt = new int[slots.size()];
		for (int i = 0; i < slots.size(); i++) {
			slotsInt[i] = slots.get(i);
		}

		return slotsInt;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemStack, EnumFacing side) {
		return SlotUtil.isSlotInRange(slot, ITradeStation.SLOT_RECEIVE_BUFFER, ITradeStation.SLOT_RECEIVE_BUFFER_COUNT);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (SlotUtil.isSlotInRange(slotIndex, ITradeStation.SLOT_SEND_BUFFER, ITradeStation.SLOT_SEND_BUFFER_COUNT)) {
			for (int i = 0; i < ITradeStation.SLOT_TRADEGOOD_COUNT; i++) {
				ItemStack tradeGood = getStackInSlot(ITradeStation.SLOT_TRADEGOOD + i);
				if (ItemStackUtil.isIdenticalItem(tradeGood, itemStack)) {
					return true;
				}
			}
			return false;
		} else if (SlotUtil.isSlotInRange(slotIndex, ITradeStation.SLOT_LETTERS_1, ITradeStation.SLOT_LETTERS_COUNT)) {
			Item item = itemStack.getItem();
			return item == Items.PAPER;
		} else if (SlotUtil.isSlotInRange(slotIndex, ITradeStation.SLOT_STAMPS_1, ITradeStation.SLOT_STAMPS_COUNT)) {
			Item item = itemStack.getItem();
			return item instanceof IStamps;
		}

		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return canSlotAccept(i, itemstack);
	}
}
