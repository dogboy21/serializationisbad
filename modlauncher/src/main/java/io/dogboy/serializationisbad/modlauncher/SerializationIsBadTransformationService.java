package io.dogboy.serializationisbad.modlauncher;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SerializationIsBadTransformationService implements ITransformationService {

    @Override
    public String name() {
        return "serializationisbad";
    }

    @Override
    public void initialize(IEnvironment environment) {
        Path minecraftDir = environment.getProperty(IEnvironment.Keys.GAMEDIR.get())
                .orElseThrow(() -> new RuntimeException("No game path found"));

        SerializationIsBad.init(minecraftDir.toFile());
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException { }

    @Override
    public List<ITransformer> transformers() {
        return SerializationIsBad.getInstance().getConfig().getPatchModules().stream()
                .map(SIBTransformer::new)
                .collect(Collectors.toList());
    }

    @Override
    public void beginScanning(IEnvironment environment) { }

}
