package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin( EnchantmentScreen.class )
public abstract class EnchantmentScreenMixin extends HandledScreen<EnchantmentScreenHandler> {
   
   public EnchantmentScreenMixin(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
   }
   
   @ModifyArg( method = "drawBackground", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I" ), index = 2 )
   private float colorText(float x) {
      return x + 1.0f;
   }
   
   @Inject( method = "drawBackground", at = @At( value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER ) )
   private void setCorrectReagentTexture(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
      RenderSystem.setShaderTexture(0, (handler.inventory.getStack(2).getItem() == Items.AIR ? Reagenchant.withoutReagent : Reagenchant.withReagent));
   }
   
   @ModifyConstant( method = "mouseClicked", constant = @Constant( doubleValue = 0.0d, ordinal = 0 ) )
   private double shiftEnchantmentClickXPart1(double constant) {
      return constant + 1;
   }
   
   @ModifyConstant( method = "mouseClicked", constant = @Constant( doubleValue = 108.0 ) )
   private double shiftEnchantmentClickXPart2(double constant) {
      return constant + 2;
   }
   
   @ModifyConstant( method = "drawBackground", constant = @Constant( intValue = 108 ) )
   private int shiftEnchantmentHoverXPart1(int constant) {
      return constant + 2;
   }
   
   @SuppressWarnings( "MixinAnnotationTarget" )
   @ModifyConstant( method = "drawBackground", constant = @Constant( expandZeroConditions = Constant.Condition.GREATER_THAN_OR_EQUAL_TO_ZERO, ordinal = 0 ), slice = @Slice( from = @At( value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawTrimmed(Lnet/minecraft/text/StringVisitable;IIII)V", ordinal = 0 ) ) )
   private int shiftEnchantmentHoverXPart2(int constant) {
      return constant + 1;
   }
   
   @ModifyConstant( method = "render", constant = @Constant( intValue = 108 ) )
   private int shiftEnchantmentTooltipXPart1(int constant) {
      return constant - 1;
   }
   
   @ModifyConstant( method = "render", constant = @Constant( intValue = 60 ) )
   private int shiftEnchantmentTooltipXPart2(int constant) {
      return constant + 2;
   }
   
   @ModifyConstant( method = "render", constant = @Constant( intValue = 14 ) )
   private int shiftEnchantmentTooltipYPart2(int constant) {
      return constant + 1;
   }
   
   @ModifyArgs( method = "drawBackground", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V" ), slice = @Slice( from = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1 ) ) )
   private void shiftEnchantmentsTexture(Args args) {
      args.set(1, (int) args.get(1) + 1);
      if ((int) args.get(5) == 108) {
         args.set(5, 109);
      }
   }
}
