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
import mail.core.network.IForestryPacketHandlerServer;
import mail.core.network.IForestryPacketServer;
import mail.core.network.PacketBufferForestry;
import mail.core.network.PacketIdServer;
import mail.core.tiles.TileUtil;
import mail.mail.tiles.TileTrader;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class PacketTraderAddressRequest extends MailPacket implements IForestryPacketServer {
	private final BlockPos pos;
	private final String addressName;

	public PacketTraderAddressRequest(TileTrader tile, String addressName) {
		this.pos = tile.getPos();
		this.addressName = addressName;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.TRADING_ADDRESS_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeString(addressName);
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			BlockPos pos = data.readBlockPos();
			String addressName = data.readString();

			TileUtil.actOnTile(player.world, pos, TileTrader.class, tile -> tile.handleSetAddressRequest(addressName));
		}
	}
}
