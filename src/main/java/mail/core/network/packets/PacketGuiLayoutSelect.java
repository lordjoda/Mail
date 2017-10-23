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
package mail.core.network.packets;

import mail.core.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

public class PacketGuiLayoutSelect extends MailPacket implements IForestryPacketClient {
	private final String layoutUid;

	public PacketGuiLayoutSelect(String layoutUid) {
		this.layoutUid = layoutUid;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_LAYOUT_SELECT;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeString(layoutUid);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			String layoutUid = data.readString();

		}
	}
}
