package io.github.frqnny.tacocraft.blockentity;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.init.ModBlocks;
import io.github.frqnny.tacocraft.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ComalBlockEntity extends BlockEntity {
    boolean doneCooking = false;
    boolean canRender = false;
    private int cookTime = -1;
    private boolean hasTortilla = false;

    public ComalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.COMAL_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ComalBlockEntity be) {
        if (!world.isClient) {

            if (be.doneCooking || be.isCooking()) {
                if (be.cookTime % 15 == 0) {
                    be.updateListeners();
                }
            }


            if (be.isCooking()) {
                be.cookTime--;
            } else if (be.cookTime == 0) {
                be.doneCooking = true;
                be.cookTime--;
                be.hasTortilla = false;
                be.updateListeners();
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.putShort("CookTime", (short) this.cookTime);
        tag.putBoolean("HasTortilla", hasTortilla);
        tag.putBoolean("CanRender", canRender);
        tag.putBoolean("DoneCooking", doneCooking);

    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.cookTime = tag.getShort("CookTime");
        hasTortilla = tag.getBoolean("HasTortilla");
        canRender = tag.getBoolean("CanRender");
        doneCooking = tag.getBoolean("DoneCooking");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::createNbt);
    }


    public void startCooking() {
        cookTime = TacoCraft.CONFIG.tortilla_cook_time;
        setCanRender();
        updateListeners();
    }

    public boolean isCooking() {
        return cookTime > 0;
    }

    public void spawnTortilla() {
        if (doneCooking) {
            if (!Objects.requireNonNull(this.getWorld()).isClient) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.TORTILLA).copy());
            }
            this.markDirty();
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            doneCooking = false;
            setCanRender();
        }
    }

    public boolean isFinished() {
        return doneCooking;
    }

    public ItemStack stackToRender() {
        return canRender ? doneCooking ? new ItemStack(ModItems.TORTILLA) : new ItemStack(ModItems.TORTILLA_DOUGH) : ItemStack.EMPTY;
    }

    public void setCanRender() {
        canRender = !canRender;
    }

    public void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

}
