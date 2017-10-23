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
import mail.core.gui.ContainerTile;
import mail.mail.tiles.TileTrader;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerTradeName extends ContainerTile<TileTrader> {

	public ContainerTradeName(TileTrader tile) {
		super(tile);
	}

	public IMailAddress getAddress() {
		return tile.getAddress();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile.isLinked()) {
			for (Object crafter : listeners) {
				if (crafter instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) crafter;
					tile.openGui(player, player.getHeldItemMainhand());
				}
			}
		}
	}
}
