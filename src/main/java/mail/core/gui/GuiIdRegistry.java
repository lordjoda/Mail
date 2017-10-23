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
package mail.core.gui;

import mail.mail.items.ItemCatalogue;
import mail.mail.items.ItemLetter;
import mail.mail.tiles.TileMailbox;
import mail.mail.tiles.TileStampCollector;
import mail.mail.tiles.TileTrader;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiIdRegistry {
	private static final Map<Class<? extends IGuiHandlerForestry>, GuiId> classMap = new HashMap<>();
	private static final Map<Integer, GuiId> idMap = new HashMap<>();
	private static int nextId = 0;

	static {
		registerGuiHandlers(GuiType.Tile, Arrays.asList(

				TileMailbox.class,
				TileStampCollector.class,
				TileTrader.class
		));

		registerGuiHandlers(GuiType.Item, Arrays.asList(
				ItemCatalogue.class,

				ItemLetter.class
		));

	}

	private static void registerGuiHandlers(GuiType guiType, List<Class<? extends IGuiHandlerForestry>> guiHandlerClasses) {
		for (Class<? extends IGuiHandlerForestry> tileGuiHandlerClass : guiHandlerClasses) {
			GuiId guiId = new GuiId(nextId++, guiType, tileGuiHandlerClass);
			classMap.put(tileGuiHandlerClass, guiId);
			idMap.put(guiId.getId(), guiId);
		}
	}

	public static GuiId getGuiIdForGuiHandler(IGuiHandlerForestry guiHandler) {
		Class<? extends IGuiHandlerForestry> guiHandlerClass = guiHandler.getClass();
		GuiId guiId = classMap.get(guiHandlerClass);
		if (guiId == null) {
			for (Map.Entry<Class<? extends IGuiHandlerForestry>, GuiId> classGuiIdEntry : classMap.entrySet()) {
				if (classGuiIdEntry.getKey().isAssignableFrom(guiHandlerClass)) {
					guiId = classGuiIdEntry.getValue();
					break;
				}
			}
		}
		if (guiId == null) {
			throw new IllegalStateException("No gui ID for gui handler: " + guiHandler);
		}
		return guiId;
	}

	@Nullable
	public static GuiId getGuiId(int id) {
		return idMap.get(id);
	}
}
