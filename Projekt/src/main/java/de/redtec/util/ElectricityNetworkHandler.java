package de.redtec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.redtec.RedTec;
import de.redtec.packet.SSendENHandeler;
import de.redtec.util.IElectricConnective.DeviceType;
import de.redtec.util.IElectricConnective.Voltage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.PacketDistributor;

public class ElectricityNetworkHandler extends WorldSavedData {

	protected static ElectricityNetworkHandler clientInstance;
	
	protected boolean hasUpdated;
	
	private boolean isServerInstace;
	private List<ElectricityNetwork> networks;
	private IWorld world;
	
	public ElectricityNetworkHandler() {
		this(true);
	}
	
	public ElectricityNetworkHandler(boolean serverInstance) {
		super("elctric_networks");
		this.isServerInstace = serverInstance;
		this.networks = new ArrayList<ElectricityNetworkHandler.ElectricityNetwork>();
	}
	
	@Override
	public void read(CompoundNBT compound) {
		
		this.networks.clear();
		ListNBT networkTag = compound.getList("Networks", 10);
		for (int i = 0; i < networkTag.size(); i++) {
			this.networks.add(ElectricityNetwork.read(networkTag.getCompound(i)));
		}
		
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		
		ListNBT networkTag = new ListNBT();
		for (ElectricityNetwork net : this.networks) {
			networkTag.add(net.write(new CompoundNBT()));
		}
		compound.put("Networks", networkTag);
		return compound;
		
	}
	
	public static ElectricityNetworkHandler getHandlerForWorld(IWorld world) {
		
		if (!world.isRemote()) {
			DimensionSavedDataManager storage = ((ServerWorld) world).getSavedData();
			ElectricityNetworkHandler handler = storage.getOrCreate(ElectricityNetworkHandler::new, "elctric_networks");
			handler.world = world;
			return handler;
		} else {
			if (clientInstance == null) clientInstance = new ElectricityNetworkHandler(false);
			return clientInstance;
		}
		
	}
	
	public boolean isServerInstace() {
		return isServerInstace;
	}
		
	public void updateNetwork(World world, BlockPos pos) {
		this.calculateNetwork(world, pos);
	}
	
	public void calculateNetwork(World world, BlockPos position) {
				
		if (this.isServerInstace) {
			
			if (world.getBlockState(position).getBlock() instanceof IElectricConnective) {
				
				ElectricityNetwork network = getNetwork(position);
				List<Direction> attachList = new ArrayList<Direction>();
				network.positions.put(position, attachList);
				
				if (!network.isUpdated((ServerWorld) world) && !world.isRemote()) {
					
					int lap = 3;
					while (lap <= 3) {
						
						lap++;
						
						network.positions.clear();
						boolean flag = this.scann(world, position, null, network.positions, 0, DeviceType.WIRE);
						
						// If no Device Found, add it self, to prevent endless new Networks;
						if (!flag) {
							network.positions.put(position, attachList);
						}
						
						if (network.positions.size() <= 1) network.lastUpdated = 0;
						network.needCurrent = 0;
						network.voltage = Voltage.NoLimit;
						network.capacity = 0;
						
						for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
							
							BlockState state = world.getBlockState(device.getKey());
							List<Direction> attacheSides = device.getValue();
							
							if (state.getBlock() instanceof IElectricConnective && !(state.getBlock() instanceof IElectricWire)) {
								
								IElectricConnective device1 = (IElectricConnective) state.getBlock();
								
								boolean hasCurrentAdded = false;
								for (Direction attachSide : attacheSides) {
									
									Voltage voltage = device1.getVoltage(world, device.getKey(), state, attachSide);
									float needCurrent = device1.getNeededCurrent(world, device.getKey(), state, attachSide);
									
									if (needCurrent >= 0) {
										
										//if (voltage.getVoltage() > network.voltage.getVoltage() && needCurrent != 0) network.voltage = voltage;
										if (!hasCurrentAdded) {
											network.needCurrent += needCurrent;
											hasCurrentAdded = true;
										}
										
									} else if (needCurrent < 0) {
										
										if (voltage.getVoltage() > network.voltage.getVoltage()) network.voltage = voltage;
										if (!hasCurrentAdded) {
											network.capacity += -needCurrent;
											hasCurrentAdded = true;
										}
										
									}
									
								}
								
							}
							
						}
						
						if (network.needCurrent <= network.capacity) {
							network.current = network.needCurrent;
						} else {
							network.current = network.capacity;
						}
						
						boolean abbortUpdate = false;
						for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
							
							BlockPos pos = device.getKey();
							BlockState state = world.getBlockState(pos);
							
							if (state.getBlock() instanceof IElectricConnective) {
								
								boolean result = ((IElectricConnective) state.getBlock()).beforNetworkChanges(world, pos, state, network, lap);
								if (result) abbortUpdate = true;
								
							}
							
						}
						
						if (abbortUpdate) continue;
						
						for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
							
							BlockPos pos = device.getKey();
							BlockState state = world.getBlockState(pos);
							
							if (state.getBlock() instanceof IElectricConnective) {
								
								((IElectricConnective) state.getBlock()).onNetworkChanges(world, pos, state, network);
								
							}
							
						}
						
						break;
						
					}
					
//					System.out.println("Network Voltage: " + network.voltage);
//					System.out.println("Network Current Needed:  " + network.needCurrent);
//					System.out.println("Network Capacity: " + network.capacity);
//					System.out.println("Network Current: " + network.current);
//					
//					System.out.println(this.networks.size());
//					System.out.println(getNetwork(position).current);
					
				}
				
			}
			
			List<ElectricityNetwork> newList = new ArrayList<>();
			for (ElectricityNetwork network1 : this.networks) {
				
				int lastUpdate = (int) (world.getGameTime() - network1.lastUpdated);
				if (network1.positions.size() > 1 && lastUpdate < 500) {
					newList.add(network1);
				}
				
			}

			networks = newList;
			
			if (world.getGameTime() % 100 == 0 && !hasUpdated) {
				
				hasUpdated = true;
				SSendENHandeler packet = new SSendENHandeler(this);
				RedTec.NETWORK.send(PacketDistributor.ALL.noArg(), packet);
				
			} else if (world.getGameTime() % 100 > 0) {
				hasUpdated = false;
			}
			
		}
		
	}
	
	public ElectricityNetwork getNetworkState(World world, BlockPos position, Direction direction) {
		
		if (this.isServerInstace) {
			
			if (world.getBlockState(position).getBlock() instanceof IElectricConnective) {
					
				ElectricityNetwork network = new ElectricityNetwork();
				List<Direction> sideList = new ArrayList<Direction>();
				sideList.add(direction.getOpposite());
				network.positions.put(position, sideList);
				this.scann(world, position.offset(direction), direction, network.positions, 0, DeviceType.WIRE);
				
				network.needCurrent = 0;
				network.voltage = Voltage.NoLimit;
				network.capacity = 0;
				
				for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
					
					BlockState state = world.getBlockState(device.getKey());
					List<Direction> attacheSides = device.getValue();
					
					if (state.getBlock() instanceof IElectricConnective && !(state.getBlock() instanceof IElectricWire)) {
						
						IElectricConnective device1 = (IElectricConnective) state.getBlock();
						
						boolean hasCurrentAdded = false;
						for (Direction attachSide : attacheSides) {
							
							Voltage voltage = device1.getVoltage(world, device.getKey(), state, attachSide);
							float needCurrent = device1.getNeededCurrent(world, device.getKey(), state, attachSide);
							
							if (needCurrent >= 0) {
								
								if (!hasCurrentAdded) {
									network.needCurrent += needCurrent;
									hasCurrentAdded = true;
								}
								
							} else if (needCurrent < 0) {
								
								if (voltage.getVoltage() > network.voltage.getVoltage()) network.voltage = voltage;
								if (!hasCurrentAdded) {
									network.capacity += -needCurrent;
									hasCurrentAdded = true;
								}
								
							}
							
						}
						
					}
					
				}
				
				network.voltage = Voltage.LowVoltage;
				
				if (network.needCurrent <= network.capacity) {
					network.current = network.needCurrent;
				} else {
					network.current = network.capacity;
				}
				
//					System.out.println("Network Voltage: " + network.voltage);
//					System.out.println("Network Current Needed:  " + network.needCurrent);
//					System.out.println("Network Capacity: " + network.capacity);
//					System.out.println("Network Current: " + network.current);
//					
//					System.out.println(this.networks.size());
//					System.out.println(getNetwork(position).current);
				
				return network;
				
			}
					
		}
		
		return new ElectricityNetwork();
		
	}
	
	protected boolean scann(World world, BlockPos scannPos, Direction direction, HashMap<BlockPos, List<Direction>> posList, int scannDepth, DeviceType lastDevice) {
		
		BlockState state = world.getBlockState(scannPos);
		
		if (state.getBlock() instanceof IElectricConnective && scannDepth < 50000) {
			
			IElectricConnective device = (IElectricConnective) state.getBlock();
			DeviceType type = device.getDeviceType();
			
			if ((direction != null ? device.canConnect(direction, world, scannPos, state) : true) && lastDevice.canConnectWith(type)) {
				
				boolean flag = false;
				
				if (posList.containsKey(scannPos)) {
					List<Direction> attachedDirection = posList.get(scannPos);
					if (direction != null) {
						if (attachedDirection.contains(direction)) return false;
						attachedDirection.add(direction);
					}
					posList.put(scannPos, attachedDirection);
					flag = true;
				} else {
					List<Direction> attachDirections = new ArrayList<Direction>();
					if (direction != null) attachDirections.add(direction);
					posList.put(scannPos, attachDirections);
					flag = true;
				}
				
				boolean flag2 = device.getDeviceType() == DeviceType.SWITCH ? device.isSwitchClosed(world, scannPos, state) : true;
				
				if (flag2) {

					for (Direction d : Direction.values()) {
						
						if (device.canConnect(d.getOpposite(), world, scannPos, state) && (direction != null ? d != direction.getOpposite() : true)) {
							
							BlockPos pos2 = scannPos.offset(d);
							boolean flag1 = this.scann(world, pos2, d, posList, scannDepth + 1, type);
							
							if (flag1) {
								
								List<Direction> sides = posList.get(scannPos);
								sides.add(d.getOpposite());
								posList.put(scannPos, sides);
								
							}
							
						}
						
					}
					
				}
				
				if (flag) {
					
					List<BlockPos> multiParts = device.getMultiBlockParts(world, scannPos, state);
					
					if (multiParts != null) {
						
						for (BlockPos multiPart : multiParts) {
							
							if (!posList.containsKey(multiPart)) {
								scann(world, multiPart, null, posList, scannDepth++, lastDevice);
							} else {
								continue;
							}
							
						}
						
					}
					
				}
				
				return flag;
				
			}
			
		}
		
		return false;
		
	}
	
	public ElectricityNetwork getNetwork(BlockPos pos) {
		
		for (ElectricityNetwork net : this.networks) {
			
			if (net.contains(pos)) return net;
			
		}
				
		ElectricityNetwork network = new ElectricityNetwork();
		this.networks.add(network);
		return network;
		
	}

	public CompoundNBT makeUpdateTag() {
		
		CompoundNBT compound = new CompoundNBT();
		ListNBT networkTag = new ListNBT();
		for (ElectricityNetwork net : this.networks) {
			if (((World) world).getGameTime() - net.lastUpdated < 500) {
				
				networkTag.add(net.write(new CompoundNBT()));
			}
		}
		compound.put("Networks", networkTag);
		return compound;
		
	}
	
	public static class ElectricityNetwork {
		
		public HashMap<BlockPos, List<Direction>> positions;
		public float current;
		public Voltage voltage;
		public float capacity;
		public float needCurrent;
		public long lastUpdated;
		
		public ElectricityNetwork() {
			this.positions = new HashMap<BlockPos, List<Direction>>();
			this.voltage = Voltage.NoLimit;
			this.current = 0;
			this.capacity = 0;
		}
		
		public boolean contains(BlockPos pos) {
			for (BlockPos pos1 : this.positions.keySet()) {
				if (pos1.equals(pos)) return true;
			}
			return false;
		}
		
		public boolean isUpdated(ServerWorld world) {
			
			long time = world.getGameTime();
			
			if (this.lastUpdated != time) {
				this.lastUpdated = time;
				return false;
			}
			
			return true;
			
		}
		
		public float getGeneratorProductivity() {
			return Math.max(1F, (float) this.needCurrent / this.current);
		}
		
		public Voltage canMachinesRun() {
			boolean hasPower = this.needCurrent - this.current <= 0.001 && this.current > 0;
			return hasPower ? this.voltage : Voltage.NoLimit;
		}
		
		public Voltage getVoltage() {
			return voltage;
		}
		
		public float getCurrent() {
			return current;
		}
		
		public float getNeedCurrent() {
			return needCurrent;
		}
		
		public float getCapacity() {
			return capacity;
		}
		
		public BlockPos[] getConnectedBlocks() {
			return this.positions.keySet().toArray(new BlockPos[this.positions.size()]);
		}
		
		public CompoundNBT write(CompoundNBT nbt) {
			nbt.putFloat("Current", this.current);
			nbt.putFloat("NeedCurrent", this.needCurrent);
			nbt.putString("Voltage", this.voltage.toString());
			ListNBT deviceList = new ListNBT();
			for (Entry<BlockPos, List<Direction>> entry : this.positions.entrySet()) {
				CompoundNBT entryTag = new CompoundNBT();
				entryTag.put("Position", NBTUtil.writeBlockPos(entry.getKey()));
				ListNBT sides = new ListNBT();
				for (Direction d : entry.getValue()) {
					sides.add(StringNBT.valueOf(d.getName2()));
				}
				entryTag.put("AttachedSides", sides);
				deviceList.add(entryTag);
			}
			nbt.put("Devices", deviceList);
			return nbt;
		}
		
		public static ElectricityNetwork read(CompoundNBT nbt) {
			ElectricityNetwork network = new ElectricityNetwork();
			network.current = nbt.getFloat("Current");
			network.needCurrent = nbt.getFloat("NeedCurrent");
			network.voltage = Voltage.valueOf(nbt.getString("Voltage"));
			ListNBT deviceList = nbt.getList("Devices", 10);
			for (int i = 0; i < deviceList.size(); i++) {
				CompoundNBT entryTag = deviceList.getCompound(i);
				BlockPos position = NBTUtil.readBlockPos(entryTag.getCompound("Position"));
				List<Direction> sides = new ArrayList<Direction>();
				ListNBT sidesList = entryTag.getList("AttachedSides", 8);
				for (int i1 = 0; i1 < sidesList.size(); i1++) {
					sides.add(Direction.byName(sidesList.getString(i1)));
				}
				network.positions.put(position, sides);
			}
			return network;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ElectricityNetwork) {
				ElectricityNetwork other = (ElectricityNetwork) obj;
				return other.getConnectedBlocks().equals(this.getConnectedBlocks());
			}
			return false;
		}
		
	}
	
}
