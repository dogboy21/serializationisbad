package io.dogboy.serializationisbad.legacyforge;

import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.SerializationIsBad;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;

public class SIBTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (Patches.getPatchModuleForClass(transformedName) == null) return basicClass;

        SerializationIsBad.logger.info("Applying patches to " + transformedName);

        ClassNode classNode = Patches.readClassNode(basicClass);
        Patches.applyPatches(transformedName, classNode);
        return Patches.writeClassNode(classNode);
    }
}
