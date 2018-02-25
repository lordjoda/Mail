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
import mail.api.mail.ILetter;
import mail.api.mail.IMailAddress;
import mail.api.mail.PostManager;
import mail.core.inventory.InventoryAdapter;
import mail.core.utils.InventoryUtil;
import mail.core.utils.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class POBoxSQL extends SQLSavedData implements IPOBox {

    public static final String SAVE_NAME = "POBox_";
    public static final short SLOT_SIZE = 84;
    private final World world;
    PreparedStatement loadStatement;
    @Nullable
    private IMailAddress address;
    private final InventoryAdapter letters;
    private PreparedStatement saveStatement;

    public POBoxSQL(Connection connection, IMailAddress address, World world) throws SQLException {
        super("POBox", SAVE_NAME + address, connection);
        this.world = world;
        if (address.getType() != EnumAddressee.PLAYER) {
            throw new IllegalArgumentException("POBox address must be a player");
        }
        letters = new InventoryAdapter(SLOT_SIZE, "Letters").disableAutomation();

        this.address = address;
        try {
            load();
        } catch (SQLException e) {
            Log.warning("Load Failed. Reconnect?", e);
            load();
        }
    }

    public World getWorld() {
        return world;
    }
//	@SuppressWarnings("unused")
    //public POBoxSQL(String savename) {
//		super(savename);
//	}


    @Override
    protected void setupStatements() throws SQLException {
        loadStatement = connection.prepareStatement("SELECT Address ,Inventory FROM " + tableName + " WHERE ID ='" + key + "';");
        saveStatement = connection.prepareStatement("REPLACE INTO  " + tableName + " (ID, Address, Inventory) VALUES (?,?,?);");
    }

    @Override
    public void load() throws SQLException {
        ResultSet resultSet = loadStatement.executeQuery();
        if (resultSet.next()) {

            try {
                letters.clear();
                this.address = new MailAddress(readFormStatement(resultSet, "Address"));
                this.letters.readFromNBT(readFormStatement(resultSet, "Inventory"));
            } catch (IOException e) {
                Log.error(e.getLocalizedMessage(),e);

            }

        } else {
            Log.warning("Else Block Reached");
            //??
        }
        resultSet.close();
    }

    @Override
    public void save() throws SQLException {

        saveStatement.setString(1, key);
        try {
            writeToStatement(address, saveStatement, 2);
            writeToStatement(letters, saveStatement, 3);
            saveStatement.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //
//    @Override
//    public void readFromNBT(NBTTagCompound nbttagcompound) {
//        if (nbttagcompound.hasKey("address")) {
//            this.address = new MailAddressSQL(connection);
//        }
//        letters.readFromNBT(nbttagcompound);
//    }
//
//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
//        if (this.address != null) {
//            NBTTagCompound nbt = new NBTTagCompound();
//            this.address.writeToNBT(nbt);
//            nbttagcompound.setTag("address", nbt);
//        }
//        letters.writeToNBT(nbttagcompound);
//        return nbttagcompound;
//    }

    @Override
    public boolean storeLetter(ItemStack letterstack) {
        ILetter letter = PostManager.postRegistry.getLetter(letterstack);
        Preconditions.checkNotNull(letter, "Letter stack must be a valid letter");
        //update before adding
        try {
            load();
        } catch (SQLException e) {
            Log.warning("Save Failed. Reconnect?",e);
            try {
                load();
            } catch (SQLException e1) {
                Log.error(e1.getLocalizedMessage(),e1);
            }
        }
        // Mark letter as processed
        letter.setProcessed(true);
        letter.invalidatePostage();
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        letter.writeToNBT(nbttagcompound);
        letterstack.setTagCompound(nbttagcompound);

        boolean b = InventoryUtil.tryAddStack(letters, letterstack, true);
        //first add then save
        this.markDirty();
        return b;
    }

    @Override
    public POBoxInfo getPOBoxInfo() {
        int playerLetters = 0;
        int tradeLetters = 0;
        for (int i = 0; i < letters.getSizeInventory(); i++) {
            if (letters.getStackInSlot(i).isEmpty()) {
                continue;
            }
            NBTTagCompound tagCompound = letters.getStackInSlot(i).getTagCompound();
            if (tagCompound != null) {
                ILetter letter = new Letter(tagCompound);
                if (letter.getSender().getType() == EnumAddressee.PLAYER) {
                    playerLetters++;
                } else {
                    tradeLetters++;
                }
            }
        }

        return new POBoxInfo(playerLetters, tradeLetters);
    }

    /* IINVENTORY */

    @Override
    public boolean isEmpty() {
        return letters.isEmpty();
    }

    @Override
    public void markDirty() {
//        super.markDirty();
        letters.markDirty();
        try {
            save();
        } catch (SQLException e) {
            Log.warning("Save Failed. Reconnect?",e);
            try {
                save();
            } catch (SQLException e1) {
                Log.error(e1.getLocalizedMessage(),e1);
            }

        }
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        letters.setInventorySlotContents(var1, var2);
        this.markDirty();
    }

    @Override
    public int getSizeInventory() {
        return letters.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return letters.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        return letters.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return letters.removeStackFromSlot(index);
    }

    @Override
    public String getName() {
        return letters.getName();
    }

    @Override
    public int getInventoryStackLimit() {
        return letters.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return letters.isUsableByPlayer(var1);
    }

    @Override
    public void openInventory(EntityPlayer var1) {
    }

    @Override
    public void closeInventory(EntityPlayer var1) {
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return letters.isItemValidForSlot(i, itemstack);
    }

    @Override
    public ITextComponent getDisplayName() {
        return letters.getDisplayName();
    }

    /* FIELDS */
    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

}
