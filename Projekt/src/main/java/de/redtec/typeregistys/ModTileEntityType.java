package de.redtec.typeregistys;

import com.mojang.datafixers.types.Type;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityAdvancedMovingBlock;
import de.redtec.tileentity.TileEntityControllPanel;
import de.redtec.tileentity.TileEntityConveyorBelt;
import de.redtec.tileentity.TileEntityFluidInput;
import de.redtec.tileentity.TileEntityFluidOutput;
import de.redtec.tileentity.TileEntityFluidPipe;
import de.redtec.tileentity.TileEntityFluidValve;
import de.redtec.tileentity.TileEntityFuseBox;
import de.redtec.tileentity.TileEntityHarvester;
import de.redtec.tileentity.TileEntityHoverControler;
import de.redtec.tileentity.TileEntityItemDetector;
import de.redtec.tileentity.TileEntityJigsaw;
import de.redtec.tileentity.TileEntityLockedCompositeBlock;
import de.redtec.tileentity.TileEntityMAlloyFurnace;
import de.redtec.tileentity.TileEntityMBlender;
import de.redtec.tileentity.TileEntityMBurnedCable;
import de.redtec.tileentity.TileEntityMCoalHeater;
import de.redtec.tileentity.TileEntityMElectricFurnace;
import de.redtec.tileentity.TileEntityMFluidBath;
import de.redtec.tileentity.TileEntityMGenerator;
import de.redtec.tileentity.TileEntityMMultimeter;
import de.redtec.tileentity.TileEntityMRaffinery;
import de.redtec.tileentity.TileEntityMSchredder;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityMThermalZentrifuge;
import de.redtec.tileentity.TileEntityMotor;
import de.redtec.tileentity.TileEntityRedstoneReciver;
import de.redtec.tileentity.TileEntitySignalAntenna;
import de.redtec.tileentity.TileEntitySignalProcessorContact;
import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.tileentity.TileEntityStoringCraftingTable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntityType {
	
	public static final TileEntityType<TileEntityAdvancedMovingBlock> ADVANCED_PISTON = register("advanced_piston", TileEntityType.Builder.create(TileEntityAdvancedMovingBlock::new, RedTec.advanced_moving_block));
	public static final TileEntityType<TileEntityRedstoneReciver> REMOTE_CONTROLER = register("remote_controler", TileEntityType.Builder.create(TileEntityRedstoneReciver::new, RedTec.advanced_moving_block));
	public static final TileEntityType<TileEntitySignalAntenna> SIGNAL_ANTENNA = register("signal_antenna", TileEntityType.Builder.create(TileEntitySignalAntenna::new, RedTec.antenna_conector));
	public static final TileEntityType<TileEntityStoringCraftingTable> STORING_CRAFTING_TABLE = register("storing_crafting_table", TileEntityType.Builder.create(TileEntityStoringCraftingTable::new, RedTec.storing_crafting_table));
	public static final TileEntityType<TileEntitySignalProcessorContact> SIGNAL_PROCESSOR = register("signal_processor", TileEntityType.Builder.create(TileEntitySignalProcessorContact::new, RedTec.signal_processor_contact));
	public static final TileEntityType<TileEntityLockedCompositeBlock> LOCKED_COMPOSITE_BLOCK = register("locked_radial_conector", TileEntityType.Builder.create(TileEntityLockedCompositeBlock::new, RedTec.radial_conector));
	public static final TileEntityType<TileEntityHoverControler> HOVER_CONTROLER = register("hover_controler", TileEntityType.Builder.create(TileEntityHoverControler::new, RedTec.hover_controler));
	public static final TileEntityType<TileEntityControllPanel> CONTROLL_PANEL = register("controll_panel", TileEntityType.Builder.create(TileEntityControllPanel::new, RedTec.controll_panel));
	public static final TileEntityType<TileEntityHarvester> HARVESTER = register("harvester", TileEntityType.Builder.create(TileEntityHarvester::new, RedTec.harvester));
	public static final TileEntityType<TileEntitySimpleBlockTicking> SIMPLE_BLOCK_TICKING = register("simple_block_ticking", TileEntityType.Builder.create(TileEntitySimpleBlockTicking::new, RedTec.panel_lamp, RedTec.infinity_power_source, RedTec.transformator_contact));
	public static final TileEntityType<TileEntityJigsaw> JIGSAW = register("jigsaw", TileEntityType.Builder.create(TileEntityJigsaw::new, RedTec.jigsaw));
	public static final TileEntityType<TileEntityMGenerator> GENERATOR = register("generator", TileEntityType.Builder.create(TileEntityMGenerator::new, RedTec.generator));
	public static final TileEntityType<TileEntityFluidPipe> FLUID_PIPE = register("fluid_pipe", TileEntityType.Builder.create(TileEntityFluidPipe::new, RedTec.fluid_pipe));
	public static final TileEntityType<TileEntityFluidInput> FLUID_INPUT = register("fluid_input", TileEntityType.Builder.create(TileEntityFluidInput::new, RedTec.fluid_input));
	public static final TileEntityType<TileEntityFluidOutput> FLUID_OUTPUT = register("fluid_output", TileEntityType.Builder.create(TileEntityFluidOutput::new, RedTec.fluid_output));
	public static final TileEntityType<TileEntityMSteamGenerator> STEAM_GENERATOR = register("steam_generator", TileEntityType.Builder.create(TileEntityMSteamGenerator::new, RedTec.steam_generator));
	public static final TileEntityType<TileEntityMCoalHeater> COAL_HEATER = register("coal_heater", TileEntityType.Builder.create(TileEntityMCoalHeater::new, RedTec.coal_heater));
	public static final TileEntityType<TileEntityFuseBox> FUSE_BOX = register("fuse_box", TileEntityType.Builder.create(TileEntityFuseBox::new, RedTec.fuse_box));
	public static final TileEntityType<TileEntityMMultimeter> MULTIMETER = register("block_multimeter", TileEntityType.Builder.create(TileEntityMMultimeter::new, RedTec.multimeter));
	public static final TileEntityType<TileEntityFluidValve> FLUID_VALVE = register("fluid_valve", TileEntityType.Builder.create(TileEntityFluidValve::new, RedTec.fluid_valve));
	public static final TileEntityType<TileEntityMElectricFurnace> ELECTRIC_FURNACE = register("electric_furnace", TileEntityType.Builder.create(TileEntityMElectricFurnace::new, RedTec.electric_furnace));
	public static final TileEntityType<TileEntityMSchredder> SCHREDDER = register("schredder", TileEntityType.Builder.create(TileEntityMSchredder::new, RedTec.schredder));
	public static final TileEntityType<TileEntityMBlender> BLENDER = register("blender", TileEntityType.Builder.create(TileEntityMBlender::new, RedTec.blender));
	public static final TileEntityType<TileEntityMRaffinery> RAFFINERY = register("raffinery", TileEntityType.Builder.create(TileEntityMRaffinery::new, RedTec.raffinery));
	public static final TileEntityType<TileEntityMAlloyFurnace> ALLOY_FURNACE = register("alloy_furnace", TileEntityType.Builder.create(TileEntityMAlloyFurnace::new, RedTec.alloy_furnace));
	public static final TileEntityType<TileEntityConveyorBelt> CONVEYOR_BELT = register("conveyor_belt", TileEntityType.Builder.create(TileEntityConveyorBelt::new, RedTec.conveyor_belt, RedTec.conveyor_spliter));
	public static final TileEntityType<TileEntityMThermalZentrifuge> THERMAL_ZENTRIFUGE = register("thermal_zentrifuge", TileEntityType.Builder.create(TileEntityMThermalZentrifuge::new, RedTec.thermal_zentrifuge));
	public static final TileEntityType<TileEntityMotor> MOTOR = register("motor", TileEntityType.Builder.create(TileEntityMotor::new, RedTec.motor));
	public static final TileEntityType<TileEntityMFluidBath> FLUID_BATH = register("fluid_bath", TileEntityType.Builder.create(TileEntityMFluidBath::new, RedTec.fluid_bath));
	public static final TileEntityType<TileEntityMBurnedCable> BURNED_CABLE = register("burned_cable", TileEntityType.Builder.create(TileEntityMBurnedCable::new, RedTec.burned_cable));
	public static final TileEntityType<TileEntityItemDetector> ITEM_DETECTOR = register("item_detector", TileEntityType.Builder.create(TileEntityItemDetector::new, RedTec.item_detector));
	
	private static <T extends TileEntity> TileEntityType<T> register(String key, TileEntityType.Builder<T> builder) {
		Type<?> type = Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, key);
		TileEntityType<T> tileEntityType = builder.build(type);
		tileEntityType.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.TILE_ENTITIES.register(tileEntityType);
		return tileEntityType;
	}
	
}
