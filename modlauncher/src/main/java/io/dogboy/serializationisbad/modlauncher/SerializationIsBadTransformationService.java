package io.dogboy.serializationisbad.modlauncher;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SerializationIsBadTransformationService implements ITransformationService {

    @Override
    public String name() {
        return "serializationisbad";
    }

    @Override
    public void initialize(IEnvironment environment) {
        if (SerializationIsBad.isAgentActive()) return;

        Path minecraftDir = environment.getProperty(IEnvironment.Keys.GAMEDIR.get())
                .orElseThrow(() -> new RuntimeException("No game path found"));

        SerializationIsBad.init(minecraftDir.toFile());
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException { }

    @Override
    public List<ITransformer> transformers() {
        if (SerializationIsBad.isAgentActive()) return Collections.emptyList();

        return SerializationIsBad.getInstance().getConfig().getPatchModules().stream()
                .map(SIBTransformer::new)
                .collect(Collectors.toList());
    }

    @Override
    public void beginScanning(IEnvironment environment) { }

    @Override
    public Map.Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalClassesLocator() {
        return new Map.Entry<Set<String>, Supplier<Function<String, Optional<URL>>>>() {
            @Override
            public Set<String> getKey() {
                return new HashSet<>(Arrays.asList("io.dogboy.serializationisbad."));
            }

            @Override
            public Supplier<Function<String, Optional<URL>>> getValue() {
                return () -> str -> Optional.ofNullable(SerializationIsBadTransformationService.class.getResource("/" + str));
            }

            @Override
            public Supplier<Function<String, Optional<URL>>> setValue(Supplier<Function<String, Optional<URL>>> value) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
