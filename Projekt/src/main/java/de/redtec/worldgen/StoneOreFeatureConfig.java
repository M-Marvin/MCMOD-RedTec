package de.redtec.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

public class StoneOreFeatureConfig implements IFeatureConfig {
	
	public static final Codec<StoneOreFeatureConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(RuleTest.field_237127_c_.fieldOf("target").forGetter((config) -> {
			return config.target;
		}), BlockState.field_235877_b_.fieldOf("stoneState").forGetter((config) -> {
			return config.stoneState;
		}), BlockState.field_235877_b_.fieldOf("oreState").forGetter((config) ->  {
			return config.oreState;
		}), Codec.INT.fieldOf("size").forGetter((config) -> {
			return config.size;
		})).apply(codec, StoneOreFeatureConfig::new);
	});
	
	public final RuleTest target;
	public final int size;
	public final BlockState stoneState;
	public final BlockState oreState;
	
	public StoneOreFeatureConfig(RuleTest target, BlockState stoneState, BlockState oreState, int size) {
		this.target = target;
		this.size = size;
		this.stoneState = stoneState;
		this.oreState = oreState;
	}
	
}
