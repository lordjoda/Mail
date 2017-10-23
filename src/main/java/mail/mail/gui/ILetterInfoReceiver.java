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

import javax.annotation.Nullable;

import mail.api.mail.EnumAddressee;
import mail.api.mail.IMailAddress;
import mail.api.mail.ITradeStationInfo;

public interface ILetterInfoReceiver {
	void handleLetterInfoUpdate(EnumAddressee type, @Nullable IMailAddress address, @Nullable ITradeStationInfo tradeInfo);
}
