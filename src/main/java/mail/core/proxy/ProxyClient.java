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

import mail.core.models.ModelManager;
import mail.core.render.TextureManagerForestry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyCommon {

	@Override
	public void registerBlock(Block block) {
		ModelManager.getInstance().registerBlockClient(block);
		TextureManagerForestry.getInstance().registerBlock(block);
	}

	@Override
	public void registerItem(Item item) {
		ModelManager.getInstance().registerItemClient(item);
		TextureManagerForestry.getInstance().registerItem(item);
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public double getBlockReachDistance(EntityPlayer entityplayer) {
		if (entityplayer instanceof EntityPlayerSP) {
			return Minecraft.getMinecraft().playerController.getBlockReachDistance();
		} else {
			return 4f;
		}
	}

}
