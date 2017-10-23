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
package mail.core.gui;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import mail.api.core.IErrorLogicSource;
import mail.api.core.IErrorState;
import mail.core.network.packets.PacketErrorUpdate;
import mail.core.tiles.TileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class ContainerTile<T extends TileEntity> extends ContainerForestry {
	protected final T tile;
	@Nullable
	private ImmutableSet<IErrorState> previousErrorStates;
	private int previousEnergyManagerData = 0;
	private int previousWorkCounter = 0;
	private int previousTicksPerWorkCycle = 0;

	protected ContainerTile(T tile) {
		this.tile = tile;
	}

	protected ContainerTile(T tileForestry, InventoryPlayer playerInventory, int xInv, int yInv) {
		this(tileForestry);

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return true;
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return TileUtil.isUsableByPlayer(entityplayer, tile);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		if (tile instanceof IErrorLogicSource) {
			IErrorLogicSource errorLogicSource = (IErrorLogicSource) tile;
			ImmutableSet<IErrorState> errorStates = errorLogicSource.getErrorLogic().getErrorStates();

			if (previousErrorStates == null || !errorStates.equals(previousErrorStates)) {
				PacketErrorUpdate packet = new PacketErrorUpdate(tile, errorLogicSource);
				sendPacketToListeners(packet);
			}

			previousErrorStates = errorStates;
		}

	}


}
