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
package mail;

import com.google.common.base.Preconditions;
import mail.api.core.MailAPI;
import mail.core.EventHandlerCore;
import mail.core.config.Config;
import mail.core.config.Constants;
import mail.core.errors.EnumErrorCode;
import mail.core.errors.ErrorStateRegistry;
import mail.core.gui.GuiHandler;
import mail.core.network.PacketHandler;
import mail.core.proxy.Proxies;
import mail.core.utils.MigrationHelper;
import mail.plugins.PluginManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.File;

/**
 * BungeeMail Minecraft Mod
 *
 * @author SirSengir
 */
@Mod(
		modid = Constants.MOD_ID,
		name = Constants.MOD_NAME,
		version = Constants.VERSION,
		guiFactory = "mail.core.config.MailGuiConfigFactory",
		acceptedMinecraftVersions = "[1.11]",
		dependencies = "required-after:forge@[13.20.0.2270,);"
				+ "after:jei@[4.5.0,);")
public class BungeeMail {

	@SuppressWarnings("NullableProblems")
	@Mod.Instance(Constants.MOD_ID)
	public static BungeeMail instance;
	@Nullable
	private File configFolder;

	public BungeeMail() {
		MailAPI.instance = this;
		MailAPI.mailConstants = new Constants();
		MailAPI.errorStateRegistry = new ErrorStateRegistry();
		EnumErrorCode.init();
		FluidRegistry.enableUniversalBucket();
	}

	@Nullable
	private static PacketHandler packetHandler;

	public static PacketHandler getPacketHandler() {
		Preconditions.checkState(packetHandler != null);
		return packetHandler;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		packetHandler = new PacketHandler();

		// Register event handler
		EventHandlerCore eventHandlerCore = new EventHandlerCore();
		MinecraftForge.EVENT_BUS.register(eventHandlerCore);
		MinecraftForge.EVENT_BUS.register(Config.class);
		//Proxies.common.registerEventHandlers();

		configFolder = new File(event.getModConfigurationDirectory(), Constants.MOD_ID);
		Config.load(event.getSide());

		PluginManager.runSetup(event);

		PluginManager.runPreInit(event.getSide());

		Proxies.render.registerModels();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		PluginManager.runInit();

		Proxies.render.registerItemAndBlockColors();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PluginManager.runPostInit();


		// Handle IMC messages.
		PluginManager.processIMCMessages(FMLInterModComms.fetchRuntimeMessages(MailAPI.instance));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		PluginManager.serverStarting(event.getServer());
	}

	@Nullable
	public File getConfigFolder() {
		return configFolder;
	}

	@EventHandler
	public void processIMCMessages(IMCEvent event) {
		PluginManager.processIMCMessages(event.getMessages());
	}

	@EventHandler
	public void onMissingMappings(FMLMissingMappingsEvent event) {
		MigrationHelper.onMissingMappings(event);
	}
}
