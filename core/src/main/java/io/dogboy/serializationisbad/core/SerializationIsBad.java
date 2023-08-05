package io.dogboy.serializationisbad.core;

import com.google.gson.Gson;
import io.dogboy.serializationisbad.core.config.SIBConfig;
import io.dogboy.serializationisbad.core.logger.ILogger;
import io.dogboy.serializationisbad.core.logger.Log4JLogger;
import io.dogboy.serializationisbad.core.logger.NativeLogger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SerializationIsBad {
    public static final ILogger logger = SerializationIsBad.initLogger(SerializationIsBad.class.getCanonicalName());
    private static SerializationIsBad instance;
    private static boolean agentActive = false;

    public static SerializationIsBad getInstance() {
        if (SerializationIsBad.instance == null) throw new IllegalStateException("SerializationIsBad has not been initialized yet");

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
        if (this.config.getPatchModules().isEmpty()) {
            throw new RuntimeException("You are currently using SerializationIsBad without any patch modules configured. The mod on its own doesn't do anything so please install a config file patching the vulnerabilities");
        }

        SerializationIsBad.logger.info("Loaded config file");
        SerializationIsBad.logger.info("  Blocking Enabled: " + this.config.isExecuteBlocking());
        SerializationIsBad.logger.info("  Loaded Patch Modules: " + this.config.getPatchModules().size());
    }

    public SIBConfig getConfig() {
        return this.config;
    }

    private static SIBConfig readConfig(File minecraftDir) {
        File configFile = new File(new File(minecraftDir, "config"), "serializationisbad.json");
        Gson gson = new Gson();

        SIBConfig localConfig = new SIBConfig();
        if (configFile.isFile()) {
            try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                localConfig = gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),
                        SIBConfig.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load local config file", e);
            }
        } else {
            configFile.getParentFile().mkdirs();
            try (FileOutputStream fileOutputStream = new FileOutputStream(configFile)) {
                fileOutputStream.write(gson.toJson(localConfig).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create local config file", e);
            }
        }

        if (!localConfig.isUseRemoteConfig()) {
            SerializationIsBad.logger.info("Using local config file");
            return localConfig;
        }

        SIBConfig remoteConfig = SerializationIsBad.readRemoteConfig(localConfig.getRemoteConfigUrl());
        if (remoteConfig != null) {
            SerializationIsBad.logger.info("Using remote config file");
            return remoteConfig;
        }

        SerializationIsBad.logger.info("Using local config file as a fallback");
        return localConfig;
    }

    private static SIBConfig readRemoteConfig(String url) {
        Gson gson = new Gson();
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, new SecureRandom());
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);

            if (connection.getResponseCode() != 200) throw new IOException("Invalid response code: " + connection.getResponseCode());

            try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return gson.fromJson(inputStreamReader, SIBConfig.class);
            }
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
