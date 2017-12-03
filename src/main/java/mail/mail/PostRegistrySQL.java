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

import com.mojang.authlib.GameProfile;
import mail.api.mail.*;
import mail.core.config.Config;
import mail.core.utils.Log;
import mail.core.utils.NetworkUtil;
import mail.core.utils.PlayerUtil;
import mail.mail.network.packets.PacketPOBoxInfoResponse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PostRegistrySQL implements IPostRegistry {
    private static Connection connection;
    //    @Nullable
    public PostOfficeSQL cachedPostOffice;
    public final Map<IMailAddress, POBoxSQL> cachedPOBoxes = new HashMap<>();
    public final Map<IMailAddress, TradeStationSQL> cachedTradeStations = new HashMap<>();
    private int tick = 0;

    public PostRegistrySQL(Connection connection) {
        MinecraftForge.EVENT_BUS.register(this);

        this.connection = connection;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            tick++;
            if (tick % Config.ticksForUpdate == 0) {
            tick = 0;
                //update stuff
                cachedPOBoxes.forEach((iMailAddress, poBoxSQL) -> {
                    try {
                        poBoxSQL.load();
                        EntityPlayer player = PlayerUtil.getPlayer(poBoxSQL.getWorld(), iMailAddress.getPlayerProfile());
                        if (player != null) {
                            NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(poBoxSQL.getPOBoxInfo()), player);
                        }
                    } catch (SQLException e) {


                    }
                });
            }
        }
    }

    private final Map<EnumAddressee, IPostalCarrier> carriers = new EnumMap<>(EnumAddressee.class);

    /**
     * @param world   the Minecraft world the PO box will be in
     * @param address the potential address of the PO box
     * @return true if the passed address is valid for PO Boxes.
     */
    @Override
    public boolean isValidPOBox(World world, IMailAddress address) {
        return address.getType() == EnumAddressee.PLAYER && address.getName().matches("^[a-zA-Z0-9]+$");
    }

    @Nullable
    public POBoxSQL getPOBox(World world, IMailAddress address) {

        if (cachedPOBoxes.containsKey(address)) {
            POBoxSQL poBox = cachedPOBoxes.get(address);

            return poBox;
        }
        POBoxSQL pobox = null;
        try {
            pobox = new POBoxSQL(connection, address,world);
            cachedPOBoxes.put(address, pobox);
        } catch (SQLException e) {
            Log.error(e.getLocalizedMessage());
        }



        return pobox;
    }

    @Override
    public void clearPostOffice() {
        cachedPostOffice = null;
    }

    @Override
    public void clearPoBoxes() {
        cachedPOBoxes.clear();
    }

    @Override
    public void clearTradeStations() {
        cachedTradeStations.clear();
    }


    public IPOBox getOrCreatePOBox(World world, IMailAddress address) {

        POBoxSQL pobox = getPOBox(world, address);

        if (pobox == null) {
            try {
                pobox = new POBoxSQL(connection, address, world);
                pobox.markDirty();
                EntityPlayer player = PlayerUtil.getPlayer(world, address.getPlayerProfile());
                if (player != null) {
                    NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo()), player);
                }
            } catch (SQLException e) {
                Log.error(e.getLocalizedMessage());

                e.printStackTrace();
            }
        }

        return pobox;
    }

    /**
     * @param world   the Minecraft world the Trader will be in
     * @param address the potential address of the Trader
     * @return true if the passed address can be an address for a trade station
     */
    @Override
    public boolean isValidTradeAddress(World world, IMailAddress address) {
        return address.getType() == EnumAddressee.TRADER && address.getName().matches("^[a-zA-Z0-9]+$");
    }

    /**
     * @param world   the Minecraft world the Trader will be in
     * @param address the potential address of the Trader
     * @return true if the trade address has not yet been used before.
     */
    @Override
    public boolean isAvailableTradeAddress(World world, IMailAddress address) {
        return getTradeStation(world, address) == null;
    }

    @Override
    public TradeStationSQL getTradeStation(World world, IMailAddress address) {

        if (cachedTradeStations.containsKey(address)) {
            TradeStationSQL tradeStationSQL = (TradeStationSQL) cachedTradeStations.get(address);

            return tradeStationSQL;
        }

        TradeStationSQL trade = null;
        try {
            trade = new TradeStationSQL(connection, address);
        } catch (SQLException e) {
            Log.error(e.getLocalizedMessage());
        }
        // Only existing and valid mail orders are returned
        if (trade != null && trade.isValid()) {
            cachedTradeStations.put(address, trade);
            getPostOffice(world).registerTradeStation(trade);
            return trade;
        }

        return null;
    }

    @Override
    public TradeStationSQL getOrCreateTradeStation(World world, GameProfile owner, IMailAddress address) {
        TradeStationSQL trade = getTradeStation(world, address);

        if (trade == null) {
            try {
                trade = new TradeStationSQL(connection, owner, address);
                trade.markDirty();
                cachedTradeStations.put(address, trade);
                getPostOffice(world).registerTradeStation(trade);
            } catch (SQLException e) {
                Log.error(e.getMessage());
                e.printStackTrace();
            }
        }

        return trade;
    }

    @Override
    public void deleteTradeStation(World world, IMailAddress address) {
        TradeStationSQL trade = getTradeStation(world, address);
        if (trade == null) {
            return;
        }

        // Need to be marked as invalid since WorldSavedData seems to do some caching of its own.
        trade.invalidate();
        getPostOffice(world).deregisterTradeStation(trade);
        trade.delete();

    }

    @Override
    public IPostOffice getPostOffice(World world) {
        if (cachedPostOffice != null) {
            return cachedPostOffice;
        }

//        PostOffice office = (PostOffice) world.loadData(PostOffice.class, PostOffice.SAVE_NAME);
        PostOfficeSQL office = null;
        try {
            office = new PostOfficeSQL(connection);
        } catch (SQLException e) {
            Log.error(e.getLocalizedMessage());
        }
//            world.setData(PostOffice.SAVE_NAME, office);

        cachedPostOffice = office;

        office.setWorld(world);

        return office;
    }


    @Override
    public IMailAddress getMailAddress(GameProfile gameProfile) {
        return new MailAddress(gameProfile);
    }

    @Override
    public IMailAddress getMailAddress(String traderName) {
        return new MailAddress(traderName);
    }

    /* CARRIER */
    @Override
    public Map<EnumAddressee, IPostalCarrier> getRegisteredCarriers() {
        return carriers;
    }

    @Override
    public void registerCarrier(IPostalCarrier carrier) {
        carriers.put(carrier.getType(), carrier);
    }

    @Override
    public IPostalCarrier getCarrier(EnumAddressee type) {
        return carriers.get(type);
    }

    /* LETTERS */
    @Override
    public ILetter createLetter(IMailAddress sender, IMailAddress recipient) {
        return new Letter(sender, recipient);
    }

    @Override
    public ItemStack createLetterStack(ILetter letter) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        letter.writeToNBT(nbttagcompound);

        ItemStack letterStack = LetterProperties.createStampedLetterStack(letter);
        letterStack.setTagCompound(nbttagcompound);

        return letterStack;
    }

    @Override
    @Nullable
    public ILetter getLetter(ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return null;
        }

        if (!PostManager.postRegistry.isLetter(itemstack)) {
            return null;
        }

        if (itemstack.getTagCompound() == null) {
            return null;
        }

        return new Letter(itemstack.getTagCompound());
    }

    @Override
    public boolean isLetter(ItemStack itemstack) {
        return itemstack.getItem() == PluginMail.getItems().letters;
    }
}
