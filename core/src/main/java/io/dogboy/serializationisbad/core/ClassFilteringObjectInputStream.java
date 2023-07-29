package io.dogboy.serializationisbad.core;

import io.dogboy.serializationisbad.core.config.PatchModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

public class ClassFilteringObjectInputStream extends ObjectInputStream {
    private final PatchModule patchModule;

    public ClassFilteringObjectInputStream(InputStream in, PatchModule patchModule) throws IOException {
        super(in);
        this.patchModule = patchModule;
    }

    private boolean isClassAllowed(String className) {
        if (className.startsWith("[L") && className.endsWith(";")) {
            className = className.substring(2, className.length() - 1);
        } else if (className.startsWith("L") && className.endsWith(";")) {
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

        return super.resolveClass(desc);
    }

}
