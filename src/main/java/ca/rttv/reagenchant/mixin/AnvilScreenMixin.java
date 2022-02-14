package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin( AnvilScreen.class )
public abstract class AnvilScreenMixin extends HandledScreen<AnvilScreenHandler> {
   
   public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title);
   }
   
   @ModifyArg( method = "<init>", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ForgingScreen;<init>(Lnet/minecraft/screen/ForgingScreenHandler;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/text/Text;Lnet/minecraft/util/Identifier;)V" ), index = 3 )
   private static Identifier init(Identifier value) {
      return Reagenchant.anvil;
   }
   
   @ModifyVariable( method = "drawForeground", at = @At( value = "STORE", ordinal = 2 ), index = 6 )
   private Text ironCostText(Text value) {
      if (this.handler.repairItemUsage > 0) {
         return new LiteralText(String.format("Iron Cost: %d", this.handler.repairItemUsage));
      }
      return value;
   }
}
