package mail.core.blocks;

import com.google.common.base.Preconditions;
import mail.api.core.IModelManager;
import mail.core.tiles.TileForestry;
import mail.core.utils.BlockUtil;
import mail.core.utils.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class MachineProperties<T extends TileForestry> implements IMachineProperties<T> {
	private final String name;
	private final String teIdent;
	private final Class<T> teClass;
	private final AxisAlignedBB boundingBox;
	@Nullable
	private Block block;

	public MachineProperties(Class<T> teClass, String name) {
		this("forestry." + name, teClass, name, new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}

	public MachineProperties(Class<T> teClass, String name, AxisAlignedBB boundingBox) {
		this("forestry." + name, teClass, name, boundingBox);
	}

	private MachineProperties(String teIdent, Class<T> teClass, String name, AxisAlignedBB boundingBox) {
		this.teIdent = teIdent;
		this.teClass = teClass;
		this.name = name;
		this.boundingBox = boundingBox;
	}

	@Override
	public void setBlock(Block block) {
		this.block = block;
	}

	@Nullable
	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockPos pos, IBlockState state) {
		return boundingBox;
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(World world, BlockPos pos, Vec3d startVec, Vec3d endVec) {
		return BlockUtil.collisionRayTrace(pos, startVec, endVec, boundingBox);
	}

	@Override
	public void registerTileEntity() {
		GameRegistry.registerTileEntity(teClass, teIdent);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		ResourceLocation itemNameFromRegistry = ItemStackUtil.getItemNameFromRegistry(item);
		Preconditions.checkNotNull(itemNameFromRegistry, "No registry name for items");
		String identifier = itemNameFromRegistry.getResourcePath();
		manager.registerItemModel(item, 0, identifier);
	}

	@Override
	public TileEntity createTileEntity() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName(), e);
		}
	}

	@Override
	public Class<T> getTeClass() {
		return teClass;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return true;
	}
}
