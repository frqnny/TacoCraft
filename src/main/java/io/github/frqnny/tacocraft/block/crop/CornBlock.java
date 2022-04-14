package io.github.frqnny.tacocraft.block.crop;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.init.ModBlocks;
import io.github.frqnny.tacocraft.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CornBlock extends TallPlantBlock implements Fertilizable {
    //Thanks To Yog, the CornBlock wouldn't be possible
    public static final Identifier ID = TacoCraft.id("corn_block");

    public static final IntProperty AGE = Properties.AGE_7;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape[] BOTTOM_AGES = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    private static final VoxelShape[] TOP_AGE = new VoxelShape[]{
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.0D, 16.0D),
            //up ontil this point the top ones are not used at all
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };
    public static int growthDelay;

    public CornBlock(int delay, AbstractBlock.Settings s) {
        super(s);
        growthDelay = delay;
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(HALF, DoubleBlockHalf.LOWER));
    }

    protected static int getAge(BlockState state) {
        return state.get(AGE);
    }

    public static boolean isMature(BlockState state) {
        return state.get(AGE) >= 7;
    }

    protected static int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.isIn(ModBlocks.FARMLAND);
    }

    public BlockState withAge(int age) {
        return this.getDefaultState().with(AGE, age);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
        super.scheduledTick(state, world, pos, random);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = state.get(AGE);
            int newAge = age + 1;
            if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                if (age < 7 && world.random.nextInt(growthDelay) == 0) {
                    if (newAge < 4) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    } else {
                        world.setBlockState(pos, this.withAge(newAge).with(HALF, DoubleBlockHalf.UPPER), 2);
                    }
                    world.setBlockState(pos.down(), this.withAge(newAge).with(HALF, DoubleBlockHalf.LOWER), 2);
                }
            } else {
                if (age < 7 && world.random.nextInt(growthDelay) == 0) {
                    if (newAge < 4) {
                        world.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 2);
                    } else {
                        world.setBlockState(pos.up(), this.withAge(newAge).with(HALF, DoubleBlockHalf.UPPER), 2);
                    }
                    world.setBlockState(pos, this.withAge(newAge).with(HALF, DoubleBlockHalf.LOWER), 2);
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(AGE).equals(7)) {
            int count = world.random.nextInt(3) + 1;
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.CORN, count));
            if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                world.setBlockState(pos, this.withAge(6).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos.down(), this.withAge(6).with(HALF, DoubleBlockHalf.LOWER));
            } else {
                world.setBlockState(pos.up(), this.withAge(6).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos, this.withAge(6).with(HALF, DoubleBlockHalf.LOWER));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        int newAge = CornBlock.getAge(state) + CornBlock.getGrowthAmount(world);
        if (newAge > 7) {
            newAge = 7;
        }

        if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
            if (age < 7 && world.random.nextInt(growthDelay) == 0) {
                if (newAge < 4) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                } else {
                    world.setBlockState(pos, this.withAge(newAge).with(HALF, DoubleBlockHalf.UPPER));
                }
                world.setBlockState(pos.down(), this.withAge(newAge).with(HALF, DoubleBlockHalf.LOWER));

            }
        } else {
            if (age < 7 && world.random.nextInt(growthDelay) == 0) {
                if (newAge < 4) {
                    world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());

                } else {
                    world.setBlockState(pos.up(), this.withAge(newAge).with(HALF, DoubleBlockHalf.UPPER));
                }
                world.setBlockState(pos, this.withAge(newAge).with(HALF, DoubleBlockHalf.LOWER));
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.CORN_SEED);
    }

    @Override
    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !CornBlock.isMature(state);
    }

    @Override
    public boolean canGrow(World world, AbstractRandom random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, AbstractRandom random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        builder.add(AGE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
            return TOP_AGE[state.get(AGE)];
        } else {
            return BOTTOM_AGES[state.get(AGE)];
        }
    }
}