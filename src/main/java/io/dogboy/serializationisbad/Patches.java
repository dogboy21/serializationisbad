package io.dogboy.serializationisbad;

import io.dogboy.serializationisbad.config.PatchModule;

public class Patches {
    public static PatchModule getPatchModuleForClass(String className) {
        return SerializationIsBad.config.getPatchModules().stream()
                .filter(patchModule -> patchModule.getClassesToPatch().contains(className))
                .findFirst()
                .orElse(null);
    }
}
