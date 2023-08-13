package io.dogboy.serializationisbad.core;

import io.dogboy.serializationisbad.core.config.PatchModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ClassFilteringObjectInputStream extends ObjectInputStream {
    private final PatchModule patchModule;
    private final ClassLoader parentClassLoader;

    public ClassFilteringObjectInputStream(InputStream in, PatchModule patchModule, ClassLoader parentClassLoader) throws IOException {
        super(in);
        this.patchModule = patchModule;
        this.parentClassLoader = parentClassLoader;
    }

    public ClassFilteringObjectInputStream(InputStream in, PatchModule patchModule) throws IOException {
        this(in, patchModule, null);
    }

    private boolean isClassAllowed(String className) {
        // strip all array dimensions, just get the base type
        while (className.startsWith("[")) {
            className = className.substring(1);
        }

        if (className.startsWith("L") && className.endsWith(";")) {
            className = className.substring(1, className.length() - 1);
        }

        if (SerializationIsBad.getInstance().getConfig().getClassAllowlist().contains(className)
                || this.patchModule.getClassAllowlist().contains(className)) {
            return true;
        }

        Set<String> allowedPackages = new HashSet<>(SerializationIsBad.getInstance().getConfig().getPackageAllowlist());
        allowedPackages.addAll(this.patchModule.getPackageAllowlist());

        for (String allowedPackage : allowedPackages) {
            if (className.startsWith(allowedPackage + ".")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        SerializationIsBad.logger.debug("Resolving class " + desc.getName());

        if (!this.isClassAllowed(desc.getName())) {
            SerializationIsBad.logger.warn("Tried to resolve class " + desc.getName() + ", which is not allowed to be deserialized");
            if (SerializationIsBad.getInstance().getConfig().isExecuteBlocking())
                throw new ClassNotFoundException("Class " + desc.getName() + " is not allowed to be deserialized");
        }

        if (this.parentClassLoader == null) {
            return super.resolveClass(desc);
        }

        String name = desc.getName();
        try {
            return Class.forName(name, false, this.parentClassLoader);
        } catch (ClassNotFoundException ex) {
            Class<?> cl = ClassFilteringObjectInputStream.primClasses.get(name);
            if (cl != null) {
                return cl;
            } else {
                throw ex;
            }
        }
    }

    private static final HashMap<String, Class<?>> primClasses = new HashMap<>(8, 1.0F);
    static {
        ClassFilteringObjectInputStream.primClasses.put("boolean", boolean.class);
        ClassFilteringObjectInputStream.primClasses.put("byte", byte.class);
        ClassFilteringObjectInputStream.primClasses.put("char", char.class);
        ClassFilteringObjectInputStream.primClasses.put("short", short.class);
        ClassFilteringObjectInputStream.primClasses.put("int", int.class);
        ClassFilteringObjectInputStream.primClasses.put("long", long.class);
        ClassFilteringObjectInputStream.primClasses.put("float", float.class);
        ClassFilteringObjectInputStream.primClasses.put("double", double.class);
        ClassFilteringObjectInputStream.primClasses.put("void", void.class);
    }

}
