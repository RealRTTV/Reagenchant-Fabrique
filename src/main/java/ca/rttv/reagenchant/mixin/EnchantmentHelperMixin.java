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

    /**
     * this shift took me 3 days to find out,
     * in injecting inbetween of the [STRING LOAD / ALOAD] operation
     * and the [STRING RETURN / ARETURN] operation which means
     * I don't affect the variable because its happening after
     * the value is grabbed, I just had to shift it an opcode before
     */
    @ModifyVariable(method = "generateEnchantments", at = @At(value = "RETURN", ordinal = 1, shift = At.Shift.BEFORE), ordinal = 0)
    private static List<EnchantmentLevelEntry> list(List<EnchantmentLevelEntry> list, Random random, ItemStack stack, int level, boolean treasureAllowed) throws FileNotFoundException {
        if (Reagenchant.reagent != Items.AIR) {
            List<EnchantmentLevelEntry> reagentEntries = getReagentEntries(random, stack.getItem(), level, Reagenchant.reagent);
            if (reagentEntries.size() > 0) {

            }
            List<EnchantmentLevelEntry> concat = new ArrayList<>(reagentEntries);
            concat.addAll(list);
            for (int i = 0; i < concat.size(); i++) {
                for (int j = i; j < concat.size(); j++) {
                    if (i != j && concat.get(i).enchantment == concat.get(j).enchantment) {
                        concat.remove(j);
                    }
                }
            }
            return concat;
        }
        return list;
    }

    /**
     * this is the getReagentEntries method
     * this method in simple does what the
     * vanilla {@link EnchantmentHelper#getPossibleEntries(int, net.minecraft.item.ItemStack, boolean)}
     * method does but for only the entries
     * in the current reagent's {@code enchantments}
     * array, this array can be found at
     * the respective config file.
     *
     * @param random  a mirrored version of the enchantment random
     * @param item    item to be enchanted
     * @param power   bookshelf power for enchant level
     * @param reagent current reagent
     * @return all of the enchantments the item can get from the current reagent (if rng rolls yes)
     * @throws FileNotFoundException FileReader lol
     */
    private static List<EnchantmentLevelEntry> getReagentEntries(Random random, Item item, int power, Item reagent) throws FileNotFoundException {
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

        for (int i = 0; i < reagentObject.getAsJsonArray("enchantments").size(); i++) {
            if (random.nextDouble() <= reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("probability").getAsFloat()) {
                // all the stars have aligned
                // time to replace it!
                Enchantment enchantment = Objects.requireNonNull(Registry.ENCHANTMENT.get(new Identifier(reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("enchantment").getAsString())));
                // checks if enchantment is valid and if it doesn't already exist lol
                if (enchantment.type.isAcceptableItem(item)) {
                    Reagenchant.decrement = reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("reagentCost").getAsInt();

                    // ctrl c ctrl v of mc code, yes ik what it does
                    for (int level = enchantment.getMaxLevel(); level > enchantment.getMinLevel() - 1; --level) {
                        if (power >= enchantment.getMinPower(level) && power <= enchantment.getMaxPower(level)) {
                            newList.add(new EnchantmentLevelEntry(enchantment, level));
                        }
                    }
                }
            }
        }
        return newList;
    }
}
