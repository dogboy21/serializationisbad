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
    private static boolean agentActive = false;

    public static SerializationIsBad getInstance() {
        return SerializationIsBad.instance;
    }

    public static void init(File minecraftDir) {
        if (SerializationIsBad.instance != null) {
            SerializationIsBad.logger.warn("Attempted to initialize SerializationIsBad twice, skipping");
            return;
        }

        String implementationType = SerializationIsBad.getImplementationType();
        if (implementationType.equals("agent")) {
            SerializationIsBad.agentActive = true;
        }
        SerializationIsBad.logger.info("Initializing SerializationIsBad, implementation type: " + implementationType);
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

    private static String getImplementationType() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (stackTraceElement.getClassName().startsWith("io.dogboy.serializationisbad.")
                    && !stackTraceElement.getClassName().startsWith("io.dogboy.serializationisbad.core.")) {
                return stackTraceElement.getClassName().split("[.]")[3];
            }
        }

        return "unknown";
    }

    public static boolean isAgentActive() {
        return SerializationIsBad.agentActive;
    }
}
