package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.redtec.RedTec;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenStoredCrafting extends ContainerScreen<ContainerStoredCrafting> {
	
   public static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/gui/storing_crafting_table.png");
   
   public ScreenStoredCrafting(ContainerStoredCrafting screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(screenContainer, inv, titleIn);
   }
   
   @SuppressWarnings("deprecation")
   protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_230706_i_.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
      int i = this.guiLeft;
      int j = (this.field_230709_l_ - this.ySize) / 2;
      this.func_238474_b_(p_230450_1_, i, j, 0, 0, this.xSize, this.ySize);
   }
   
}