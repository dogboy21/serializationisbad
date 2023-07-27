package io.dogboy.serializationisbad.core;

import com.google.gson.Gson;
import io.dogboy.serializationisbad.core.config.SIBConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SerializationIsBad {
    public static final Logger logger = LogManager.getLogger(SerializationIsBad.class);
    private static SerializationIsBad instance;

    public static SerializationIsBad getInstance() {
        return SerializationIsBad.instance;
    }

    public static void init(File minecraftDir) {
        if (SerializationIsBad.instance != null) {
            throw new IllegalStateException("Cannot create multiple instances of SerializationIsBad");
        }

        SerializationIsBad.instance = new SerializationIsBad(minecraftDir);
    }

    private final SIBConfig config;

    private SerializationIsBad(File minecraftDir) {
        this.config = SerializationIsBad.readConfig(minecraftDir);

        SerializationIsBad.logger.info("Loaded config file");
        SerializationIsBad.logger.info("  Blocking Enabled: " + this.config.isExecuteBlocking());
        SerializationIsBad.logger.info("  Loaded Patch Modules: " + this.config.getPatchModules().size());
    }

    public SIBConfig getConfig() {
        return this.config;
    }

    private static SIBConfig readConfig(File minecraftDir) {
        File configFile = new File(new File(minecraftDir, "config"), "serializationisbad.json");

        if (configFile.isFile()) {
            Gson gson = new Gson();
            try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                return gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),
                        SIBConfig.class);
            } catch (Exception e) {
                SerializationIsBad.logger.error("Failed to load config file", e);
            }
        }

        return new SIBConfig();
    }

}
