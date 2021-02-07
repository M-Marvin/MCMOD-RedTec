package de.redtec.tileentity;

import java.util.Optional;

import de.redtec.blocks.BlockMultiPart;
import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.gui.ContainerMRaffinery;
import de.redtec.recipetypes.RifiningRecipe;
import de.redtec.registys.ModRecipeTypes;
import de.redtec.registys.ModSoundEvents;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.FluidBucketHelper;
import de.redtec.util.IElectricConnective.Voltage;
import de.redtec.util.IFluidConnective;
import de.redtec.util.ItemStackHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMRaffinery extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, ISidedInventory, IFluidConnective, INamedContainerProvider {
	
	public final int maxFluidStorage;
	
	public FluidStack fluidIn;
	public FluidStack fluidOut;
	
	public int progress1;
	public int progress2;
	public int progress3;
	public int progress4;
	public int progressTotal;
	public boolean isWorking;
	public boolean hasPower;
	
	public TileEntityMRaffinery() {
		super(ModTileEntityType.RAFFINERY, 7);
		this.maxFluidStorage = 3000;
		this.fluidIn = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-4, -1, -4), pos.add(4, 5, 4));
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
				
				this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
				
				this.fluidIn = FluidBucketHelper.transferBuckets(this, 3, this.fluidIn, this.maxFluidStorage);
				this.fluidOut = FluidBucketHelper.transferBuckets(this, 5, this.fluidOut, this.maxFluidStorage);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.world).getNetwork(this.pos);
				
				this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
				this.isWorking = canWork() && this.hasPower;
				
				if (this.isWorking) {
					
					RifiningRecipe recipe = findRecipe();
					
					if (recipe != null) {
						
						if (	ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(0), recipe.getRecipeOutput()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), recipe.getRecipeOutput2()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(2), recipe.getRecipeOutput3())) {
							
							this.progressTotal = recipe.getRifiningTime() / 3;
							
							if (this.progress4 > 0) {
								
								this.progress4++;
								
								if (this.progress4 >= this.progressTotal) {
									
									this.progress4 = 0;
									
									if (this.fluidOut.isEmpty()) {
										this.fluidOut = recipe.getRecipeOutputFluid();
									} else {
										this.fluidOut.grow(recipe.getRecipeOutputFluid().getAmount());
									}
									
								}
								
							} else if (this.progress3 > 0) {
								
								this.progress3++;
								
								if (this.progress3 >= this.progressTotal) {
									
									this.progress3 = 0;
									if (!recipe.getRecipeOutputFluid().isEmpty()) this.progress4 = 1;
									
									if (this.getStackInSlot(2).isEmpty()) {
										this.setInventorySlotContents(2, recipe.getRecipeOutput3());
									} else {
										this.getStackInSlot(2).grow(recipe.getRecipeOutput3().getCount());
									}
									
								}
								
							} else if (this.progress2 > 0) {
								
								this.progress2++;
								
								if (this.progress2 >= this.progressTotal) {
									
									this.progress2 = 0;
									if (!recipe.getRecipeOutput3().isEmpty()) {
										this.progress3 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getStackInSlot(1).isEmpty()) {
										this.setInventorySlotContents(1, recipe.getRecipeOutput2());
									} else {
										this.getStackInSlot(1).grow(recipe.getRecipeOutput2().getCount());
									}
									
								}
								
							} else {
								
								this.progress1++;
								
								if (this.progress1 >= this.progressTotal) {
									
									this.progress1 = 0;
									if (!recipe.getRecipeOutput2().isEmpty()) {
										this.progress2 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getStackInSlot(0).isEmpty()) {
										this.setInventorySlotContents(0, recipe.getRecipeOutput());
									} else {
										this.getStackInSlot(0).grow(recipe.getRecipeOutput().getCount());
									}
									
									this.fluidIn.shrink(recipe.fluidIn.getAmount());
									
								}
								
							}
							
						}
						
					}
					
				} else {
					this.progress1 = 0;
					this.progress2 = 0;
					this.progress3 = 0;
					this.progress4 = 0;
				}
				
			} else if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 3, 0))) {
				TileEntityMRaffinery tileEntity = (TileEntityMRaffinery) BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
				if (tileEntity != null) {
					FluidStack rest = pushFluid(tileEntity.fluidOut, world, pos);
					if (rest != tileEntity.fluidOut) tileEntity.fluidOut = rest;
				}
			}
			
		} else {
			
			if (this.isWorking) {
				
				IParticleData paricle = ParticleTypes.POOF;
				Direction facing = getBlockState().get(BlockMultiPart.FACING);
				
				int ox = 0;
				int oz = 0;
				
				switch(facing) {
				default:
				case NORTH:
					oz = 2;
					ox = 2;
					break;
				case EAST:
					ox = -1;
					oz = 2;
					break;
				case SOUTH:
					ox = -1;
					oz = -1;
					break;
				case WEST:
					ox = 2;
					oz = -1;
					break;
				}
				;
				float width = 0.4F;
				float height = 0.4F;

				float x = this.pos.getX() + ox + (world.rand.nextFloat() + 3.0F) * width;
				float y = this.pos.getY() + 2 + (world.rand.nextFloat() + 5.5F) * height;
				float z = this.pos.getZ() + oz + (world.rand.nextFloat() + 1.0F) * width;
				this.world.addParticle(paricle, x, y, z, 0, 0, 0);
				
				SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
				
				if (this.maschineSound == null ? true : !soundHandler.isPlaying(maschineSound)) {
					
					this.maschineSound = new SoundMachine(this, ModSoundEvents.RAFFINERY_LOOP);
					soundHandler.play(this.maschineSound);
					
				}
				
			}
		}
		
	}

	private SoundMachine maschineSound;

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	public boolean canWork() {
		return this.findRecipe() != null;
	}
	
	public RifiningRecipe findRecipe() {
		Optional<RifiningRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.RIFINING, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMRaffinery(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.raffinery");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return true;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
		if (tileEntity instanceof TileEntityMRaffinery) {
			if (ipos.equals(new BlockPos(1, 3, 0))) {
				int transfer = Math.min(amount, ((TileEntityMRaffinery) tileEntity).fluidOut.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMRaffinery) tileEntity).fluidOut.getFluid(), transfer);
					((TileEntityMRaffinery) tileEntity).fluidOut.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
		if (tileEntity instanceof TileEntityMRaffinery) {
			if (ipos.equals(new BlockPos(1, 0, 1))) {
				if (((TileEntityMRaffinery) tileEntity).fluidIn.getFluid().isEquivalentTo(fluid.getFluid()) || ((TileEntityMRaffinery) tileEntity).fluidIn.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMRaffinery) tileEntity).fluidIn.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMRaffinery) tileEntity).fluidIn.isEmpty()) {
							((TileEntityMRaffinery) tileEntity).fluidIn = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMRaffinery) tileEntity).fluidIn.grow(transfer);
						}
						return fluidRest;
					}
				}
			}
		}
		return fluid;
	}

	@Override
	public Fluid getFluidType() {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn.getFluid();
		} else {
			return this.fluidOut.getFluid();
		}
	}

	@Override
	public FluidStack getStorage() {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn;
		} else {
			return this.fluidOut;
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().get(BlockMultiPart.FACING);
		return	(ipos.equals(new BlockPos(1, 0, 1)) && side == facing.getOpposite()) ||
				(ipos.equals(new BlockPos(1, 3, 0)) && side == facing);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluidIn.isEmpty()) compound.put("FluidIn", this.fluidIn.writeToNBT(new CompoundNBT()));
		if (!this.fluidOut.isEmpty()) compound.put("FluidOut", this.fluidOut.writeToNBT(new CompoundNBT()));
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("progress1", this.progress1);
		compound.putInt("progress2", this.progress2);
		compound.putInt("progress3", this.progress3);
		compound.putInt("progress4", this.progress4);
		compound.putInt("progressTotal", this.progressTotal);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.fluidIn = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
		if (compound.contains("FluidIn")) this.fluidIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidIn"));
		if (compound.contains("FluidOut")) this.fluidOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidOut"));
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.progress1 = compound.getInt("progress1");
		this.progress2 = compound.getInt("progress2");
		this.progress3 = compound.getInt("progress3");
		this.progress4 = compound.getInt("progress4");
		this.progressTotal = compound.getInt("progressTotal");
		super.func_230337_a_(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}