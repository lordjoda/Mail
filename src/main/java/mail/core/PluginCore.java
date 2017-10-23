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
package mail.core;

import com.google.common.base.Preconditions;
import mail.core.blocks.BlockRegistryCore;
import mail.core.commands.CommandPlugins;
import mail.core.commands.RootCommand;
import mail.core.items.ItemRegistryCore;
import mail.core.network.IPacketRegistry;
import mail.core.network.PacketRegistryCore;
import mail.core.owner.GameProfileDataSerializer;
import mail.core.proxy.Proxies;
import mail.core.recipes.ShapedRecipeCustom;
import mail.core.utils.MAilModEnvWarningCallable;
import mail.plugins.BlankForestryPlugin;
import mail.plugins.MailPlugin;
import mail.plugins.ForestryPluginUids;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@MailPlugin(pluginID = ForestryPluginUids.CORE, name = "Core", author = "SirSengir", unlocalizedDescription = "for.plugin.core.description")
public class PluginCore extends BlankForestryPlugin {
	public static final RootCommand rootCommand = new RootCommand();
	@Nullable
	public static ItemRegistryCore items;
	@Nullable
	private static BlockRegistryCore blocks;

	public static ItemRegistryCore getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	public static BlockRegistryCore getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public boolean canBeDisabled() {
		return false;
	}

	@Override
	public Set<String> getDependencyUids() {
		return Collections.emptySet();
	}

	@Override
	public void setupAPI() {
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryCore();
		blocks = new BlockRegistryCore();
	}

	@Override
	public void preInit() {
		super.preInit();

		GameProfileDataSerializer.register();

		MinecraftForge.EVENT_BUS.register(this);

		rootCommand.addChildCommand(new CommandPlugins());
}

	@Override
	public void doInit() {
		super.doInit();

		BlockRegistryCore blocks = getBlocks();


		MAilModEnvWarningCallable.register();


		Proxies.render.initRendering();

		RecipeSorter.register("forestry:shaped", ShapedRecipeCustom.class, RecipeSorter.Category.SHAPED, "");
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return new SaveEventHandlerCore();
	}

	@Override
	public void registerCrates() {
		// forestry items
		ItemRegistryCore items = getItems();
		BlockRegistryCore blocks = getBlocks();
	}

	@Override
	public void registerRecipes() {
		BlockRegistryCore blocks = getBlocks();
		ItemRegistryCore items = getItems();

	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryCore();
	}


	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[]{rootCommand};
	}


	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// research note items are not useful without actually having completed research
	}


}
