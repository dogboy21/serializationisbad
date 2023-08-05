package io.dogboy.serializationisbad.core.config;

import java.util.HashSet;
import java.util.Set;

public class PatchModule {
    public final static PatchModule EMPTY = new PatchModule();

    private Set<String> classesToPatch;
    private Set<String> classAllowlist;
    private Set<String> packageAllowlist;

    public PatchModule() {
        this.classesToPatch = new HashSet<>();
        this.classAllowlist = new HashSet<>();
        this.packageAllowlist = new HashSet<>();
    }

    public Set<String> getClassesToPatch() {
        return this.classesToPatch;
    }

    public void setClassesToPatch(Set<String> classesToPatch) {
        this.classesToPatch = classesToPatch;
    }

    public Set<String> getClassAllowlist() {
        return this.classAllowlist;
    }

    public void setClassAllowlist(Set<String> classAllowlist) {
        this.classAllowlist = classAllowlist;
    }

    public Set<String> getPackageAllowlist() {
        return this.packageAllowlist;
    }

    public void setPackageAllowlist(Set<String> packageAllowlist) {
        this.packageAllowlist = packageAllowlist;
    }
}
