package io.dogboy.serializationisbad.agent;

import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SerializationIsBadAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        SerializationIsBad.init(new File("."));
        inst.addTransformer(new SIBTransformer());
    }

    /**
     * When SIB is loaded as a JVM agent in a LaunchWrapper environment,
     * we need to add an exclusion to the LaunchClassLoader so SIB classes
     * always get loaded with the parent classloader.
     * Otherwise, mod classes create a separate SIB instance which is not initialized.
     *
     * I know this is an ugly hack, please don't judge me.
     */
    static void insertLaunchWrapperExclusion() {
        try {
            Class<?> launchClass = Class.forName("net.minecraft.launchwrapper.Launch");
            Field classLoaderField = launchClass.getDeclaredField("classLoader");
            Object classLoader = classLoaderField.get(null);
            Class<?> classLoaderClass = Class.forName("net.minecraft.launchwrapper.LaunchClassLoader");
            Method addClassLoaderExclusion = classLoaderClass.getDeclaredMethod("addClassLoaderExclusion", String.class);
            addClassLoaderExclusion.invoke(classLoader, "io.dogboy.serializationisbad");
        } catch (Exception e) {
            SerializationIsBad.logger.error("Failed to add LaunchWrapper exclusion", e);
        }
    }

}
