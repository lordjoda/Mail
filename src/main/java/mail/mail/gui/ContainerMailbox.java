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
package mail.mail.gui;

import javax.annotation.Nullable;

import mail.core.gui.ContainerTile;
import mail.core.gui.slots.SlotOutput;
import mail.core.inventory.watchers.ISlotChangeWatcher;
import mail.core.utils.NetworkUtil;
import mail.core.utils.SlotUtil;
import mail.mail.IPOBox;
import mail.mail.POBoxInfo;
import mail.mail.POBoxSQL;
import mail.mail.network.packets.PacketPOBoxInfoResponse;
import mail.mail.tiles.TileMailbox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.sql.SQLException;

public class ContainerMailbox extends ContainerTile<TileMailbox> implements ISlotChangeWatcher {

    public static final short SLOT_LETTERS = 0;
    public static final short SLOT_LETTERS_COUNT = 7 * 12;
    @Nullable
    private final IPOBox mailInventory;

    public ContainerMailbox(InventoryPlayer playerInventory, TileMailbox tile) {
        super(tile, playerInventory, 35, 145);
        IInventory inventory = tile.getOrCreateMailInventory(playerInventory.player.world, playerInventory.player.getGameProfile());

        if (inventory instanceof IPOBox) {
            this.mailInventory = (IPOBox) inventory;
        } else {
            this.mailInventory = null;
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 12; j++) {
                SlotOutput slotOutput = new SlotOutput(inventory, j + i * 9, 8 + j * 18, 8 + i * 18);
                slotOutput.setChangeWatcher(this);
                addSlotToContainer(slotOutput);
            }
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType_or_button, ClickType clickTypeIn, EntityPlayer player) {
        System.out.println("klick");
        update();
        ItemStack stack = super.slotClick(slotId, dragType_or_button, clickTypeIn, player);

        if (SlotUtil.isSlotInRange(slotId, SLOT_LETTERS, SLOT_LETTERS_COUNT)) {
            if (!player.world.isRemote && mailInventory != null) {
                POBoxInfo info = mailInventory.getPOBoxInfo();
                NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(info), player);
            }
        }

        return stack;
    }

    @Override
    public void onSlotChanged(IInventory inventory, int slot) {
        System.out.println("slot changed");
    }

    private void update() {
        if (mailInventory instanceof POBoxSQL) {
            try {
                ((POBoxSQL) mailInventory).load();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
