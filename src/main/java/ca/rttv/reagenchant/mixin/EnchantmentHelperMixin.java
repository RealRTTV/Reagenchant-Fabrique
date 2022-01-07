package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.config.extra.JsonHelper;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @ModifyVariable(method = "generateEnchantments", at = @At(value = "RETURN", ordinal = 1), ordinal = 0)
    private static List<EnchantmentLevelEntry> list(List<EnchantmentLevelEntry> list2, Random random, ItemStack stack, int level, boolean treasureAllowed) throws FileNotFoundException {
        if (Reagenchant.reagent != Items.AIR) {
            List<EnchantmentLevelEntry> concat = new ArrayList<>();
            concat.addAll(extras(random, stack.getItem(), list2, level, Reagenchant.reagent));
            concat.addAll(list2);
            for (int i = 0; i < concat.size(); i++) {
                for (int j = i; j < concat.size(); j++) {
                    if (i != j && concat.get(i).enchantment == concat.get(j).enchantment) {
                        concat.remove(j);
                    }
                }
            }
            return concat;
        }
        return list2;
    }

    private static List<EnchantmentLevelEntry> extras(Random random, Item item, List<EnchantmentLevelEntry> list, int power, Item reagent) throws FileNotFoundException {
        File[] files = Objects.requireNonNull(JsonHelper.getConfigDirectory().listFiles());
        JsonObject reagentObject = null;
        List<EnchantmentLevelEntry> newList = new ArrayList<>();

        for (File file : files) {
            reagentObject = JsonParser.parseString(new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"))).getAsJsonObject();
            String jsonItem = reagentObject.get("item").getAsString();
            if (jsonItem.equals(Registry.ITEM.getId(reagent).toString())) {
                break; // breaks the for loop setting everything perfectly
            }
        }

        if (reagentObject == null) {
            throw new FileNotFoundException("Your reagent is not in an existing file, idk how this error occurred but please add a json under the config folder in the format of the others"); // wow tabnine read my mind
        }

        for (int i = 0; i < reagentObject.getAsJsonArray("enchantments").size(); i++) {
            if (random.nextDouble() <= reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("probability").getAsFloat()) {
                // all the stars have aligned
                // time to replace it!

                reagentArray:
                for (int j = 0; j < reagentObject.getAsJsonArray("enchantments").size(); j++) {
                    Enchantment enchantment = Objects.requireNonNull(Registry.ENCHANTMENT.get(new Identifier(reagentObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString())));
                    // checks if enchantment is valid and if it doesn't already exist lol
                    if (enchantment.type.isAcceptableItem(item)) {

                        for (EnchantmentLevelEntry entry : newList) { // this is to not add multiple of the same enchantment
                            if (entry.enchantment == enchantment) {
                                break reagentArray;
                            }
                        }

                        for (EnchantmentLevelEntry entry : list) { // this is to not add multiple of the same enchantment
                            if (entry.enchantment == enchantment) {
                                break reagentArray;
                            }
                        }

                        Reagenchant.decrement = reagentObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("reagentCost").getAsInt();

                        // ctrl c ctrl v of mc code, yes ik what it does
                        for (int level = enchantment.getMaxLevel(); level > enchantment.getMinLevel() - 1; --level) {
                            if (power >= enchantment.getMinPower(level) && power <= enchantment.getMaxPower(level)) {
                                newList.add(new EnchantmentLevelEntry(enchantment, level));
                                break reagentArray;
                            }
                        }
                    }
                }
            }
        }
        return newList;
    }
}
