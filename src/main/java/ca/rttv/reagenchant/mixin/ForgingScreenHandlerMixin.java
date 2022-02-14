package ca.rttv.reagenchant.mixin;

import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin( ForgingScreenHandler.class )
public abstract class ForgingScreenHandlerMixin {
   @ModifyArg( method = "<init>", at = @At( value = "INVOKE", target = "Lnet/minecraft/screen/ForgingScreenHandler$1;<init>(Lnet/minecraft/screen/ForgingScreenHandler;I)V", ordinal = 0 ) )
   private int init(int value) {
      return 4;
   }
}
