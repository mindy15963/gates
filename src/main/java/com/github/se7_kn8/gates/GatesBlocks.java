package com.github.se7_kn8.gates;

import com.github.se7_kn8.gates.block.CustomDetector;
import com.github.se7_kn8.gates.block.CustomRepeater;
import com.github.se7_kn8.gates.block.OneInputLogicGate;
import com.github.se7_kn8.gates.block.TwoInputLogicGate;
import com.github.se7_kn8.gates.block.wireless_redstone.ReceiverBlock;
import com.github.se7_kn8.gates.block.wireless_redstone.TransmitterBlock;
import com.github.se7_kn8.gates.block.wireless_redstone.WirelessRedstoneLamp;
import com.github.se7_kn8.gates.tile.CustomDetectorTile;
import com.github.se7_kn8.gates.tile.ReceiverTileEntity;
import com.github.se7_kn8.gates.tile.TransmitterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Gates.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GatesBlocks {

	public static final Random rand = new Random(System.currentTimeMillis());

	public static final Map<ResourceLocation, Block> BLOCKS = new HashMap<>();
	public static final List<TileEntityType<? extends TileEntity>> TILE_ENTITIES = new ArrayList<>();

	public static final Block AND_GATE = addBlock("and_gate", new TwoInputLogicGate((x1, x2) -> x1 && x2), Gates.GATES_ITEM_GROUP);
	public static final Block OR_GATE = addBlock("or_gate", new TwoInputLogicGate((x1, x2) -> x1 || x2), Gates.GATES_ITEM_GROUP);
	public static final Block NAND_GATE = addBlock("nand_gate", new TwoInputLogicGate((x1, x2) -> !(x1 && x2)), Gates.GATES_ITEM_GROUP);
	public static final Block NOR_GATE = addBlock("nor_gate", new TwoInputLogicGate((x1, x2) -> !(x1 || x2)), Gates.GATES_ITEM_GROUP);
	public static final Block XOR_GATE = addBlock("xor_gate", new TwoInputLogicGate((x1, x2) -> x1 ^ x2), Gates.GATES_ITEM_GROUP);
	public static final Block XNOR_GATE = addBlock("xnor_gate", new TwoInputLogicGate((x1, x2) -> !(x1 ^ x2)), Gates.GATES_ITEM_GROUP);

	public static final Block NOT_GATE = addBlock("not_gate", new OneInputLogicGate(x1 -> !x1), Gates.GATES_ITEM_GROUP);

	public static final Block FAST_REPEATER = addBlock("fast_repeater", new CustomRepeater(1), Gates.GATES_ITEM_GROUP);
	public static final Block SLOW_REPEATER = addBlock("slow_repeater", new CustomRepeater(4), Gates.GATES_ITEM_GROUP);

	public static final Block WIRELESS_REDSTONE_RECEIVER = addBlock("receiver", new ReceiverBlock(), Gates.GATES_ITEM_GROUP);
	public static final Block WIRELESS_REDSTONE_TRANSMITTER = addBlock("transmitter", new TransmitterBlock(), Gates.GATES_ITEM_GROUP);

	public static final Block WIRELESS_REDSTONE_LAMP = addBlock("wireless_redstone_lamp", new WirelessRedstoneLamp(), Gates.GATES_ITEM_GROUP);

	public static final Block RAIN_DETECTOR = addBlock("rain_detector", new CustomDetector((blockState, world, blockPos) -> world.isRainingAt(blockPos.up(2)) ? 15 : 0), Gates.GATES_ITEM_GROUP);
	public static final Block THUNDER_DETECTOR = addBlock("thunder_detector", new CustomDetector((blockState, world, blockPos) -> world.isThundering() ? 15 : 0), Gates.GATES_ITEM_GROUP);

	public static final TileEntityType<CustomDetectorTile> RAIN_DETECTOR_TILE_ENTITY = addTileEntity("rain_detector", CustomDetectorTile::new, RAIN_DETECTOR, THUNDER_DETECTOR);

	public static final TileEntityType<ReceiverTileEntity> RECEIVER_TILE_ENTITY_TYPE = addTileEntity("receiver", ReceiverTileEntity::new, WIRELESS_REDSTONE_RECEIVER);
	public static final TileEntityType<TransmitterTileEntity> TRANSMITTER_TILE_ENTITY_TYPE = addTileEntity("transmitter", TransmitterTileEntity::new, WIRELESS_REDSTONE_TRANSMITTER);


	@SubscribeEvent
	public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> tileEntityTypeRegister) {
		tileEntityTypeRegister.getRegistry().registerAll(TILE_ENTITIES.toArray(new TileEntityType[0]));
	}


	@SubscribeEvent
	public static void onBlocksRegistry(RegistryEvent.Register<Block> blockRegistryEvent) {
		for (ResourceLocation location : GatesBlocks.BLOCKS.keySet()) {
			Block block = GatesBlocks.BLOCKS.get(location);
			block.setRegistryName(location);
			blockRegistryEvent.getRegistry().register(block);
		}
	}

	private static Block addBlock(String name, Block block, ItemGroup tab) {
		GatesBlocks.BLOCKS.put(new ResourceLocation(Gates.MODID, name), block);
		BlockItem itemBlock = new BlockItem(block, new Item.Properties().group(tab));
		itemBlock.addToBlockToItemMap(Item.BLOCK_TO_ITEM, itemBlock);
		GatesItems.ITEMS.put(new ResourceLocation(Gates.MODID, name), itemBlock);
		return block;
	}

	private static <T extends TileEntity> TileEntityType<T> addTileEntity(String name, Supplier<T> tileEntitySupplier, Block... validBlocks) {
		TileEntityType<T> type = TileEntityType.Builder.create(tileEntitySupplier, validBlocks).build(null);
		type.setRegistryName(name);
		TILE_ENTITIES.add(type);
		return type;
	}

}
