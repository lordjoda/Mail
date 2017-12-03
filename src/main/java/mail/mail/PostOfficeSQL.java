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

import mail.api.mail.*;
import mail.core.utils.Log;
import mail.mail.items.EnumStampDefinition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class PostOfficeSQL extends SQLSavedData implements IPostOffice {

    // / CONSTANTS
    public static final String SAVE_NAME = "ForestryMail";
    private final int[] collectedPostage = new int[EnumPostage.values().length];
    private LinkedHashMap<IMailAddress, ITradeStation> activeTradeStations = new LinkedHashMap<>();
    private PreparedStatement loadStatement;
    private PreparedStatement saveStatement;
    private PreparedStatement getTradeStationQuery;

    // CONSTRUCTORS
    public PostOfficeSQL(Connection connection) throws SQLException {
        super("PostOffice", SAVE_NAME, connection);
        load();
        save();
    }

    @SuppressWarnings("unused")
//	public PostOfficeSQL(Connection connection,String s) {
//		super("PostOfficeSQL",s,connection);
//	}

    public void setWorld(World world) {
        refreshActiveTradeStations(world);
    }

    protected void setupStatements() throws SQLException {

        String query = "SELECT ID, Value FROM " + tableName;
        loadStatement = connection.prepareStatement(query);
        String saveQuery = "REPLACE into " + tableName + " (ID,Value) VALUES ";
        for (int i = 0; i < EnumPostage.values().length; i++) {
            saveQuery += "(" + i + ",?), ";
        }
        if (saveQuery.endsWith(", ")) {
            saveQuery = saveQuery.substring(0, saveQuery.length() - ", ".length());
        }
        saveStatement = connection.prepareStatement(saveQuery);
        String tradeStationQuery = "SELECT Address FROM " + TradeStationSQL.TABLE_NAME_TRADE_STATION + ";";
        getTradeStationQuery = connection.prepareStatement(tradeStationQuery);

    }

    public void load() throws SQLException {


        ResultSet resultSet = loadStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("ID");
            int value = resultSet.getInt("Value");
            if (EnumPostage.values().length > id) {
                collectedPostage[id] = value;
            }
        }
        resultSet.close();
    }

    public void save() throws SQLException {
        for (int i = 0; i < collectedPostage.length; i++) {
            saveStatement.setInt(i + 1, collectedPostage[i]);
        }
        saveStatement.execute();
    }
//
//    @Override
//    public void readFromNBT(NBTTagCompound nbttagcompound) {
//        for (int i = 0; i < collectedPostage.length; i++) {
//            if (nbttagcompound.hasKey("CPS" + i)) {
//                collectedPostage[i] = nbttagcompound.getInteger("CPS" + i);
//            }
//        }
//    }
//
//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
//        for (int i = 0; i < collectedPostage.length; i++) {
//            nbttagcompound.setInteger("CPS" + i, collectedPostage[i]);
//        }
//        return nbttagcompound;
//    }

	/* TRADE STATION MANAGMENT */

    @Override
    public LinkedHashMap<IMailAddress, ITradeStation> getActiveTradeStations(World world) {
        //this should allow up2 date information when required?
        refreshActiveTradeStations(world);
        return this.activeTradeStations;
    }

    private void refreshActiveTradeStations(World world) {
        activeTradeStations = new LinkedHashMap<>();
        try {
            ResultSet resultSet = getTradeStationQuery.executeQuery();
            while (resultSet.next()) {

                MailAddress address = new MailAddress(SQLSavedData.readFormStatement(resultSet, "Address"));
                ITradeStation trade = PostManager.postRegistry.getTradeStation(world, address);
                if (trade == null) {
                    continue;
                }

                registerTradeStation(trade);
            }
        } catch (IOException | SQLException e) {
            Log.error(e.getLocalizedMessage());
        }

//        File worldSave = world.getSaveHandler().getMapFileFromName("dummy");
//        File file = worldSave.getParentFile();
//        if (!file.exists() || !file.isDirectory()) {
//            return;
//        }
//
//        String[] list = file.list();
//        if (list == null) {
//            return;
//        }
//
//        for (String str : list) {
//            if (!str.startsWith(TradeStationSQL.SAVE_NAME)) {
//                continue;
//            }
//            if (!str.endsWith(".dat")) {
//                continue;
//            }
//
//
//        }
    }

    @Override
    public void registerTradeStation(ITradeStation trade) {
        if (!activeTradeStations.containsKey(trade.getAddress())) {
            activeTradeStations.put(trade.getAddress(), trade);
        }
    }

    @Override
    public void deregisterTradeStation(ITradeStation trade) {
        activeTradeStations.remove(trade.getAddress());
    }

    // / STAMP MANAGMENT
    @Override
    public ItemStack getAnyStamp(int max) {
        return getAnyStamp(EnumPostage.values(), max);
    }

    @Override
    public ItemStack getAnyStamp(EnumPostage postage, int max) {
        return getAnyStamp(new EnumPostage[]{postage}, max);
    }

    @Override
    public ItemStack getAnyStamp(EnumPostage[] postages, int max) {
        for (EnumPostage postage : postages) {
            int collected = Math.min(max, collectedPostage[postage.ordinal()]);
            collectedPostage[postage.ordinal()] -= collected;

            if (collected > 0) {
                EnumStampDefinition stampDefinition = EnumStampDefinition.getFromPostage(postage);
                return PluginMail.getItems().stamps.get(stampDefinition, collected);
            }
        }

        return ItemStack.EMPTY;
    }

    // / DELIVERY
    @Override
    public IPostalState lodgeLetter(World world, ItemStack itemstack, boolean doLodge) {
        ILetter letter = PostManager.postRegistry.getLetter(itemstack);
        if (letter == null) {
            return EnumDeliveryState.NOT_MAILABLE;
        }

        if (letter.isProcessed()) {
            return EnumDeliveryState.ALREADY_MAILED;
        }

        if (!letter.isPostPaid()) {
            return EnumDeliveryState.NOT_POSTPAID;
        }

        if (!letter.isMailable()) {
            return EnumDeliveryState.NOT_MAILABLE;
        }

        IPostalState state = EnumDeliveryState.NOT_MAILABLE;
        IMailAddress address = letter.getRecipient();
        if (address != null) {
            IPostalCarrier carrier = PostManager.postRegistry.getCarrier(address.getType());
            if (carrier != null) {
                state = carrier.deliverLetter(world, this, address, itemstack, doLodge);
            }
        }

        if (!state.isOk()) {
            return state;
        }

        collectPostage(letter.getPostage());
        try {
            save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //    markDirty();
        return EnumDeliveryState.OK;

    }

    @Override
    public void collectPostage(NonNullList<ItemStack> stamps) {
        for (ItemStack stamp : stamps) {
            if (stamp == null) {
                continue;
            }

            if (stamp.getItem() instanceof IStamps) {
                EnumPostage postage = ((IStamps) stamp.getItem()).getPostage(stamp);
                collectedPostage[postage.ordinal()] += stamp.getCount();
            }
        }
    }
}
