package io.dogboy.serializationisbad.agent;

import io.dogboy.serializationisbad.core.SerializationIsBad;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * Another hacky workaround for newer Fabric versions that enforce
     * classpath isolation. This adds the path to the SiB jar to the
     * list of jar paths that are allowed to be loaded by the parent
     * classloader
     *
     * @param fabricClassLoader The classloader that was used to load the Fabric classes
     */
    static void insertFabricValidParentUrl(ClassLoader fabricClassLoader) {
        try {
            Path sibPath = new File(SerializationIsBadAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath();

            // basically accessing the following:
            // ((KnotClassDelegate) ((Knot) FabricLauncherBase.getLauncher()).classLoader).validParentCodeSources

            Class<?> fabricLauncherBaseClass = Class.forName("net.fabricmc.loader.impl.launch.FabricLauncherBase", true, fabricClassLoader);
            Method getLauncherMethod = fabricLauncherBaseClass.getDeclaredMethod("getLauncher");
            Object fabricLauncher = getLauncherMethod.invoke(null);
            Field classLoaderField = fabricLauncher.getClass().getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            Object classLoader = classLoaderField.get(fabricLauncher);
            Field validParentCodeSourcesField = classLoader.getClass().getDeclaredField("validParentCodeSources");
            validParentCodeSourcesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<Path> validParentCodeSources = (Set<Path>) validParentCodeSourcesField.get(classLoader);

            Set<Path> newValidParentCodeSources = new HashSet<>(validParentCodeSources);
            newValidParentCodeSources.add(sibPath);

            validParentCodeSourcesField.set(classLoader, newValidParentCodeSources);
        } catch (Throwable e) {
            SerializationIsBad.logger.error("Failed to insert Fabric valid parent URL", e);
        }
    }

}
