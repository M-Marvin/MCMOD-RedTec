package de.redtec.fluids.util;

import java.util.Random;

import de.redtec.RedTec;
import net.minecraft.block.Block;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class GasFluid extends Fluid implements IBucketPickupHandler {
	
	@Override
	public Item getFilledBucket() {
		return RedTec.steam_bucket;
	}

	@Override
	protected boolean canDisplace(FluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
		return false;
	}

	@Override
	public Vector3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
		return new Vector3d(0, 0, 0);
	}

	@Override
	public int getTickRate(IWorldReader p_205569_1_) {
		return 1;
	}

	@Override
	protected float getExplosionResistance() {
		return 100F;
	}

	@Override
	public float getActualHeight(FluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
		return getHeight(p_215662_1_);
	}

	@Override
	public float getHeight(FluidState p_223407_1_) {
		return 1F;
	}
	
	@Override
	public boolean isSource(FluidState state) {
		return true;
	}
	
	@Override
	public VoxelShape func_215664_b(FluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
	}
	
	@Override
	public int getLevel(FluidState p_207192_1_) {
		return 8;
	}
	
	@Override
	protected boolean ticksRandomly() {
		return true;
	}
	
	@Override
	public void tick(World worldIn, BlockPos pos, FluidState state) {}
	
	public void onMoved(World worldIn, BlockPos pos, Direction moveDirection, FluidState state, Random random) {};
	
//	@Override
//	public Fluid getFlowingFluid() {
//		return this;
//	}
//
//	@Override
//	public Fluid getStillFluid() {
//		return this;
//	}
//
//	@Override
//	protected boolean canSourcesMultiply() {
//		return false;
//	}
//
//	@Override
//	public void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {}
//	
//	@Override
//	protected int getSlopeFindDistance(IWorldReader worldIn) {
//		return 0;
//	}
//
//	@Override
//	protected int getLevelDecreasePerBlock(IWorldReader worldIn) {
//		return 0;
//	}
	
}
