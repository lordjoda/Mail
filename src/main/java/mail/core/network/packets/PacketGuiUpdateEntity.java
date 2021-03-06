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
import mail.core.network.IStreamableGui;
import mail.core.network.PacketBufferForestry;
import mail.core.network.PacketIdClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketGuiUpdateEntity extends MailPacket implements IForestryPacketClient {
	private final Entity entity;
	private final IStreamableGui streamableGui;

	public PacketGuiUpdateEntity(IStreamableGui streamableGui, Entity entity) {
		this.entity = entity;
		this.streamableGui = streamableGui;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.GUI_UPDATE_ENTITY;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		data.writeEntityById(entity);
		streamableGui.writeGuiData(data);
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			Entity entity = data.readEntityById(player.world);
			if (entity instanceof IStreamableGui) {
				((IStreamableGui) entity).readGuiData(data);
			}
		}
	}
}
