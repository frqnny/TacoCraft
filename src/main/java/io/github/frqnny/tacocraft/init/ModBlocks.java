package io.github.frqnny.tacocraft.init;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.block.*;
import io.github.frqnny.tacocraft.block.crop.CornBlock;
import io.github.frqnny.tacocraft.blockentity.ComalBlockEntity;
import io.github.frqnny.tacocraft.blockentity.FurnaceBlockEntity;
import io.github.frqnny.tacocraft.blockentity.OpenPotBlockEntity;
import io.github.frqnny.tacocraft.blockentity.PencaBlockEntity;
import io.github.frqnny.tacocraft.client.render.ComalBlockEntityRenderer;
import io.github.frqnny.tacocraft.client.render.OpenPotBlockEntityRenderer;
import io.github.frqnny.tacocraft.client.render.PencaBlockEntityRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final Block FURNACE_BLOCK = new FurnaceBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(3.5F).lightLevel(createLightLevelFromBlockState()).requiresTool());
    public static final Block COMAL = new ComalBlock(FabricBlockSettings.of(Material.DECORATION));
    public static final Block CORN_BLOCK = new CornBlock(8, FabricBlockSettings.of(Material.PLANT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
    public static final Block CORN_BRICK = new Block(FabricBlockSettings.copyOf(Blocks.BRICKS).requiresTool());
    public static final Block OPEN_POT = new OpenPotBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON).requiresTool());
    public static final Block PENCA = new PencaBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES));
    public static final Block TACO_BOWL = new TacoBowlBlock(FabricBlockSettings.copyOf(Blocks.CAKE));
    public static final TagKey<Block> FARMLAND = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "farmlands"));

    public static void init() {
        Registry.register(Registry.BLOCK, FurnaceBlock.ID, FURNACE_BLOCK);
        Registry.register(Registry.BLOCK, ComalBlock.ID, COMAL);
        Registry.register(Registry.BLOCK, CornBlock.ID, CORN_BLOCK);
        Registry.register(Registry.BLOCK, OpenPotBlock.ID, OPEN_POT);
        Registry.register(Registry.BLOCK, PencaBlock.ID, PENCA);
        Registry.register(Registry.BLOCK, TacoCraft.id("corn_brick"), CORN_BRICK);
        Registry.register(Registry.BLOCK, TacoBowlBlock.ID, TACO_BOWL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, FurnaceBlock.ID, FURNACE_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ComalBlock.ID, COMAL_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, OpenPotBlock.ID, OPEN_POT_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, PencaBlock.ID, PENCA_BLOCK_ENTITY);
    }    public static final BlockEntityType<FurnaceBlockEntity> FURNACE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(FurnaceBlockEntity::new, FURNACE_BLOCK).build();

    public static void clientInit() {
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.COMAL_BLOCK_ENTITY, ComalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.PENCA_BLOCK_ENTITY, PencaBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.OPEN_POT_BLOCK_ENTITY, OpenPotBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMAL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CORN_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CORN_BLOCK, RenderLayer.getCutout());
    }

    private static ToIntFunction<BlockState> createLightLevelFromBlockState() {
        return (blockState) -> (Boolean) blockState.get(Properties.LIT) ? 13 : 0;
    }




    public static final BlockEntityType<ComalBlockEntity> COMAL_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ComalBlockEntity::new, COMAL).build();


    public static final BlockEntityType<OpenPotBlockEntity> OPEN_POT_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(OpenPotBlockEntity::new, OPEN_POT).build();


    public static final BlockEntityType<PencaBlockEntity> PENCA_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(PencaBlockEntity::new, PENCA).build();


}
