package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin {
    @Inject(method = "drawBackground", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER))
    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        RenderSystem.setShaderTexture (0, (Reagenchant.reagent == Items.AIR ? Reagenchant.withoutReagent : Reagenchant.withReagent));
    }

    @ModifyArgs(
            method = "drawBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/EnchantmentScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
    )
    private void shiftEnchantmentsTexture(Args args) {
        args.set(1, (int) args.get(1) + 1);
        if ((int) args.get(5) == 108) {
            args.set(5, 109);
        }
    }

    @ModifyArgs(
            method = "drawBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I")
    )
    private void colorText(Args args) {
        args.set(2, (float) args.get(2) + 1f);
        if (true) {
            args.set(4, new Color(
                    0xFF - ((int) args.get(4) >> 16 & 0xFF),
                    0xFF - ((int) args.get(4) >> 8 & 0xFF),
                    0xFF - ((int) args.get(4) >> 0 & 0xFF)
            ).getRGB());
        }
    }
}
