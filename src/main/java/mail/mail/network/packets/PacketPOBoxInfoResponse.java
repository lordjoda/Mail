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

import mail.core.network.MailPacket;
import mail.core.network.IForestryPacketClient;
import mail.core.network.IForestryPacketHandlerClient;
import mail.core.network.PacketBufferForestry;
import mail.core.network.PacketIdClient;
import mail.mail.POBoxInfo;
import mail.mail.gui.GuiMailboxInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPOBoxInfoResponse extends MailPacket implements IForestryPacketClient {
	public final POBoxInfo poboxInfo;

	public PacketPOBoxInfoResponse(POBoxInfo info) {
		this.poboxInfo = info;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.POBOX_INFO_RESPONSE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeInt(poboxInfo.playerLetters);
		data.writeInt(poboxInfo.tradeLetters);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			POBoxInfo poboxInfo = new POBoxInfo(data.readInt(), data.readInt());
			GuiMailboxInfo.instance.setPOBoxInfo(player, poboxInfo);
		}
	}
}
