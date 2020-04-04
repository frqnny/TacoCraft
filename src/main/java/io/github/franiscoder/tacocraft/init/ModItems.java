package io.github.franiscoder.tacocraft.init;

import io.github.franiscoder.tacocraft.TacoCraft;
import io.github.franiscoder.tacocraft.item.TacoHelper;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item CORN_SEED = new AliasedBlockItem(ModBlocks.CORN_BLOCK, new Item.Settings().group(TacoCraft.ITEM_GROUP));
    public static final Item CORN = new Item(new Item.Settings().group(TacoCraft.ITEM_GROUP).food(new FoodComponent.Builder().hunger(1).saturationModifier(0.6F).build()));
    public static final Item TORTILLA_DOUGH = new Item(new Item.Settings().group(TacoCraft.ITEM_GROUP));
    public static final Item TORTILLA = new Item(new Item.Settings().group(TacoCraft.ITEM_GROUP).food(new FoodComponent.Builder().hunger(1).saturationModifier(0.6F).build()));
    public static final Item EMPTY_SHELL = new Item(new Item.Settings().group(TacoCraft.ITEM_GROUP).food(new FoodComponent.Builder().hunger(1).saturationModifier(0.6F).build()));

    public static final Item STEAK_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_BEEF));
    public static final Item CARNITAS_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_PORKCHOP));
    public static final Item AL_PASTOR_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_PORKCHOP, 1, 0.1F));
    public static final Item FISH_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_COD));
    public static final Item CHICKEN_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_CHICKEN));

    public static final Item CRUNCHY_TACO = TacoHelper.createTaco(TacoHelper.createFoodComponent(FoodComponents.COOKED_BEEF));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID,"corn_seed"), CORN_SEED);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID,"corn"), CORN);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "tortilla_dough"), TORTILLA_DOUGH);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID,"tortilla"), TORTILLA);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "empty_shell"), EMPTY_SHELL);

        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "steak_taco"), STEAK_TACO);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "carnitas_taco"), CARNITAS_TACO);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "al_pastor_taco"), AL_PASTOR_TACO);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "fish_taco"), FISH_TACO);
        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "chicken_taco"), CHICKEN_TACO);

        Registry.register(Registry.ITEM, new Identifier(TacoCraft.MODID, "crunchy_taco"), CRUNCHY_TACO);
    }

}
