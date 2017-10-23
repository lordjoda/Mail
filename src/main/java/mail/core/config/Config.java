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
package mail.core.config;

import com.google.common.collect.LinkedListMultimap;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import net.minecraftforge.common.config.Property;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import mail.BungeeMail;
import mail.core.utils.Log;
import mail.core.utils.Translator;
import mail.mail.gui.GuiMailboxInfo;

public class Config {

	public static final String CATEGORY_COMMON = "common";

	@Nullable
	public static LocalizedConfiguration configCommon;

	public static boolean isDebug = false;

	// Graphics
	public static boolean enableParticleFX = true;
	public static boolean craftingStampsEnabled = true;
	public static final ArrayList<String> collectorStamps = new ArrayList<>();

	// Mail
	public enum MailMode{
		None,
		MYSql
	}
	public static boolean mailAlertEnabled = true;
	public static GuiMailboxInfo.XPosition mailAlertXPosition = GuiMailboxInfo.XPosition.LEFT;
	public static GuiMailboxInfo.YPosition mailAlertYPosition = GuiMailboxInfo.YPosition.TOP;

	public static MailMode SQLMode = MailMode.None;
	public static String serverAddress = "localhost";
	public static int serverPort = 3306;
	public static String userName = "root";
	public static String password = "";
	public static String databaseName = "mail";
	public static int ticksForUpdate;

	// Gui tabs (Ledger)
	public static int guiTabSpeed = 8;

	// Hints
	public static boolean enableHints = true;
	public static final LinkedListMultimap<String, String> hints = LinkedListMultimap.create();
	public static boolean enableEnergyStat = true;



	public static void load(Side side) {
		File configCommonFile = new File(BungeeMail.instance.getConfigFolder(), CATEGORY_COMMON + ".cfg");
		configCommon = new LocalizedConfiguration(configCommonFile, "1.2.0");
		loadConfigCommon(side);


		loadHints();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
		if(!event.getModID().equals(Constants.MOD_ID)){
			return;
		}
		loadConfigCommon(FMLCommonHandler.instance().getSide());
	}

	private static void loadConfigCommon(Side side) {

		boolean recreate = configCommon.getBooleanLocalized("difficulty", "recreate.definitions", true);
		if (recreate) {
			Log.info("Recreating all gamemode definitions from the defaults. This may be caused by an upgrade");

			String recreateDefinitionsComment = Translator.translateToLocal("for.config.difficulty.recreate.definitions.comment");
			Property property = configCommon.get("difficulty", "recreate.definitions", true, recreateDefinitionsComment);
			property.set(false);

		}

		// RetroGen

		craftingStampsEnabled = configCommon.getBooleanLocalized("crafting.stamps", "enabled", true);

		String[] allStamps = new String[]{"1n", "2n", "5n", "10n", "20n", "50n", "100n"};
		String[] defaultCollectors = new String[]{"20n", "50n", "100n"};
		String[] stamps = configCommon.getStringListLocalized("crafting.stamps", "disabled", defaultCollectors, allStamps);
		try {
			collectorStamps.addAll(Arrays.asList(stamps));
		} catch (RuntimeException ex) {
			Log.warning("Failed to read config for 'crafting.stamps.disabled', setting to default.");
			Property property = configCommon.get("crafting.stamps", "disabled", defaultCollectors);
			property.setToDefault();
			collectorStamps.addAll(Arrays.asList(defaultCollectors));
		}

		if (side == Side.CLIENT) {
			mailAlertEnabled = configCommon.getBooleanLocalized("tweaks.gui.mail.alert", "enabled", mailAlertEnabled);
			mailAlertXPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "xPosition", mailAlertXPosition, GuiMailboxInfo.XPosition.values());
			mailAlertYPosition = configCommon.getEnumLocalized("tweaks.gui.mail.alert", "yPosition", mailAlertYPosition, GuiMailboxInfo.YPosition.values());

			guiTabSpeed = configCommon.getIntLocalized("tweaks.gui.tabs", "speed", guiTabSpeed, 1, 50);
			enableHints = configCommon.getBooleanLocalized("tweaks.gui.tabs", "hints", enableHints);
			enableEnergyStat = configCommon.getBooleanLocalized("tweaks.gui.tabs", "energy", enableEnergyStat);

			enableParticleFX = configCommon.getBooleanLocalized("performance", "particleFX", enableParticleFX);
		}
//		else
			{
			SQLMode = configCommon.getEnumLocalized("mail", "mode", SQLMode, MailMode.values());
			serverAddress = configCommon.getStringLocalized("mail", "address", serverAddress);
			serverPort = configCommon.getIntLocalized("mail", "port", serverPort, 0, Short.MAX_VALUE * 2 + 1);
			userName = configCommon.getStringLocalized("mail", "username", userName);
			password = configCommon.getStringLocalized("mail", "password", password);
			databaseName = configCommon.getStringLocalized("mail", "databaseName", databaseName);
			ticksForUpdate = configCommon.getIntLocalized("mail", "ticks", 60 * 20, 1, Integer.MAX_VALUE);
		}

		isDebug = configCommon.getBooleanLocalized("debug", "enabled", isDebug);

		configCommon.save();
	}


	private static void CopyFileToFS(File destination, String resourcePath) {
		InputStream stream = Config.class.getResourceAsStream(resourcePath);
		OutputStream outstream;
		int readBytes;
		byte[] buffer = new byte[4096];
		try {

			if (destination.getParentFile() != null) {
				destination.getParentFile().mkdirs();
			}

			if (!destination.exists() && !destination.createNewFile()) {
				return;
			}

			outstream = new FileOutputStream(destination);
			while ((readBytes = stream.read(buffer)) > 0) {
				outstream.write(buffer, 0, readBytes);
			}
		} catch (FileNotFoundException e) {
			Log.error("File not found.", e);
		} catch (IOException e) {
			Log.error("Failed to copy file.", e);
		}
	}

	private static void loadHints() {

		Properties prop = new Properties();

		try {
			InputStream hintStream = Config.class.getResourceAsStream("/config/forestry/hints.properties");
			prop.load(hintStream);
		} catch (IOException | NullPointerException e) {
			Log.error("Failed to load hints file.", e);
		}

		for (String key : prop.stringPropertyNames()) {
			String[] parsedHints = parseHints(prop.getProperty(key));
			for (String parsedHint : parsedHints) {
				hints.put(key, parsedHint);
			}
		}
	}

	private static String[] parseHints(String list) {
		if (list.isEmpty()) {
			return new String[0];
		} else {
			return list.split("[;]+");
		}
	}
}
