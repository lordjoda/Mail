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
import mail.core.render.TextureManagerForestry;
import mail.core.utils.NetworkUtil;
import mail.core.utils.PlayerUtil;
import mail.core.utils.Translator;
import mail.mail.network.packets.PacketPOBoxInfoResponse;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PostalCarrier implements IPostalCarrier {

	private final String iconID;
	private final EnumAddressee type;

	public PostalCarrier(EnumAddressee type) {
		iconID = "mail/carrier." + type;
		this.type = type;
	}

	@Override
	public EnumAddressee getType() {
		return type;
	}

	@Override
	public String getName() {
		return Translator.translateToLocal("for.gui.addressee." + type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSprite() {
		return TextureManagerForestry.getInstance().getDefault(iconID);
	}

	@Override
	public IPostalState deliverLetter(World world, IPostOffice office, IMailAddress recipient, ItemStack letterStack, boolean doDeliver) {
		if (type == EnumAddressee.TRADER) {
			return handleTradeLetter(world, recipient, letterStack, doDeliver);
		} else {
			return storeInPOBox(world, recipient, letterStack);
		}
	}

	private static IPostalState handleTradeLetter(World world, IMailAddress recipient, ItemStack letterStack, boolean doLodge) {
		ITradeStation trade = PostManager.postRegistry.getTradeStation(world, recipient);
		if (trade == null) {
			return EnumDeliveryState.NO_MAILBOX;
		}

		return trade.handleLetter(world, recipient, letterStack, doLodge);
	}

	private static EnumDeliveryState storeInPOBox(World world, IMailAddress recipient, ItemStack letterStack) {

		IPOBox pobox = PostManager.postRegistry.getPOBox(world, recipient);
		if (pobox == null) {
			return EnumDeliveryState.NO_MAILBOX;
		}

		if (!pobox.storeLetter(letterStack.copy())) {
			return EnumDeliveryState.MAILBOX_FULL;
		} else {
			EntityPlayer player = PlayerUtil.getPlayer(world, recipient.getPlayerProfile());
			if (player instanceof EntityPlayerMP) {
				NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo()), player);
			}
		}

		return EnumDeliveryState.OK;
	}

}
