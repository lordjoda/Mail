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

import com.mojang.authlib.GameProfile;
import mail.api.mail.ILetter;
import mail.api.mail.IMailAddress;
import mail.api.mail.IPostalState;
import mail.api.mail.PostManager;
import mail.core.inventory.InventoryAdapter;
import mail.core.tiles.TileBase;
import mail.mail.EnumDeliveryState;
import mail.mail.POBoxSQL;
import mail.mail.gui.ContainerMailbox;
import mail.mail.gui.GuiMailbox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileMailbox extends TileBase {

    public TileMailbox() {
        setInternalInventory(new InventoryAdapter(POBoxSQL.SLOT_SIZE, "Letters").disableAutomation());
    }

    /* GUI */
    @Override
    public void openGui(EntityPlayer player, ItemStack heldItem) {
        if (world.isRemote) {
            return;
        }

        // Handle letter sending
        if (PostManager.postRegistry.isLetter(heldItem)) {
            IPostalState result = this.tryDispatchLetter(heldItem);
            if (!result.isOk()) {
                player.sendMessage(new TextComponentString(result.getDescription()));
            } else {
                heldItem.shrink(1);
            }
        } else {
            super.openGui(player, heldItem);
        }
    }

    /* MAIL HANDLING */
    public IInventory getOrCreateMailInventory(World world, GameProfile playerProfile) {
        if (world.isRemote) {
            return getInternalInventory();
        }

        IMailAddress address = PostManager.postRegistry.getMailAddress(playerProfile);
        return PostManager.postRegistry.getOrCreatePOBox(world, address);
    }

    /**
     * server only
     */
    private IPostalState tryDispatchLetter(ItemStack letterStack) {
        ILetter letter = PostManager.postRegistry.getLetter(letterStack);
        IPostalState result;

        if (letter != null) {
            result = PostManager.postRegistry.getPostOffice(world).lodgeLetter(world, letterStack, true);
        } else {
            result = EnumDeliveryState.NOT_MAILABLE;
        }

        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGui(EntityPlayer player, int data) {
        return new GuiMailbox(player.inventory, this);
    }

    @Override
    public Container getContainer(EntityPlayer player, int data) {
        return new ContainerMailbox(player.inventory, this);
    }
}
