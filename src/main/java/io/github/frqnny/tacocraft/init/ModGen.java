package io.github.frqnny.tacocraft.init;

import io.github.frqnny.tacocraft.TacoCraft;
import io.github.frqnny.tacocraft.world.CornFieldFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;

public class ModGen {
    public static final Feature<DefaultFeatureConfig> CORN_FEATURE = new CornFieldFeature();
    public static final ConfiguredFeature<?, ?> CONFIGURED_CORN_FEATURE = new ConfiguredFeature<>(CORN_FEATURE, DefaultFeatureConfig.DEFAULT);
    public static PlacedFeature PLACED_CORN;

    public static void init() {
        Registry.register(Registry.FEATURE, TacoCraft.id("feature"), CORN_FEATURE);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, TacoCraft.id("feature_configured"), CONFIGURED_CORN_FEATURE);

        PLACED_CORN = new PlacedFeature(getEntry(BuiltinRegistries.CONFIGURED_FEATURE, CONFIGURED_CORN_FEATURE),
                List.of(RarityFilterPlacementModifier.of(TacoCraft.CONFIG.corn_gen_chance), SquarePlacementModifier.of(), PlacedFeatures.BOTTOM_TO_120_RANGE, BiomePlacementModifier.of())
        );
        Registry.register(BuiltinRegistries.PLACED_FEATURE, TacoCraft.id("feature_places"), PLACED_CORN);
        BiomeModifications
                .create(TacoCraft.id("feature"))
                .add(
                        ModificationPhase.ADDITIONS,
                        BiomeSelectors.tag(BiomeTags.IS_SAVANNA).and(BiomeSelectors.tag(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE)),
                        (biomeModificationContext -> biomeModificationContext.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, PLACED_CORN))
                );

        /*
        DynamicRegistrySetupCallback.EVENT.register(registryManager -> {
            Registry<StructurePool>  structurePools = registryManager.get(Registry.STRUCTURE_POOL_KEY);
            RegistryEntryAddedCallback.event(structurePools).register((rawId, id, object) -> {
                Registry<StructureProcessorList> structureProcessorLists =  registryManager.get(Registry.STRUCTURE_PROCESSOR_LIST_KEY);
                RegistryEntry<StructureProcessorList> list =structureProcessorLists.getEntry(RegistryKey.of(Registry.STRUCTURE_PROCESSOR_LIST_KEY, new Identifier("farm_plains"))).get();
                StructurePoolElement element = StructurePoolElement.ofProcessedLegacySingle("tacocraft:plains_corn_farm", list).apply(StructurePool.Projection.RIGID);

                FabricStructurePool pool = new FabricStructurePool(object);

                pool.addStructurePoolElement(element, 100);
            });
        });

         */

    }


    public static <T> RegistryEntry<T> getEntry(Registry<T> registry, T value) {
        return registry.getEntry(registry.getKey(value).orElseThrow()).orElseThrow();
    }
}
