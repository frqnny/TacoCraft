package io.github.frqnny.tacocraft.blockentity;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OpenPotBlockEntity extends BlockEntity {
    public int closed_pancas = 0;
    public boolean finished = false;
    public int tick = 0;
    protected boolean ready = false;
    protected boolean cooking = false;

    public OpenPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.OPEN_POT_BLOCK_ENTITY, pos, state);
    }


    public static void tick(World world, BlockPos pos, BlockState state, OpenPotBlockEntity be) {
        if (!world.isClient()) {
            if (be.ready) {
                if (be.canStartCooking()) {
                    be.tick = TacoCraft.CONFIG.penca_cook_time;
                    be.ready = false;
                    be.cooking = true;

                    if (!world.isClient) {
                        be.updateListeners();
                    }
                }
            } else if (be.cooking) {
                be.tick--;
                if (be.tick == 0) {
                    be.cooking = false;
                    be.finished = true;
                    world.setBlockState(pos.up(), Blocks.COARSE_DIRT.getDefaultState());
                    if (!world.isClient) {
                        be.updateListeners();
                    }
                }
            }
        }
    }


    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        closed_pancas = tag.getInt("ClosedPancas");
        tick = tag.getInt("Tick");
        ready = tag.getBoolean("Ready");
        cooking = tag.getBoolean("Cooking");
        finished = tag.getBoolean("Finished");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("ClosedPancas", closed_pancas);
        tag.putInt("Tick", tick);
        tag.putBoolean("Ready", ready);
        tag.putBoolean("Finished", finished);
        tag.putBoolean("Cooking", cooking);
    }


    public void setReady() {
        ready = true;
    }

    public boolean canStartCooking() {

        boolean isDirt = true;
        BlockPos.Mutable mutable = pos.mutableCopy();
        int originalX = mutable.getX();
        int y = mutable.getY() + 1;
        int originalZ = mutable.getZ();
        for (int x = -1; x < 1; x++) {
            for (int z = -1; z < 1; z++) {
                mutable.set(originalX + x, y, originalZ + z);
                if (!world.getBlockState(mutable).isOf(Blocks.DIRT) && !world.getBlockState(mutable).isOf(Blocks.GRASS_BLOCK)) {
                    isDirt = false;
                }
            }
        }

        return isDirt;
    }


    public boolean isCooking() {
        return cooking;
    }

    public void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::createNbt);
    }
}
