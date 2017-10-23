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

import mail.api.mail.IMailAddress;
import mail.api.mail.ITradeStation;
import mail.core.gui.ContainerTile;
import mail.core.gui.slots.SlotFiltered;
import mail.core.gui.slots.SlotForestry;
import mail.core.gui.slots.SlotOutput;
import mail.mail.tiles.TileTrader;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerTrader extends ContainerTile<TileTrader> {

	public ContainerTrader(InventoryPlayer player, TileTrader tile) {
		super(tile, player, 33, 138);

		// Trade good
		this.addSlotToContainer(new SlotForestry(this.tile, ITradeStation.SLOT_TRADEGOOD, 78, 109).blockShift());

		// Exchange
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotForestry(this.tile, ITradeStation.SLOT_EXCHANGE_1 + col + row * 2, 69 + col * 18, 55 + row * 18).blockShift());
			}
		}

		// Stamps
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotFiltered(this.tile, ITradeStation.SLOT_STAMPS_1 + col + row * 2, 15 + col * 18, 37 + row * 18));
			}
		}

		// Letters
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				addSlotToContainer(new SlotFiltered(this.tile, ITradeStation.SLOT_LETTERS_1 + col + row * 2, 15 + col * 18, 73 + row * 18));
			}
		}

		// Buffers
		final int bufCols = 5;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new SlotOutput(this.tile, ITradeStation.SLOT_RECEIVE_BUFFER + col + row * bufCols, 123 + col * 18, 19 + row * 18));
			}
		}

		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < bufCols; col++) {
				addSlotToContainer(new SlotFiltered(this.tile, ITradeStation.SLOT_SEND_BUFFER + col + row * bufCols, 123 + col * 18, 19 + 18 * 4 + row * 18));
			}
		}
	}

	public IMailAddress getAddress() {
		return tile.getAddress();
	}

}
