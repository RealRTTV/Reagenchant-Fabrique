package ca.rttv.reagenchant.config.extra;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;

public class JsonHelper {
    public static boolean writeJsonToFile(JsonObject root, File file, Gson GSON) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(root));
            writer.close();
            return true;
        } catch (Exception ignored) { }
        return false;
    }
    public static File getConfigDirectory() {
        return new File(".", "config/reagenchant");
    }
}
