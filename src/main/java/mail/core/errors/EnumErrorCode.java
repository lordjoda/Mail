/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.core.errors;

import mail.api.core.MailAPI;
import mail.api.core.IErrorState;
import mail.core.config.Constants;
import mail.core.render.TextureManagerForestry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumErrorCode implements IErrorState {

	// Trade Station
	NO_STAMPS("no_stamps"), // The trade station requires more stamps to pay postage.
	NO_PAPER("no_paper"), // The trade station requires more paper to send letters.
	NO_SUPPLIES("no_supplies", "no_resource"), // The trade station requires more supplies to send.
	NO_TRADE("no_trade", "no_resource"), // The trade station requires items to Send and Receive.
	NO_SPACE_INVENTORY("no_space"), // Empty this machine's inventory.

	// Trade Station naming
	NOT_ALPHANUMERIC("not_alpha_numeric"), // A Trade Station name must consist of letters and numbers only.
	NOT_UNIQUE("not_unique"), // Trade Station names must be unique and this name is already taken.

	// Letters
	NOT_POST_PAID("not_postpaid", "no_stamps"), // Apply more stamps to pay the postal service.
	NO_RECIPIENT("no_recipient"), // You need to address your letter to a recipient to send it.

	;

	private final String name;
	private final String iconName;
	@SideOnly(Side.CLIENT)
	private TextureAtlasSprite texture;

	EnumErrorCode(String name) {
		this(name, name);
	}

	EnumErrorCode(String name, String iconName) {
		this.name = name;
		this.iconName = iconName;
	}

	@Override
	public String getUnlocalizedDescription() {
		return "for.errors." + name + ".desc";
	}

	@Override
	public String getUnlocalizedHelp() {
		return "for.errors." + name + ".help";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprite() {
		ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "gui/errors/" + iconName);
		texture = TextureManagerForestry.getInstance().registerGuiSprite(location);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getSprite() {
		return texture;
	}

	@Override
	public short getID() {
		return (short) ordinal();
	}

	@Override
	public String getUniqueName() {
		return Constants.MOD_ID + ":" + name;
	}

	public static void init() {
		for (IErrorState code : values()) {
			MailAPI.errorStateRegistry.registerErrorState(code);
		}
	}
}
