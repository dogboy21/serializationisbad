package io.dogboy.serializationisbad.legacyforge;

import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.SerializationIsBad;
import io.dogboy.serializationisbad.core.config.PatchModule;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;

public class SIBTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!Patches.shouldPatchClass(transformedName)) {
            return basicClass;
        }

        ClassNode classNode = Patches.readClassNode(basicClass);
        Patches.applyPatches(transformedName, classNode);
        return Patches.writeClassNode(classNode);
    }
}
