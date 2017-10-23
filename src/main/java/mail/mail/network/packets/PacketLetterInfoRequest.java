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
package mail.mail.network.packets;

import java.io.IOException;

import mail.api.mail.EnumAddressee;
import mail.core.network.MailPacket;
import mail.core.network.IForestryPacketHandlerServer;
import mail.core.network.IForestryPacketServer;
import mail.core.network.PacketBufferForestry;
import mail.core.network.PacketIdServer;
import mail.mail.gui.ContainerLetter;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketLetterInfoRequest extends MailPacket implements IForestryPacketServer {
	private final String recipientName;
	private final EnumAddressee addressType;

	public PacketLetterInfoRequest(String recipientName, EnumAddressee addressType) {
		this.recipientName = recipientName;
		this.addressType = addressType;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.LETTER_INFO_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeString(recipientName);
		data.writeEnum(addressType, EnumAddressee.values());
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			String recipientName = data.readString();
			EnumAddressee addressType = data.readEnum(EnumAddressee.values());

			if (player.openContainer instanceof ContainerLetter) {
				ContainerLetter containerLetter = (ContainerLetter) player.openContainer;
				containerLetter.handleRequestLetterInfo(player, recipientName, addressType);
			}
		}
	}
}
