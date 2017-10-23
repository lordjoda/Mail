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
import mail.core.tiles.TileUtil;
import mail.mail.tiles.TileTrader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketTraderAddressResponse extends MailPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final String addressName;

	public PacketTraderAddressResponse(TileTrader tile, String addressName) {
		this.pos = tile.getPos();
		this.addressName = addressName;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.TRADING_ADDRESS_RESPONSE;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeString(addressName);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			String addressName = data.readString();

			TileUtil.actOnTile(player.world, pos, TileTrader.class, tile -> tile.handleSetAddressResponse(addressName));
		}
	}
}