package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.blocks.BlockJigsaw.JigsawType;
import de.redtec.worldgen.JigsawFeatureConfig;
import de.redtec.worldgen.StoneOreFeatureConfig;
import de.redtec.worldgen.placements.HorizontalSpreadPlacementConfig;
import de.redtec.worldgen.placements.SimpleOrePlacementConfig;
import de.redtec.worldgen.placements.VerticalOffsetPlacementConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures {
	
	// Ores
	public static final ConfiguredFeature<?, ?> COPPER_ORE = registerConfiguredFeature("copper_ore", 
			Feature.ORE.withConfiguration(
					new OreFeatureConfig(
							OreFeatureConfig.FillerBlockType.field_241882_a, 
							RedTec.copper_ore.getDefaultState(),
							10
					)
			)
			.withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(11, 45, 15)
					)
			)
	);
	public static final ConfiguredFeature<?, ?> BAUXIT_STONE_ORE = registerConfiguredFeature("bauxit_stone_ore",
			ModFeature.STONE_ORE.withConfiguration(
					new StoneOreFeatureConfig(
							OreFeatureConfig.FillerBlockType.field_241882_a,
							RedTec.bauxit.getDefaultState(),
							RedTec.bauxit_ore.getDefaultState(),
							42
					)
			)
			.withPlacement(
					ModPlacement.SIMPLE_ORE.configure(
							new SimpleOrePlacementConfig(45, 256, 3)
					)
			)
	);
	
	// Trees
	public static final ConfiguredFeature<?, ?> RUBBER_TREE = registerConfiguredFeature("rubber_tree",
			ModFeature.JIGSAW_FEATURE.withConfiguration(new JigsawFeatureConfig(
					new TagMatchRuleTest(ModTags.DIRT), Direction.NORTH, JigsawType.VERTICAL_UP, 
					new ResourceLocation(RedTec.MODID, "nature/rubber_tree"), new ResourceLocation(RedTec.MODID, "tree_log"),
					Blocks.DIRT, false, 1, 1)
			).withPlacement(
					ModPlacement.VERTICAL_OFFSET.configure(
							new VerticalOffsetPlacementConfig(-1)
					)
			).withPlacement(
					ModPlacement.HORIZONTAL_SPREAD.configure(
							new HorizontalSpreadPlacementConfig(2)
					)
			).withPlacement(
					Placement.field_242906_k.configure(new NoPlacementConfig())
			).withPlacement(
					Placement.field_242898_b.configure(new ChanceConfig(10))
			)
	);
	
	public static ConfiguredFeature<?, ?> registerConfiguredFeature(String key, ConfiguredFeature<?, ?> configuredFeature) {
		return Registry.register(WorldGenRegistries.field_243653_e, new ResourceLocation(RedTec.MODID, key), configuredFeature);
	}
	
}
