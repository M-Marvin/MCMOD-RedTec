package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.redtec.RedTec;
import de.redtec.blocks.BlockMultiPart;
import de.redtec.tileentity.TileEntityMRaffinery;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMRaffineryRenderer extends TileEntityRenderer<TileEntityMRaffinery> {
	
	public static final ResourceLocation RAFFINERY_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/block/raffinery.png");
	
	private TileEntityMRaffineryModel raffineryModel;
	
	public TileEntityMRaffineryRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.raffineryModel = new TileEntityMRaffineryModel();
	}

	@Override
	public void render(TileEntityMRaffinery tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultiPart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(RAFFINERY_TEXTURES));
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.rotate(facing.getRotation());
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			raffineryModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.pop();
			
		}
		
	}
	
}
