package io.dogboy.serializationisbad.agent;

import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.SerializationIsBad;
import io.dogboy.serializationisbad.core.config.PatchModule;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SIBTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String classNameDots = className.replace('/', '.');

        if (!Patches.shouldPatchClass(classNameDots)) {
            return classfileBuffer;
        }

        ClassNode classNode = Patches.readClassNode(classfileBuffer);
        Patches.applyPatches(classNameDots, classNode);
        return Patches.writeClassNode(classNode);
    }
}
