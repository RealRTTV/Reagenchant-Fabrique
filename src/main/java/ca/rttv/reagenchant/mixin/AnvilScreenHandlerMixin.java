package ca.rttv.reagenchant.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( AnvilScreenHandler.class )
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
   @Shadow
   public int repairItemUsage;
   
   public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
      super(type, syncId, playerInventory, context);
   }
   
   @ModifyArg( method = "updateResult", at = @At( value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", ordinal = 5 ) )
   private int changeExperienceCost(int var1) {
      return Math.max(var1 - this.repairItemUsage, 1);
   }
   
   @Inject( method = "canTakeOutput", at = @At( "HEAD" ), cancellable = true )
   private void hasEnoughIron(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
      if (this.repairItemUsage - 1 >= this.input.getStack(3).getCount()) {
         cir.setReturnValue(false);
      }
   }
   
   @Inject( method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At( value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;addProperty(Lnet/minecraft/screen/Property;)Lnet/minecraft/screen/Property;" ) )
   private void init(int syncId, PlayerInventory inventory, ScreenHandlerContext context, CallbackInfo ci) {
      this.addSlot(new Slot(this.input, 3, 6, 6) {
         @Override
         public boolean canInsert(ItemStack stack) {
            return stack.isOf(Items.IRON_INGOT);
         }
      });
   }
   
   @Inject( method = "onTakeOutput", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V", ordinal = 0 ) )
   private void takeIron(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
      this.input.getStack(3).decrement(this.repairItemUsage);
   }
   
   @ModifyArg( method = "onTakeOutput", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V" ), index = 0 )
   private int disableXpRemoval(int levels) {
      return repairItemUsage > 0 ? 0 : levels;
   }
   
   @ModifyArg( method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V"))
   private int swap(int repairCost) {
      return repairItemUsage > 0 ? this.input.getStack(0).getRepairCost() : repairCost;
   }
}
