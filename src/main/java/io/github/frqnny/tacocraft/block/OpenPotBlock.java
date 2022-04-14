package io.github.frqnny.tacocraft.block;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.blockentity.OpenPotBlockEntity;
import io.github.frqnny.tacocraft.init.ModBlocks;
import io.github.frqnny.tacocraft.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OpenPotBlock extends BlockWithEntity {
    public static final Identifier ID = TacoCraft.id("open_pot");

    public OpenPotBlock(Settings settings) {
        super(settings);
    }

    public static boolean isSetupReady(World world, BlockPos pos) {
        boolean isCobblestone = true;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                for (int z = -1; z < 1; z++) {
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (!world.getBlockState(mutable).isOf(Blocks.COBBLESTONE) && x != 0 && z != 0) {
                        isCobblestone = false;
                    }
                }
            }
        }

        return isCobblestone && world.getBlockState(pos.down()).isIn(BlockTags.CAMPFIRES);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OpenPotBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof OpenPotBlockEntity be)) {
                return ActionResult.FAIL;
            }
            if (isSetupReady(world, pos) && be.closed_pancas == 2 && player.getStackInHand(hand).isEmpty() && !be.finished) {
                be.setReady();
                be.updateListeners();
                return ActionResult.SUCCESS;
            } else if (be.closed_pancas < 2 && !be.finished) {
                ItemStack stack = player.getStackInHand(hand);
                Item item = stack.getItem();
                if (item.equals(ModItems.CLOSED_PENCA)) {
                    stack.decrement(1);
                    be.closed_pancas++;
                    be.updateListeners();
                    return ActionResult.SUCCESS;
                }
            } else if (be.finished) {
                be.closed_pancas = 0;
                be.finished = false;
                be.updateListeners();
                ItemStack stack = new ItemStack(ModItems.COOKED_PENCA);
                stack.setCount(2);
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }

        }
        return ActionResult.FAIL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.OPEN_POT_BLOCK_ENTITY, OpenPotBlockEntity::tick);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, AbstractRandom random) {
        if (world.getBlockEntity(pos) instanceof OpenPotBlockEntity be && be.isCooking()) {
            double d = (double) pos.getX() + 0.5D;
            double e = pos.getY();
            double f = (double) pos.getZ() + 0.5D;
            if (random.nextDouble() < 0.1D) {
                world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            world.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 2 + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
            world.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
    }
}
