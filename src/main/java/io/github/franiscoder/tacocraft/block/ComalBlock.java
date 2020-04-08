package io.github.franiscoder.tacocraft.block;

import io.github.franiscoder.tacocraft.TacoCraft;
import io.github.franiscoder.tacocraft.blockentity.ComalBlockEntity;
import io.github.franiscoder.tacocraft.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class ComalBlock extends BlockWithEntity {
    public static final Identifier ID = new Identifier(TacoCraft.MODID, "comal");

    public ComalBlock(Settings settings) {
        super(settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ComalBlockEntity();
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        Inventory inv = (Inventory) be;

        if (!(be instanceof ComalBlockEntity)) return ActionResult.PASS;


        boolean handIsTortillaDough = player.getStackInHand(hand).copy().getItem() == ModItems.TORTILLA_DOUGH;
        boolean itemIsNotCooking = inv.getInvStack(0).isEmpty();
        boolean furnaceIsLit = world.getBlockState(pos.down()).get(FurnaceBlock.LIT);
        System.out.println(handIsTortillaDough);
        System.out.println(itemIsNotCooking);
        System.out.println(furnaceIsLit);
        if (handIsTortillaDough && itemIsNotCooking && furnaceIsLit) {

            ((Inventory) be).setInvStack(0, ModItems.TORTILLA_DOUGH.getStackForRender().copy());
            player.getStackInHand(hand).decrement(1);
            ((ComalBlockEntity) be).startCooking();
            ((ComalBlockEntity) be).sync();

            return ActionResult.SUCCESS;
        } else if (((ComalBlockEntity) be).isFinished()) {
            ((ComalBlockEntity) be).spawnTortilla();
            ((ComalBlockEntity) be).sync();
        }
        return ActionResult.PASS;
    }

}
