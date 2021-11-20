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
import net.minecraft.util.ClickType;
import net.minecraft.inventory.StackReference;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

public class CondenserItem extends Item implements VirtualItem {

    public CondenserItem(Item.Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (condenseAll(player)) {
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1F, 1F);
            return TypedActionResult.success(player.getStackInHand(hand));
        } else {
            return TypedActionResult.fail(player.getStackInHand(hand));
        }
    }

    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (otherStack.isEmpty()) {
            if (clickType == ClickType.RIGHT) {
                if (condenseAll(player)) {
                    player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1F, 1F);
                }

                return true;
            } else {
                return false;
            }
        } else {
            if (condensable(otherStack)) {
                ItemStack condensedStack = condenseStack(otherStack);
                player.currentScreenHandler.setCursorStack(condensedStack);
                
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1F, 1F);
                
                return true;
            } else {
                return false;
            }
        }
    }

   public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack itemStack = slot.getStack();
            if (condensable(itemStack)) {
                ItemStack condensedStack = condenseStack(itemStack);
                
                slot.setStack(condensedStack);
                
                player.world.playSoundFromEntity(null, player, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 1F, 1F);
                
                return true;
            } else {
                return false;
            }
        }
   }
    
    public static boolean condenseAll(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        
        boolean bl = false;
        
        for(int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            
            if (condensable(stack)) {
                ItemStack condensedStack = condenseStack(stack);
                
                inventory.removeStack(i);
                if (!inventory.insertStack(condensedStack.copy())) {
                    player.dropItem(condensedStack, false);
                }
                
                bl = true;
            }
        }
        
        return bl;
    }
    
    public static ItemStack condenseStack(ItemStack stack) {
        if (condensable(stack)) {
            ItemStack condensedStack = new ItemStack(Condenser.COMPRESSED);
            NbtCompound tag = condensedStack.getOrCreateNbt();
            
            tag.put("Compressed", stack.writeNbt(new NbtCompound()));
            
            return condensedStack;
        } else {
            return stack;
        }
    }
    
    public static boolean condensable(ItemStack stack) {
        if (stack.isEmpty()) return false;

        int maxCount = stack.getMaxCount();
        return maxCount > 1 && stack.getCount() == maxCount;
    }

    @Override
    public Item getVirtualItem() {
        return Items.QUARTZ;
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack in, ServerPlayerEntity player) {
        ItemStack out = ItemHelper.createBasicVirtualItemStack(in, player);
        NbtCompound tag = out.getOrCreateNbt();
        tag.putInt("CustomModelData", 2);
        return out;
    }
}
