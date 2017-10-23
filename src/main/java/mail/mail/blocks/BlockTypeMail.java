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
package mail.mail.blocks;

import mail.core.blocks.IBlockType;
import mail.core.blocks.IMachineProperties;
import mail.core.blocks.MachineProperties;
import mail.core.tiles.TileForestry;
import mail.mail.tiles.TileMailbox;
import mail.mail.tiles.TileStampCollector;
import mail.mail.tiles.TileTrader;

public enum BlockTypeMail implements IBlockType {
	MAILBOX(TileMailbox.class, "mailbox"),
	TRADE_STATION(TileTrader.class, "trade_station"),
	PHILATELIST(TileStampCollector.class, "stamp_collector");

	public static final BlockTypeMail[] VALUES = values();

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeMail(Class<T> teClass, String name) {
		this.machineProperties = new MachineProperties<>(teClass, name);
	}

	@Override
	public IMachineProperties getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
