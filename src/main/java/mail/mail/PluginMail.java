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
package mail.mail;

import com.google.common.base.Preconditions;
import mail.api.mail.EnumAddressee;
import mail.api.mail.PostManager;
import mail.core.ISaveEventHandler;
import mail.core.PluginCore;
import mail.core.config.Config;
import mail.core.items.ItemRegistryCore;
import mail.core.network.IPacketRegistry;
import mail.core.recipes.RecipeUtil;
import mail.core.utils.Log;
import mail.core.utils.OreDictUtil;
import mail.mail.blocks.BlockRegistryMail;
import mail.mail.commands.CommandMail;
import mail.mail.items.EnumStampDefinition;
import mail.mail.items.ItemRegistryMail;
import mail.mail.network.PacketRegistryMail;
import mail.plugins.BlankForestryPlugin;
import mail.plugins.MailPlugin;
import mail.plugins.ForestryPluginUids;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.sql.Connection;

@MailPlugin(pluginID = ForestryPluginUids.MAIL, name = "Mail", author = "SirSengir, Lord Joda", unlocalizedDescription = "for.plugin.mail.description")
public class PluginMail extends BlankForestryPlugin {
    @Nullable
    private static ItemRegistryMail items;
    @Nullable
    private static BlockRegistryMail blocks;

    public static ItemRegistryMail getItems() {
        Preconditions.checkState(items != null);
        return items;
    }

    public static BlockRegistryMail getBlocks() {
        Preconditions.checkState(blocks != null);
        return blocks;
    }

    @Override
    public void setupAPI() {
//		PostManager.postRegistry = new PostRegistry();
        Connection connection = ConnectionHandler.initConnection();
        if (null != connection) {
        Log.info("Using SQL Mail");
            PostManager.postRegistry = new PostRegistrySQL(connection);
        } else {
            Log.info("Using default Mail");
            PostManager.postRegistry = new PostRegistry();
        }

        PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.PLAYER));
        PostManager.postRegistry.registerCarrier(new PostalCarrier(EnumAddressee.TRADER));
    }

    @Override
    public void registerItemsAndBlocks() {
        items = new ItemRegistryMail();
        blocks = new BlockRegistryMail();
    }

    @Override
    public void preInit() {
        super.preInit();

        PluginCore.rootCommand.addChildCommand(new CommandMail());

        if (Config.mailAlertEnabled) {
            MinecraftForge.EVENT_BUS.register(new EventHandlerMailAlert());
        }
    }

    // TODO: Buildcraft for 1.9
//	@Override
//	public void registerTriggers() {
//		MailTriggers.initialize();
//	}

    @Override
    public void doInit() {
        super.doInit();

        BlockRegistryMail blocks = getBlocks();
        blocks.mailbox.init();
        blocks.tradeStation.init();
        blocks.stampCollector.init();
    }

    @Override
    public IPacketRegistry getPacketRegistry() {
        return new PacketRegistryMail();
    }

    @Override
    public void registerRecipes() {
        ItemRegistryCore coreItems = PluginCore.getItems();
        ItemRegistryMail items = getItems();
        BlockRegistryMail blocks = getBlocks();

        ItemStack stampGlue;
        ItemStack letterGlue;

//		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
//			ItemRegistryApiculture beeItems = PluginApiculture.getItems();
//			stampGlue = beeItems.honeyDrop.getItemStack();
//			letterGlue = beeItems.propolis.getWildcard();
//		} else {
        stampGlue = new ItemStack(Items.SLIME_BALL);
        letterGlue = new ItemStack(Items.SLIME_BALL);
//		}

        RecipeUtil.addShapelessRecipe(items.letters.getItemStack(), Items.PAPER, letterGlue);

        if (Config.craftingStampsEnabled) {
            for (EnumStampDefinition stampDefinition : EnumStampDefinition.VALUES) {
                if (Config.collectorStamps.contains(stampDefinition.getUid())) {
                    continue;
                }

                ItemStack stamps = items.stamps.get(stampDefinition, 9);

                Log.error("RECIPES NOT SET!");
				RecipeUtil.addRecipe(stamps,
						"XXX",
						"###",
						" Z ",
						'X', stampDefinition.getCraftingIngredient(),
						'#', Items.PAPER,
						'Z', stampGlue);
//				RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEED_OIL.getFluid(300), ItemStack.EMPTY, stamps,
//						"XXX",
//						"###",
//						'X', stampDefinition.getCraftingIngredient(),
//						'#', Items.PAPER);
            }
        }

        // Recycling
        RecipeUtil.addRecipe(new ItemStack(Items.PAPER), "###", '#', OreDictUtil.EMPTIED_LETTER_ORE_DICT);

        // Carpenter


//		RecipeManagers.carpenterManager.addRecipe(10,
//				new FluidStack(FluidRegistry.WATER, 250), ItemStack.EMPTY,
//				items.letters.getItemStack(), "###", "###", '#',
//				coreItems.woodPulp);

        RecipeUtil.addShapelessRecipe(items.catalogue.getItemStack(), items.stamps.getWildcard(), new ItemStack(Items.BOOK));
//
		RecipeUtil.addRecipe(new ItemStack(blocks.mailbox),
				" # ",
				" Y ",
				" X ",
				'#',  new ItemStack(Items.SIGN),
				'Y', "chestWood",
				'X', new ItemStack(Blocks.OAK_FENCE));


        RecipeUtil.addRecipe(new ItemStack(blocks.tradeStation),
                " # ",
                " Y ",
                " X ",
                '#',  new ItemStack(Items.BOOK),
                'Y', "chestWood",
                'X', new ItemStack(Blocks.OAK_FENCE));

//		RecipeUtil.addRecipe(new ItemStack(blocks.tradeStation),
//				"Z#Z",
//				"#Y#",
//				"XWX",
//				'#', coreItems.tubes.get(EnumElectronTube.BRONZE, 1),
//				'X', "chestWood",
//				'Y', coreItems.sturdyCasing,
//				'Z', coreItems.tubes.get(EnumElectronTube.IRON, 1),
//				'W',"chestWood");//TODO better recipe
////				'W', ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{}));
    }

    @Override
    public ISaveEventHandler getSaveEventHandler() {
        return new SaveEventHandlerMail();
    }
}
