package de.redtec.dynamicsounds;

import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.typeregistys.ModSoundEvents;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundMSteamGeneratorLoop extends TickableSound {

	protected World world;
	protected BlockPos pos;
	
	public SoundMSteamGeneratorLoop(TileEntityMSteamGenerator tileEntity) {
		super(ModSoundEvents.TURBIN_LOOP, SoundCategory.BLOCKS);
		this.pos = tileEntity.getPos();
		this.world = tileEntity.getWorld();
		this.x = tileEntity.getPos().getX();
		this.y = tileEntity.getPos().getY();
		this.z = tileEntity.getPos().getZ();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
		this.pitch = 0.1F;
	}

	@Override
	public void tick() {
		
		TileEntity te = this.world.getTileEntity(pos);
		TileEntityMSteamGenerator tileEntity = te instanceof TileEntityMSteamGenerator ? (TileEntityMSteamGenerator) te : new TileEntityMSteamGenerator();
		
		if (tileEntity.accerlation > 0) {
			
			this.pitch = tileEntity.accerlation / 20F * 2F;
			this.volume = tileEntity.accerlation / 20F;
			
			this.x = tileEntity.getPos().getX();
			this.y = tileEntity.getPos().getY();
			this.z = tileEntity.getPos().getZ();
			
		} else {
			
			this.finishPlaying();
			
		}
		
	}

}
