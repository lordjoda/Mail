package mail.api.core;

import net.minecraft.nbt.NBTTagCompound;

public interface INbtReadable {
	void readFromNBT(NBTTagCompound nbt);
}
