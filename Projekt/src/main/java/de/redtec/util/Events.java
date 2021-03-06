package de.redtec.util;

import de.redtec.RedTec;
import de.redtec.fluids.util.BlockGasFluid;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=RedTec.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class Events {
	
	@SubscribeEvent
	public static void onResourceManagerReload(net.minecraftforge.event.AddReloadListenerEvent event) {
		JigsawFileManager.onFileManagerReload();
	}
	
	@SubscribeEvent
	public static void onRightClickBlock(net.minecraftforge.event.entity.player.FillBucketEvent event) {

		World worldIn = event.getWorld();
		PlayerEntity playerIn = event.getPlayer();
		
		RayTraceResult result = event.getTarget();
		Vector3d vec1p = event.getPlayer().getLookVec();
		int x = (int) (result.getHitVec().x % 1 == 0 ? result.getHitVec().x + (vec1p.x > 0 ? 0 : -1) : Math.floor(result.getHitVec().x));
		int y = (int) (result.getHitVec().y % 1 == 0 ? result.getHitVec().y + (vec1p.y > 0 ? 0 : -1) : Math.floor(result.getHitVec().y));
		int z = (int) (result.getHitVec().z % 1 == 0 ? result.getHitVec().z + (vec1p.z > 0 ? 0 : -1) : Math.floor(result.getHitVec().z));
		BlockPos fluidPos = new BlockPos(x, y, z);
		FluidState fluidState = worldIn.getFluidState(fluidPos);
		
		if (fluidState.getBlockState().getBlock() instanceof BlockGasFluid) {
			
			ItemStack bucketItem = playerIn.getHeldItemMainhand();
						
			if (fluidState.getBlockState().getBlock() instanceof IBucketPickupHandler) {
				Fluid fluid = ((IBucketPickupHandler)fluidState.getBlockState().getBlock()).pickupFluid(worldIn, fluidPos, fluidState.getBlockState());
				
				if (fluid != Fluids.EMPTY) {
					
					playerIn.addStat(Stats.ITEM_USED.get(bucketItem.getItem()));
					
					SoundEvent soundevent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL;
					playerIn.playSound(soundevent, 1.0F, 1.0F);
					ItemStack itemstack1 = DrinkHelper.fill(bucketItem, playerIn, new ItemStack(fluid.getFilledBucket()));
					if (!worldIn.isRemote) {
						CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)playerIn, new ItemStack(fluid.getFilledBucket()));
						playerIn.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack1);
					}
					
				}
			}
			
		}
		
	}
	
}
