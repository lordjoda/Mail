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

import mail.core.network.MailPacket;
import mail.core.network.IForestryPacketClient;
import mail.core.network.IForestryPacketHandlerClient;
import mail.core.network.PacketBufferForestry;
import mail.core.network.PacketIdClient;
import mail.core.tiles.IItemStackDisplay;
import mail.core.tiles.TileForestry;
import mail.core.tiles.TileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketItemStackDisplay extends MailPacket implements IForestryPacketClient {
	private final BlockPos pos;
	private final ItemStack itemStack;

	public <T extends TileForestry & IItemStackDisplay> PacketItemStackDisplay(T tile, ItemStack itemStack) {
		this.pos = tile.getPos();
		this.itemStack = itemStack;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeBlockPos(pos);
		data.writeItemStack(itemStack);
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.ITEMSTACK_DISPLAY;
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			BlockPos pos = data.readBlockPos();
			ItemStack itemStack = data.readItemStack();

			TileUtil.actOnTile(player.world, pos, IItemStackDisplay.class, tile -> tile.handleItemStackForDisplay(itemStack));
		}
	}
}
