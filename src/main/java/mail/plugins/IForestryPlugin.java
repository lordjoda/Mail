package mail.plugins;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import mail.core.IPickupHandler;
import mail.core.IResupplyHandler;
import mail.core.ISaveEventHandler;
import mail.core.network.IPacketRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public interface IForestryPlugin {
	boolean isAvailable();

	boolean canBeDisabled();

	String getFailMessage();

	/**
	 * See MailPlugin.pluginID()
	 */
	Set<String> getDependencyUids();

	void setupAPI();

	void disabledSetupAPI();

	void registerItemsAndBlocks();

	void preInit();

	void registerTriggers();

	void registerBackpackItems();

	void registerCrates();

	void doInit();

	void registerRecipes();

	void addLootPoolNames(Set<String> lootPoolNames);

	void postInit();

	boolean processIMCMessage(FMLInterModComms.IMCMessage message);

	void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated);

	void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ);

	void getHiddenItems(List<ItemStack> hiddenItems);

	@Nullable
	ISaveEventHandler getSaveEventHandler();

	@Nullable
	IPacketRegistry getPacketRegistry();

	@Nullable
	IResupplyHandler getResupplyHandler();

	@Nullable
	ICommand[] getConsoleCommands();

	@Nullable
	IFuelHandler getFuelHandler();
}
