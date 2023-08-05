package io.dogboy.serializationisbad.agent;

import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class SIBTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (className == null) return classfileBuffer;
            if ("net/minecraft/launchwrapper/ITweaker".equals(className)) SerializationIsBadAgent.insertLaunchWrapperExclusion();

            String classNameDots = className.replace('/', '.');

            if (Patches.getPatchModuleForClass(classNameDots) == null) return classfileBuffer;

            return Patches.patchClass(classfileBuffer, classNameDots, false);
        } catch (Throwable e) {
            SerializationIsBad.logger.error("Failed to run agent class transformer", e);
            return classfileBuffer;
        }
    }
}
