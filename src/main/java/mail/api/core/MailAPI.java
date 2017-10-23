/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

/**
 * BungeeMail's API is divided into several subcategories to make it easier to understand.
 * <p>
 * If you need to distribute API files, try to only include the parts you are actually
 * using to minimize conflicts due to API changes.
 * <p>
 * .core     - Miscallenous base classes and interfaces as well as some basics for tools, armor, game modes and stuff needed by biome mods.
 * .mail     - Anything related to handling letters and adding new mail carrier systems.
 * <p>
 * Note that if BungeeMail is not present, all these references will be null.
 */
public class MailAPI {

	/**
	 * The main mod instance for BungeeMail.
	 */
	public static Object instance;

	/**
	 * A {@link ITextureManager} needed for some things in the API.
	 */
	@SideOnly(Side.CLIENT)
	public static ITextureManager textureManager;

	@SideOnly(Side.CLIENT)
	public static IModelManager modelManager;

	public static IMailConstants mailConstants;
	/**
	 * The currently enabled BungeeMail plugins.
	 * Can be used to check if certain features are available, for example:
	 * MailAPI.enabledPlugins.contains("APICULTURE")
	 */
	public static Set<String> enabledPlugins;

	/**
	 * Instance of the errorStateRegistry for registering errors.
	 * Also creates new instances of IErrorLogic.
	 */
	public static IErrorStateRegistry errorStateRegistry;
}
