package io.github.jupiterio.condenser;

import eu.pb4.polymer.item.VirtualItem;
import eu.pb4.polymer.item.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InfusedQuartzItem extends Item implements VirtualItem {

    public InfusedQuartzItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public Item getVirtualItem() {
        return Items.QUARTZ;
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack in, ServerPlayerEntity player) {
        ItemStack out = ItemHelper.createBasicVirtualItemStack(in, player);
        NbtCompound tag = out.getOrCreateNbt();
        tag.putInt("CustomModelData", 1);
        return out;
    }
}
