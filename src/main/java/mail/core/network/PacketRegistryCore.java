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
package mail.core.network;

import mail.core.network.packets.PacketErrorUpdate;
import mail.core.network.packets.PacketErrorUpdateEntity;
import mail.core.network.packets.PacketFXSignal;
import mail.core.network.packets.PacketGuiLayoutSelect;
import mail.core.network.packets.PacketGuiSelectRequest;
import mail.core.network.packets.PacketGuiUpdate;
import mail.core.network.packets.PacketGuiUpdateEntity;
import mail.core.network.packets.PacketItemStackDisplay;
import mail.core.network.packets.PacketTileStream;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRegistryCore implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.GUI_SELECTION_REQUEST.setPacketHandler(new PacketGuiSelectRequest.Handler());
		}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.ERROR_UPDATE.setPacketHandler(new PacketErrorUpdate.Handler());
		PacketIdClient.ERROR_UPDATE_ENTITY.setPacketHandler(new PacketErrorUpdateEntity.Handler());
		PacketIdClient.GUI_UPDATE.setPacketHandler(new PacketGuiUpdate.Handler());
		PacketIdClient.GUI_UPDATE_ENTITY.setPacketHandler(new PacketGuiUpdateEntity.Handler());
		PacketIdClient.GUI_LAYOUT_SELECT.setPacketHandler(new PacketGuiLayoutSelect.Handler());
		PacketIdClient.TILE_FORESTRY_UPDATE.setPacketHandler(new PacketTileStream.Handler());
		PacketIdClient.ITEMSTACK_DISPLAY.setPacketHandler(new PacketItemStackDisplay.Handler());
		PacketIdClient.FX_SIGNAL.setPacketHandler(new PacketFXSignal.Handler());
	}
}
