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
import mail.core.render.TextureMapForestry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ProxyRenderClient extends ProxyRender {


	@Override
	public void initRendering() {
		TextureManagerForestry textureManagerForestry = TextureManagerForestry.getInstance();
		TextureMapForestry textureMap = textureManagerForestry.getTextureMap();

		Minecraft minecraft = Minecraft.getMinecraft();
		minecraft.renderEngine.loadTickableTexture(TextureManagerForestry.getInstance().getGuiTextureMap(), textureMap);
	}


	@Override
	public void registerModels() {
		ModelManager.getInstance().registerModels();
	}

	@Override
	public void registerItemAndBlockColors() {
		ModelManager.getInstance().registerItemAndBlockColors();
	}

	private static class FluidStateMapper extends StateMapperBase {
		private final ModelResourceLocation fluidLocation;

		public FluidStateMapper(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
			return fluidLocation;
		}
	}

	private static class FluidItemMeshDefinition implements ItemMeshDefinition {
		private final ModelResourceLocation fluidLocation;

		public FluidItemMeshDefinition(ModelResourceLocation fluidLocation) {
			this.fluidLocation = fluidLocation;
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return fluidLocation;
		}
	}
}
