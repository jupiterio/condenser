package io.github.jupiterio.condenser;

import eu.pb4.polymer.item.VirtualItem;
import eu.pb4.polymer.item.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class CompressedItem extends Item implements VirtualItem {

    public CompressedItem(Item.Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemStack compStack = getCompressed(stack);
        
        if (compStack.isEmpty()) {
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
        } else {
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1F, 1F);
        }
        
        if (stack.getCount() == 1 && !player.isCreative()) {
            return TypedActionResult.success(compStack);
        } else {
            if (!player.getInventory().insertStack(compStack.copy())) {
                player.dropItem(compStack, false);
            }

            stack.decrement(1);

            return TypedActionResult.success(stack);
        }
    }

    public Text getName(ItemStack stack) {
        ItemStack compStack = getDeepCompressed(stack);
        int compression = getCompression(stack);

        if (!compStack.isEmpty()) {
            return new TranslatableText("item.condenser.compressed.n", compression, compStack.getName());
        } else {
            if (compression == 0) {
                return new TranslatableText("item.condenser.pure_compression");
            } else if (compression <= 10) {
                return new TranslatableText("item.condenser.pure_compression.n", compression);
            } else {
                return new TranslatableText("item.condenser.black_hole");
            }
        }
    }

    public ItemStack getCompressed(ItemStack stack) {
        NbtCompound compTag = stack.getOrCreateNbt().getCompound("Compressed");
        return ItemStack.fromNbt(compTag);
    }

    public int getCompression(ItemStack stack) {
        ItemStack compStack = getCompressed(stack);
        if (!compStack.isEmpty()) {
            if (compStack.getItem() == Condenser.COMPRESSED) {
                return 1 + getCompression(compStack);
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    public ItemStack getDeepCompressed(ItemStack stack) {
        ItemStack compStack = getCompressed(stack);
        if (!compStack.isEmpty() && compStack.getItem() == Condenser.COMPRESSED) {
            return getDeepCompressed(compStack);
        } else {
            return compStack;
        }
    }

    @Override
    public Item getVirtualItem() {
        return Items.BLACK_DYE;
    }

    @Override
    public Item getVirtualItem(ItemStack in, ServerPlayerEntity player) {
        var compStack = getDeepCompressed(in);

        if (compStack.isEmpty()) {
            return Items.BLACK_DYE;
        } else {
            var item = compStack.getItem();
            if (item instanceof VirtualItem virtual) {
                return virtual.getVirtualItem(compStack, player);
            } else {
                return compStack.getItem();
            }
        }
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack in, ServerPlayerEntity player) {
        var out = ItemHelper.createBasicVirtualItemStack(in, player);
        var tag = out.getOrCreateNbt();
        tag.putBoolean("IsCompressed", true);
        if (getDeepCompressed(in).isEmpty()) {
            tag.putInt("CustomModelData", 1);
        }
        return out;
    }
}
