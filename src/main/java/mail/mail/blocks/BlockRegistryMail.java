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

import mail.core.blocks.BlockRegistry;
import mail.core.items.ItemBlockForestry;

public class BlockRegistryMail extends BlockRegistry {
	public final BlockMail mailbox;
	public final BlockMail tradeStation;
	public final BlockMail stampCollector;

	public BlockRegistryMail() {
		mailbox = new BlockMail(BlockTypeMail.MAILBOX);
		registerBlock(mailbox, new ItemBlockForestry(mailbox), "mailbox");

		tradeStation = new BlockMail(BlockTypeMail.TRADE_STATION);
		registerBlock(tradeStation, new ItemBlockForestry(tradeStation), "trade_station");

		stampCollector = new BlockMail(BlockTypeMail.PHILATELIST);
		registerBlock(stampCollector, new ItemBlockForestry(stampCollector), "stamp_collector");
	}
}
