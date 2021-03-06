package de.redtec.tileentity;

import de.redtec.RedTec;
import de.redtec.typeregistys.ModTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityMBurnedCable extends TileEntity {
	
	protected Block cableBlock;
	
	public TileEntityMBurnedCable() {
		super(ModTileEntityType.BURNED_CABLE);
		this.cableBlock = RedTec.copper_cable;
	}
	
	public Block getCableBlock() {
		return cableBlock;
	}
	
	public void setCableBlock(Block cableBlock) {
		this.cableBlock = cableBlock;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (this.cableBlock != null) compound.putString("CableBlock", this.cableBlock.getRegistryName().toString());
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		if (nbt.contains("CableBlock")) this.cableBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("CableBlock")));
		super.read(state, nbt);
	}

}
