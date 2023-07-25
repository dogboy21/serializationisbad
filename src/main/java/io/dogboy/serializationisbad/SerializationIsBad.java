package io.dogboy.serializationisbad;

import com.google.gson.Gson;
import io.dogboy.serializationisbad.config.SIBConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name(SerializationIsBad.modId)
@net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions("io.dogboy.serializationisbad")
@cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name(SerializationIsBad.modId)
@cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions("io.dogboy.serializationisbad")
public class SerializationIsBad implements net.minecraftforge.fml.relauncher.IFMLLoadingPlugin, cpw.mods.fml.relauncher.IFMLLoadingPlugin {
    public static final String modId = "serializationisbad";
    static final Logger logger = LogManager.getLogger(SerializationIsBad.class);
    static SIBConfig config = new SIBConfig();

    public SerializationIsBad() {
        File minecraftHome = SerializationIsBad.getMinecraftHome();
        File configFile = new File(new File(minecraftHome, "config"), "serializationisbad.json");

        if (configFile.isFile()) {
            Gson gson = new Gson();
            try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
                SerializationIsBad.config = gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),
                        SIBConfig.class);

                SerializationIsBad.logger.info("Loaded config file");
                SerializationIsBad.logger.info("  Blocking Enabled: " + SerializationIsBad.config.isExecuteBlocking());
                SerializationIsBad.logger.info("  Loaded Patch Modules: " + SerializationIsBad.config.getPatchModules().size());
            } catch (Exception e) {
                SerializationIsBad.logger.error("Failed to load config file", e);
            }
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ SIBTransformer.class.getCanonicalName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private static File getMinecraftHome() {
        try {
            return (File) net.minecraftforge.fml.relauncher.FMLInjectionData.data()[6];
        } catch (Throwable e) {
            return (File) cpw.mods.fml.relauncher.FMLInjectionData.data()[6];
        }
    }

}
