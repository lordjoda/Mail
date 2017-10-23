package mail.mail;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by Johannes Lohrer <lohrer@dbs.ifi.lmu.de> on 12.10.2017.
 */
public interface IPOBox extends IInventory {
    boolean storeLetter(ItemStack letterstack);

    POBoxInfo getPOBoxInfo();
}
