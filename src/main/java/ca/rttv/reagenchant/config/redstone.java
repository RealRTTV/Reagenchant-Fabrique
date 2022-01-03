package ca.rttv.reagenchant.config;

import ca.rttv.reagenchant.Reagenchant;
import ca.rttv.reagenchant.config.extra.JsonHelper;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.Collectors;

public class redstone {
    public static File configFile = new File(JsonHelper.getConfigDirectory(), "redstone.json");
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static JsonObject jsonObject;

    public static void loadConfigs() {
        File dir = JsonHelper.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory() || dir.mkdirs()))
            if (!configFile.exists())
                JsonHelper.writeJsonToFile(generateDefaultConfig(), configFile, GSON);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining("\n"))).getAsJsonObject(); // this is what matters
                if (jsonObject.get("version") == null || jsonObject.get("version").getAsFloat() != Reagenchant.configVersion)
                    JsonHelper.writeJsonToFile(generateDefaultConfig(), configFile, GSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static JsonObject generateDefaultConfig() {
        JsonObject json = new JsonObject();

        json.addProperty("version", Reagenchant.configVersion);

        json.addProperty("item", "minecraft:redstone");

        JsonArray enchantments = new JsonArray();

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:efficiency");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 5);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:power");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 5);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:thorns");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 3);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:sharpness");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 5);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:sweeping");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 3);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enchantment", "minecraft:quick_charge");
            jsonObject.addProperty("minimumEnchantmentLevel", 1);
            jsonObject.addProperty("maximumEnchantmentLevel", 3);
            jsonObject.addProperty("probability", 0.5f);
            jsonObject.addProperty("reagentCost", 1);
            enchantments.add(jsonObject);
        }

        json.add("enchantments", enchantments);

        return json;
    }
}
