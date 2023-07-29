package io.dogboy.serializationisbad.legacyforge;

import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.io.File;
import java.util.Map;

@net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name(SerializationIsBadCoreMod.modId)
@net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions("io.dogboy.serializationisbad")
@cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name(SerializationIsBadCoreMod.modId)
@cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions("io.dogboy.serializationisbad")
public class SerializationIsBadCoreMod implements net.minecraftforge.fml.relauncher.IFMLLoadingPlugin, cpw.mods.fml.relauncher.IFMLLoadingPlugin {
    public static final String modId = "serializationisbad";

    public SerializationIsBadCoreMod() {
        if (SerializationIsBad.isAgentActive()) return;

        File minecraftHome = SerializationIsBadCoreMod.getMinecraftHome();
        SerializationIsBad.init(minecraftHome);
    }

    @Override
    public String[] getASMTransformerClass() {
        if (SerializationIsBad.isAgentActive()) return new String[0];

        return new String[]{ SIBTransformer.class.getCanonicalName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

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
