package io.github.frqnny.tacocraft.block;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.blockentity.ComalBlockEntity;
import io.github.frqnny.tacocraft.init.ModBlocks;
import io.github.frqnny.tacocraft.init.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class ComalBlock extends BlockWithEntity {
    public static final Identifier ID = TacoCraft.id("comal");

    public ComalBlock(Settings settings) {
        super(settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComalBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            return ActionResult.PASS;
        }
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof ComalBlockEntity comal) {

            boolean handIsTortillaDough = player.getStackInHand(hand).copy().getItem() == ModItems.TORTILLA_DOUGH;
            boolean itemIsNotCooking = !comal.isCooking();
            boolean furnaceIsLit = world.getBlockState(pos.down()).get(FurnaceBlock.LIT);

            if (handIsTortillaDough && itemIsNotCooking && furnaceIsLit) {

                player.getStackInHand(hand).decrement(1);
                comal.startCooking();
                comal.updateListeners();

                return ActionResult.SUCCESS;
            } else if (comal.isFinished()) {
                comal.spawnTortilla();
                comal.updateListeners();
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.COMAL_BLOCK_ENTITY, ComalBlockEntity::tick);
    }
}
