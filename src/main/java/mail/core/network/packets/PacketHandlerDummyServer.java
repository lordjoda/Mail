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

import java.io.IOException;

import mail.core.network.IForestryPacketHandlerServer;
import mail.core.network.PacketBufferForestry;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketHandlerDummyServer extends PacketHandlerDummy implements IForestryPacketHandlerServer {
	public static final PacketHandlerDummyServer instance = new PacketHandlerDummyServer();

	private PacketHandlerDummyServer() {

	}

	@Override
	public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {

	}
}
