package mail.core.inventory;

import mail.api.core.INbtReadable;
import mail.api.core.INbtWritable;
import mail.core.tiles.IFilterSlotDelegate;
import net.minecraft.inventory.ISidedInventory;

public interface IInventoryAdapter extends ISidedInventory, IFilterSlotDelegate, INbtWritable, INbtReadable {

}
