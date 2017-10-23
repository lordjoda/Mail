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
package mail.mail.gui;

import mail.core.gui.ContainerTile;
import mail.core.gui.slots.SlotFiltered;
import mail.core.gui.slots.SlotOutput;
import mail.mail.inventory.InventoryStampCollector;
import mail.mail.tiles.TileStampCollector;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerStampCollector extends ContainerTile<TileStampCollector> {

	public ContainerStampCollector(InventoryPlayer player, TileStampCollector tile) {
		super(tile, player, 8, 111);

		// Filter
		addSlotToContainer(new SlotFiltered(tile, InventoryStampCollector.SLOT_FILTER, 80, 19));

		// Collected Stamps
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotOutput(tile, j + i * 9 + InventoryStampCollector.SLOT_BUFFER_1, 8 + j * 18, 46 + i * 18));
			}
		}
	}
}
