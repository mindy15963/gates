package com.github.se7_kn8.gates.tile;

import com.github.se7_kn8.gates.GatesBlocks;
import com.github.se7_kn8.gates.api.CapabilityWirelessNode;
import com.github.se7_kn8.gates.api.IWirelessNode;
import com.github.se7_kn8.gates.block.wireless_redstone.TransmitterBlock;
import com.github.se7_kn8.gates.container.FrequencyContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TransmitterTileEntity extends TileEntity implements INamedContainerProvider {

	private LazyOptional<IWirelessNode> wireless = LazyOptional.of(this::createWireless);

	public TransmitterTileEntity() {
		super(GatesBlocks.TRANSMITTER_TILE_ENTITY_TYPE);
	}

	@Nonnull
	private IWirelessNode createWireless() {
		return new CapabilityWirelessNode.WirelessNodeImpl(1, IWirelessNode.Types.TRANSMITTER) {
			@Override
			public void setPower(int newPower) {
				super.setPower(newPower);
				markDirty();
			}

			@Override
			public void setFrequency(int newFrequency) {
				int oldFrequency = this.getFrequency();
				super.setFrequency(newFrequency);

				if (!world.isRemote) {

					Block block = world.getBlockState(pos).getBlock();
					if (block instanceof TransmitterBlock) {
						((TransmitterBlock) block).updateFrequency((ServerWorld) world, pos, oldFrequency);
					}
				}

				markDirty();
			}
		};
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityWirelessNode.WIRELESS_NODE) {
			return wireless.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(CompoundNBT compound) {
		CompoundNBT wirelessTag = compound.getCompound("wireless");
		wireless.ifPresent(c -> ((INBTSerializable<CompoundNBT>) c).deserializeNBT(wirelessTag));
		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		wireless.ifPresent(c -> {
			CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) c).serializeNBT();
			compound.put("wireless", compoundNBT);
		});
		return super.write(compound);
	}

	@Override
	@Nonnull
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.gates.transmitter");
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
		return new FrequencyContainer(windowId, world, pos, inventory, player);
	}
}
