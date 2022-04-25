package io.github.frqnny.tacocraft.blockentity;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.github.frqnny.tacocraft.block.inventory.FurnaceInventory;
import io.github.frqnny.tacocraft.client.gui.FurnaceGUI;
import io.github.frqnny.tacocraft.init.ModBlocks;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FurnaceBlockEntity extends BlockEntity implements FurnaceInventory, InventoryProvider, PropertyDelegateHolder, ExtendedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public int burnTime = 0;
    public int fuelTime = 0;
    public PropertyDelegate propertyDelegate = new PropertyDelegate() {

        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> fuelTime;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
            }

        }

        public int size() {
            return 2;
        }
    };

    public FurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.FURNACE_BLOCK_ENTITY, pos, state);
    }

    protected static int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, FurnaceBlockEntity be) {
        boolean markDirtyAfterTick = false;

        boolean readyToBurn = !be.isBurning() && AbstractFurnaceBlockEntity.canUseAsFuel(be.items.get(0));
        boolean isBurning = be.isBurning();
        boolean doneBurning = !be.isBurning() && be.items.get(0) == ItemStack.EMPTY;

        if (isBurning) {
            be.burnTime--;
        }

        if (!world.isClient) {
            if (readyToBurn) {
                be.fuelTime = getFuelTime(be.items.get(0));
                be.burnTime = be.fuelTime;
                ItemStack stack = be.items.get(0);
                Item item = stack.getItem();
                stack.decrement(1);
                if (stack.isEmpty()) {
                    Item item2 = item.getRecipeRemainder();
                    be.items.set(0, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                }
                world.setBlockState(pos, world.getBlockState(pos).with(AbstractFurnaceBlock.LIT, true), 3);
            } else if (doneBurning) {
                world.setBlockState(pos, world.getBlockState(pos).with(AbstractFurnaceBlock.LIT, false), 3);
            }

            if (isBurning != be.isBurning()) {
                markDirtyAfterTick = true;
                world.setBlockState(pos, world.getBlockState(pos).with(AbstractFurnaceBlock.LIT, be.isBurning()), 3);
            }
        }

        if (markDirtyAfterTick) {
            be.markDirty();
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.burnTime = tag.getShort("BurnTime");
        this.fuelTime = tag.getShort("FuelTime");
        Inventories.readNbt(tag, items);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        tag.putShort("BurnTime", (short) this.burnTime);
        tag.putShort("FuelTime", (short) this.burnTime);
        Inventories.writeNbt(tag, items);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FurnaceGUI(syncId, inv, ScreenHandlerContext.create(this.world, this.pos));
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[items.size()];
        int resultLength = result.length;
        for (int i = 0; i < resultLength; i++) {
            result[i] = i;
        }

        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }


}
