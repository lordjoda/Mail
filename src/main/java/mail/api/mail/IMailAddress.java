/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.mail;

import com.mojang.authlib.GameProfile;
import mail.api.core.INbtWritable;

public interface IMailAddress extends INbtWritable {

	EnumAddressee getType();

	String getName();

	boolean isValid();

	GameProfile getPlayerProfile();
}
