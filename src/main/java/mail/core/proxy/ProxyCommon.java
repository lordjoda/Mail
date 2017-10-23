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
package mail.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import java.io.File;

public class ProxyCommon {
	public void registerItem(Item item) {

	}

	public void registerBlock(Block block) {

	}

	


	public File getForestryRoot() {
		return new File(".");
	}

	public double getBlockReachDistance(EntityPlayer entityplayer) {
		return 4f;
	}

}
