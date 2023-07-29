package io.dogboy.serializationisbad.core.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SIBConfig {
    private boolean executeBlocking;
    private List<PatchModule> patchModules;
    private Set<String> classAllowlist;
    private Set<String> packageAllowlist;

    public SIBConfig() {
        this.executeBlocking = true;
        this.patchModules = new ArrayList<>();
        this.classAllowlist = new HashSet<>();
        this.packageAllowlist = new HashSet<>();
    }

    public boolean isExecuteBlocking() {
        return this.executeBlocking;
    }

    public void setExecuteBlocking(boolean executeBlocking) {
        this.executeBlocking = executeBlocking;
    }

    public List<PatchModule> getPatchModules() {
        return this.patchModules;
    }

    public void setPatchModules(List<PatchModule> patchModules) {
        this.patchModules = patchModules;
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
