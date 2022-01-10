package ca.rttv.reagenchant.access;

import net.minecraft.item.Item;

public interface ItemStackAccess {
    void setReagent(Item reagent);

    Item getReagent();

    void setDecrement(int decrement, int slot);

    int[] getDecrement();

    int getReagentCount();

    void setReagentCount(int reagentCount);

    int getSlot();

    void setSlot(int slot);
}
