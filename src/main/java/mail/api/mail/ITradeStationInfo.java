/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.mail;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ITradeStationInfo {
	IMailAddress getAddress();

	GameProfile getOwner();

	ItemStack getTradegood();

	NonNullList<ItemStack> getRequired();

	EnumTradeStationState getState();
}
