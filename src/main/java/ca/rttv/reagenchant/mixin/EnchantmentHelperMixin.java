package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.config.extra.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinLevel()I"))
    private static int getMinLevel(Enchantment instance, int level) {
//        String enchantment = Registry.ENCHANTMENT.getId(instance).toString();
//        File[] files = JsonHelper.getConfigDirectory().listFiles();
//        for (File file : files) {
//            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject();
//                for (int j = 0; j < jsonObject.getAsJsonArray("enchantments").size(); j++)
//                    if (jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString().equals(enchantment)
//                     && Registry.ITEM.getId(Reagenchant.reagent).toString().equals(jsonObject.get("item").getAsString()))
//                        return jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("minimumEnchantmentLevel").getAsInt();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return level;
    }

    @Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
    private static int getMaxLevel(Enchantment instance, int level) {
//        String enchantment = Registry.ENCHANTMENT.getId(instance).toString();
//        File[] files = JsonHelper.getConfigDirectory().listFiles();
//        for (File file : files) {
//            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject();
//                for (int j = 0; j < jsonObject.getAsJsonArray("enchantments").size(); j++)
//                    if (jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("enchantment").getAsString().equals(enchantment)
//                     && Registry.ITEM.getId(Reagenchant.reagent).toString().equals(jsonObject.get("item").getAsString()))
//                        return jsonObject.getAsJsonArray("enchantments").get(j).getAsJsonObject().get("maximumEnchantmentLevel").getAsInt();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return level;
    }
}
