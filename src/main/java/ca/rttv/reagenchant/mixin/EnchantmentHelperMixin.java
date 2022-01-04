package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.config.extra.JsonHelper;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    private static Random rand;

    @Inject(method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getPossibleEntries(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;", shift = At.Shift.BEFORE))
    private static void generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        rand = random;
    }

    /**
     * why does intellij want me to add an author tag?
     * @reason im sorry ok ik you don't want me to do this but its just for a little bit ok its not like other stuff uses this method please cheer up ok mixin ♥♥♥
     * @author RTTV
     */
    @Overwrite
    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        Item item = stack.getItem();
        boolean bl = stack.isOf(Items.BOOK);
        Iterator<Enchantment> var6 = Registry.ENCHANTMENT.iterator();

        //noinspection LoopStatementThatDoesntLoop
        while(true) {
            while(true) {
                Enchantment enchantment;
                    do
                        do
                            do {
                                if (!var6.hasNext())
                                    return list;
                                enchantment = var6.next();
                            }
                            while (enchantment.isTreasure() && !treasureAllowed);
                        while (!enchantment.isAvailableForRandomSelection());
                    while (!enchantment.type.isAcceptableItem(item) && !bl);
                if (Reagenchant.reagent != Items.AIR) enchantment = reroll(enchantment, rand.nextDouble(), stack.getItem());

                for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                    if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
                        list.add(new EnchantmentLevelEntry(enchantment, i));
                        break;
                    }
                }
            }
        }
    }

    private static Enchantment reroll(Enchantment instance, double rand, Item item) {
        for (int i = 0; i < JsonHelper.getConfigDirectory().listFiles().length; i++) // loop over every file in the directoy
            try (BufferedReader reader = new BufferedReader(new FileReader(JsonHelper.getConfigDirectory().listFiles()[i]))) {

                JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject();

                if (jsonObject.get("item").getAsString().equals(Registry.ITEM.getId(Reagenchant.reagent).toString())) { // if its the correct file
                    for (int j = 0; j < jsonObject.getAsJsonArray("enchantments").size(); j++) // loop over every enchantment in the array
                        if (rand <= jsonObject.get("enchantments").getAsJsonArray().get(j).getAsJsonObject().get("probability").getAsFloat()) // do rng to check if we should reroll this
                            if (Registry.ENCHANTMENT.get(new Identifier(jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString())).type.isAcceptableItem(item)) // check if valid enchant type
                                if (!Registry.ENCHANTMENT.get(new Identifier(jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString())).equals(instance))
                                    return Registry.ENCHANTMENT.get(new Identifier(jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString())); // gives enchant at the index we at
                    break;
                }
            } catch (IOException e) { e.printStackTrace(); }
        return instance;
    }
}
