package io.dogboy.serializationisbad.core;

import com.google.gson.Gson;
import io.dogboy.serializationisbad.core.config.SIBConfig;
import io.dogboy.serializationisbad.core.logger.ILogger;
import io.dogboy.serializationisbad.core.logger.Log4JLogger;
import io.dogboy.serializationisbad.core.logger.NativeLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SerializationIsBad {
    public static final ILogger logger = SerializationIsBad.initLogger(SerializationIsBad.class.getCanonicalName());
    private static SerializationIsBad instance;
    private static boolean agentActive = false;

    private static final String remoteConfigUrl = "https://raw.githubusercontent.com/dogboy21/serializationisbad/master/serializationisbad.json";

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

    private static ILogger initLogger(String name) {
        try {
            // Check if needed Log4J classes are available
            Class.forName("org.apache.logging.log4j.LogManager");
            Class.forName("org.apache.logging.log4j.Logger");
            return new Log4JLogger(name);
        } catch (ClassNotFoundException e) {
            // Fallback to Java native logger
            return new NativeLogger(name);
        }
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

        SIBConfig remoteConfig = SerializationIsBad.readRemoteConfig();
        if (remoteConfig != null) {
            SerializationIsBad.logger.info("Using remote config file");
            return remoteConfig;
        } else if (configFile.isFile()) {
            Gson gson = new Gson();
            try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                return gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),
                        SIBConfig.class);
            } catch (Exception e) {
                SerializationIsBad.logger.error("Failed to load config file", e);
            }
        }

        throw new RuntimeException("You are currently using SerializationIsBad without a config file. The mod on its own doesn't do anything so please install a config file patching the vulnerabilities to " + configFile);
    }

    private static SIBConfig readRemoteConfig() {
        Gson gson = new Gson();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new java.net.URL(SerializationIsBad.remoteConfigUrl).openStream(), StandardCharsets.UTF_8)) {
            return gson.fromJson(inputStreamReader, SIBConfig.class);
        } catch (Exception e) {
            SerializationIsBad.logger.error("Failed to load remote config file", e);
        }

        return null;
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
