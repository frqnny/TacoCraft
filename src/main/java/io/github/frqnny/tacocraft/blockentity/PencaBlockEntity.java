package io.github.frqnny.tacocraft.blockentity;

import io.github.frqnny.tacocraft.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PencaBlockEntity extends BlockEntity {
    public int steaks = 0;
    public int porkchops = 0;

    public PencaBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.PENCA_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        porkchops = tag.getInt("porkchops");
        steaks = tag.getInt("steaks");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("porkchops", porkchops);
        tag.putInt("steaks", steaks);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::createNbt);
    }

    public void sync() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }
}
