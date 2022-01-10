package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.access.ItemStackAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccess {

    private Item reagent;
    private int reagentCount;
    private final int[] decrement = new int[3];
    private int slot;

    @Override
    public void setReagent(Item reagent) {
        this.reagent = reagent;
    }

    @Override
    public Item getReagent() {
        return this.reagent;
    }

    @Override
    public void setDecrement(int decrement, int slot) {
        this.decrement[slot] = decrement;
    }

    @Override
    public int[] getDecrement() {
        return decrement;
    }

    @Override
    public int getReagentCount() {
        return reagentCount;
    }

    @Override
    public void setReagentCount(int reagentCount) {
        this.reagentCount = reagentCount;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }
}
