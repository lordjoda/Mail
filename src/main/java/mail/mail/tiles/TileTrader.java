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
package mail.mail.tiles;

import java.io.IOException;
import java.sql.SQLException;

import com.google.common.base.Preconditions;
import mail.api.core.IErrorLogic;
import mail.api.mail.IMailAddress;
import mail.api.mail.IStamps;
import mail.api.mail.ITradeStation;
import mail.api.mail.PostManager;
import mail.core.errors.EnumErrorCode;
import mail.core.gui.GuiHandler;
import mail.core.inventory.IInventoryAdapter;
import mail.core.network.PacketBufferForestry;
import mail.core.owner.IOwnedTile;
import mail.core.owner.IOwnerHandler;
import mail.core.owner.OwnerHandler;
import mail.core.tiles.TileBase;
import mail.core.utils.ItemStackUtil;
import mail.core.utils.NetworkUtil;
import mail.mail.MailAddress;
//import mail.mail.TradeStation;
import mail.mail.TradeStationSQL;
import mail.mail.gui.ContainerTradeName;
import mail.mail.gui.ContainerTrader;
import mail.mail.gui.GuiTradeName;
import mail.mail.gui.GuiTrader;
import mail.mail.inventory.InventoryTradeStation;
import mail.mail.network.packets.PacketTraderAddressResponse;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileTrader extends TileBase implements IOwnedTile {
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private IMailAddress address;

	public TileTrader() {
		address = new MailAddress();
		setInternalInventory(new InventoryTradeStation());
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	@Override
	public void openGui(EntityPlayer player, ItemStack heldItem) {
		short data = (short) (isLinked() ? 0 : 1);
		ITradeStation tradeStation = PostManager.postRegistry.getTradeStation(world, address);
		if(tradeStation != null&& tradeStation instanceof TradeStationSQL){
			try {
				((TradeStationSQL) tradeStation).load();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		GuiHandler.openGui(player, this, data);
	}

	@Override
	public void onRemoval() {
		if (isLinked()) {
			PostManager.postRegistry.deleteTradeStation(world, address);
		}
	}

	/* SAVING & LOADING */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound = super.writeToNBT(nbttagcompound);

		NBTTagCompound nbt = new NBTTagCompound();
		address.writeToNBT(nbt);
		nbttagcompound.setTag("address", nbt);

		ownerHandler.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		if (nbttagcompound.hasKey("address")) {
			address = new MailAddress(nbttagcompound.getCompoundTag("address"));
		}
		ownerHandler.readFromNBT(nbttagcompound);
	}

	/* NETWORK */

	@Override
	public void writeData(PacketBufferForestry data) {
		super.writeData(data);
		ownerHandler.writeData(data);
		String addressName = address.getName();
		data.writeString(addressName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readData(PacketBufferForestry data) throws IOException {
		super.readData(data);
		ownerHandler.readData(data);
		String addressName = data.readString();
		if (!addressName.isEmpty()) {
			address = PostManager.postRegistry.getMailAddress(addressName);
		}
	}

	/* UPDATING */

	/**
	 * The trade station should show errors for missing stamps and paper first.
	 * Once it is able to send letters, it should display other error states.
	 */
	@Override
	public void updateServerSide() {

		if (!isLinked() || !updateOnInterval(10)) {
			return;
		}

		IErrorLogic errorLogic = getErrorLogic();

		errorLogic.setCondition(!hasPostageMin(3), EnumErrorCode.NO_STAMPS);
		errorLogic.setCondition(!hasPaperMin(2), EnumErrorCode.NO_PAPER);

		IInventory inventory = getInternalInventory();
		ItemStack tradeGood = inventory.getStackInSlot(TradeStationSQL.SLOT_TRADEGOOD);
		errorLogic.setCondition(tradeGood.isEmpty(), EnumErrorCode.NO_TRADE);

		boolean hasRequest = hasItemCount(TradeStationSQL.SLOT_EXCHANGE_1, TradeStationSQL.SLOT_EXCHANGE_COUNT, ItemStack.EMPTY, 1);
		errorLogic.setCondition(!hasRequest, EnumErrorCode.NO_TRADE);

		if (!tradeGood.isEmpty()) {
			boolean hasSupplies = hasItemCount(TradeStationSQL.SLOT_SEND_BUFFER, TradeStationSQL.SLOT_SEND_BUFFER_COUNT, tradeGood, tradeGood.getCount());
			errorLogic.setCondition(!hasSupplies, EnumErrorCode.NO_SUPPLIES);
		}

		if (inventory instanceof TradeStationSQL && updateOnInterval(200)) {
			boolean canReceivePayment = ((TradeStationSQL) inventory).canReceivePayment();
			errorLogic.setCondition(!canReceivePayment, EnumErrorCode.NO_SPACE_INVENTORY);
		}
	}

	/* STATE INFORMATION */
	public boolean isLinked() {
		if (!address.isValid()) {
			return false;
		}

		IErrorLogic errorLogic = getErrorLogic();

		return !errorLogic.contains(EnumErrorCode.NOT_ALPHANUMERIC) && !errorLogic.contains(EnumErrorCode.NOT_UNIQUE);
	}

	/**
	 * Returns true if there are 'itemCount' of 'items' in the inventory
	 * wildcard when items == null, counts all types of items
	 */
	private boolean hasItemCount(int startSlot, int countSlots, ItemStack item, int itemCount) {
		int count = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = startSlot; i < startSlot + countSlots; i++) {
			ItemStack itemInSlot = tradeInventory.getStackInSlot(i);
			if (itemInSlot.isEmpty()) {
				continue;
			}
			if (item.isEmpty() || ItemStackUtil.isIdenticalItem(itemInSlot, item)) {
				count += itemInSlot.getCount();
			}
			if (count >= itemCount) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the percentage of the inventory that is occupied by 'items'
	 * if items == null, returns the percentage occupied by all kinds of items
	 */
	private float percentOccupied(int startSlot, int countSlots, ItemStack item) {
		int count = 0;
		int total = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = startSlot; i < startSlot + countSlots; i++) {
			ItemStack itemInSlot = tradeInventory.getStackInSlot(i);
			if (itemInSlot.isEmpty()) {
				total += tradeInventory.getInventoryStackLimit();
			} else {
				total += itemInSlot.getMaxStackSize();
				if (item.isEmpty() || ItemStackUtil.isIdenticalItem(itemInSlot, item)) {
					count += itemInSlot.getCount();
				}
			}
		}

		return (float) count / (float) total;
	}

	public boolean hasPaperMin(int count) {
		return hasItemCount(TradeStationSQL.SLOT_LETTERS_1, TradeStationSQL.SLOT_LETTERS_COUNT, new ItemStack(Items.PAPER), count);
	}

	public boolean hasInputBufMin(float percentage) {
		IInventory inventory = getInternalInventory();
		ItemStack tradeGood = inventory.getStackInSlot(TradeStationSQL.SLOT_TRADEGOOD);
		if (tradeGood.isEmpty()) {
			return true;
		}
		return percentOccupied(TradeStationSQL.SLOT_SEND_BUFFER, TradeStationSQL.SLOT_SEND_BUFFER_COUNT, tradeGood) > percentage;
	}

	public boolean hasOutputBufMin(float percentage) {
		return percentOccupied(TradeStationSQL.SLOT_RECEIVE_BUFFER, TradeStationSQL.SLOT_RECEIVE_BUFFER_COUNT, ItemStack.EMPTY) > percentage;
	}

	public boolean hasPostageMin(int postage) {

		int posted = 0;

		IInventory tradeInventory = this.getInternalInventory();
		for (int i = TradeStationSQL.SLOT_STAMPS_1; i < TradeStationSQL.SLOT_STAMPS_1 + TradeStationSQL.SLOT_STAMPS_COUNT; i++) {
			ItemStack stamp = tradeInventory.getStackInSlot(i);
			if (!stamp.isEmpty()) {
				if (stamp.getItem() instanceof IStamps) {
					posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.getCount();
					if (posted >= postage) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/* ADDRESS */
	public IMailAddress getAddress() {
		return address;
	}

	public void handleSetAddressRequest(String addressName) {
		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		setAddress(address);

		IMailAddress newAddress = getAddress();
		String newAddressName = newAddress.getName();
		if (newAddressName.equals(addressName)) {
			PacketTraderAddressResponse packetResponse = new PacketTraderAddressResponse(this, addressName);
			NetworkUtil.sendNetworkPacket(packetResponse, pos, world);
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleSetAddressResponse(String addressName) {
		IMailAddress address = PostManager.postRegistry.getMailAddress(addressName);
		setAddress(address);
	}

	private void setAddress(IMailAddress address) {
		Preconditions.checkNotNull(address, "address must not be null");

		if (this.address.isValid() && this.address.equals(address)) {
			return;
		}

		if (!world.isRemote) {
			IErrorLogic errorLogic = getErrorLogic();

			boolean hasValidTradeAddress = PostManager.postRegistry.isValidTradeAddress(world, address);
			errorLogic.setCondition(!hasValidTradeAddress, EnumErrorCode.NOT_ALPHANUMERIC);

			boolean hasUniqueTradeAddress = PostManager.postRegistry.isAvailableTradeAddress(world, address);
			errorLogic.setCondition(!hasUniqueTradeAddress, EnumErrorCode.NOT_UNIQUE);

			if (hasValidTradeAddress & hasUniqueTradeAddress) {
				this.address = address;
				PostManager.postRegistry.getOrCreateTradeStation(world, getOwnerHandler().getOwner(), address);
			}
		} else {
			this.address = address;
		}
	}

	@Override
	public IInventoryAdapter getInternalInventory() {
		// Handle client side
		if (world.isRemote || !address.isValid()) {
			return super.getInternalInventory();
		}
		return PostManager.postRegistry.getOrCreateTradeStation(world, getOwnerHandler().getOwner(), address);
	}

	// TODO: Buildcraft for 1.9
//	@Optional.Method(modid = "BuildCraftAPI|statements")
//	@Override
//	public Collection<ITriggerExternal> getExternalTriggers(EnumFacing side, TileEntity tile) {
//		LinkedList<ITriggerExternal> res = new LinkedList<>();
//		res.add(MailTriggers.lowPaper64);
//		res.add(MailTriggers.lowPaper32);
//		res.add(MailTriggers.lowInput25);
//		res.add(MailTriggers.lowInput10);
//		res.add(MailTriggers.lowPostage40);
//		res.add(MailTriggers.lowPostage20);
//		res.add(MailTriggers.highBuffer90);
//		res.add(MailTriggers.highBuffer75);
//		return res;
//	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, int data) {
		if (data == 0) {
			return new GuiTrader(player.inventory, this);
		} else {
			return new GuiTradeName(this);
		}
	}

	@Override
	public Container getContainer(EntityPlayer player, int data) {
		if (data == 0) {
			return new ContainerTrader(player.inventory, this);
		} else {
			return new ContainerTradeName(this);
		}
	}
}
