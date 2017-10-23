/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Get the instance from {@link MailAPI#textureManager}.
 */
@SideOnly(Side.CLIENT)
public interface ITextureManager {

	/**
	 * Location of the BungeeMail Gui Texture Map.
	 * Used for binding with {@link TextureManager#bindTexture(ResourceLocation)}
	 */
	ResourceLocation getGuiTextureMap();

	/**
	 * Get a texture atlas sprite that has been registered by BungeeMail, for BungeeMail's Gui Texture Map.
	 */
	TextureAtlasSprite getDefault(String ident);

	/**
	 * Register a sprite with BungeeMail's Gui Texture Map.
	 */
	TextureAtlasSprite registerGuiSprite(ResourceLocation location);
}
