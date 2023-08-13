package io.dogboy.serializationisbad.legacyforge;

import io.dogboy.serializationisbad.core.Patches;
import net.minecraft.launchwrapper.IClassTransformer;

public class SIBTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (Patches.getPatchModuleForClass(transformedName) == null) return basicClass;

        return Patches.patchClass(basicClass, transformedName, false);
    }
}
