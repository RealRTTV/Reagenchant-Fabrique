package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.config.extra.JsonHelper;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @Shadow @Final private Inventory inventory;

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0), index = 0)
    private Slot changeItemEnchantX(Slot slot) {
        return new Slot(slot.inventory, slot.getIndex(), slot.x - 9, slot.y);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 1), index = 0)
    private Slot changeItemLapisX(Slot slot) {
        return new Slot(slot.inventory, slot.getIndex(), slot.x - 11, slot.y);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler$1;<init>(Lnet/minecraft/screen/EnchantmentScreenHandler;I)V", ordinal = 0))
    private int changeMaxSlotCount(int size) {
        return 3;
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 1))
    private void addReagentSlot(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
        ((ScreenHandler) (Object) this).addSlot(new Slot(this.inventory, 2, 42, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                boolean isValidItem = false;
                for (File file : Objects.requireNonNull(JsonHelper.getConfigDirectory().listFiles())) {
                    try {
                        isValidItem = Registry.ITEM.getId(stack.getItem()).toString().equals(JsonParser.parseString(new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"))).getAsJsonObject().get("item").getAsString());
                    } catch (FileNotFoundException e) { break; }
                    if (isValidItem) break;
                }
                return isValidItem;
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
    }

    @Inject(method = "onContentChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private void onContentChanged(Inventory inventory, CallbackInfo ci) {
        Reagenchant.reagent = inventory.getStack(2).getItem();
    }

    @Inject(method = "method_17410", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"))
    private void onButtonClick(ItemStack itemStack, int i, PlayerEntity playerEntity, int j, ItemStack itemStack2, World world, BlockPos pos, CallbackInfo ci) {
        ItemStack stack = this.inventory.getStack(2);

        if (!playerEntity.getAbilities().creativeMode) {
            stack.decrement(Reagenchant.decrement);
            if (stack.isEmpty()) {
                this.inventory.setStack(2, ItemStack.EMPTY);
            }
        }
    }
}
