package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.access.EnchantmentScreenHandlerDuck;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentScreen.class)
public abstract class EnchantmentScreenMixin extends HandledScreen {
    public EnchantmentScreenMixin(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawBackground", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER))
    private void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        RenderSystem.setShaderTexture (0, ((EnchantmentScreenHandlerDuck) this.handler).getReagentItem() == Items.AIR ? Reagenchant.withoutReagent : Reagenchant.withReagent);
    }
}
