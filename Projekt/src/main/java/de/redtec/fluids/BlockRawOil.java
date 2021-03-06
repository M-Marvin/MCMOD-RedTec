package de.redtec.fluids;

import de.redtec.fluids.util.BlockModFlowingFluid;
import de.redtec.typeregistys.ModFluids;
import de.redtec.typeregistys.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRawOil extends BlockModFlowingFluid {
	
	public BlockRawOil() {
		super("raw_oil", ModFluids.RAW_OIL, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		
		double d0 = entityIn.getPosYEye() - (double)0.11111111F;
		BlockPos blockpos = new BlockPos(entityIn.getPosX(), d0, entityIn.getPosZ());
		FluidState fluidstate = worldIn.getFluidState(blockpos);
		
		if (ModTags.RAW_OIL.contains(fluidstate.getFluid()) && entityIn instanceof LivingEntity) {
			// TODO
			((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.BLINDNESS, 40, 1));
		}
		
	}
	
}