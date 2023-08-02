package io.dogboy.serializationisbad.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.config.PatchModule;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;

public class SIBTransformer implements ITransformer<ClassNode> {
    private final PatchModule patchModule;

    public SIBTransformer(PatchModule patchModule) {
        this.patchModule = patchModule;
    }

    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        Patches.applyPatches(input.name.replace('/', '.'), input, true);
        return input;
    }

    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public Set<Target> targets() {
        return this.patchModule.getClassesToPatch().stream()
                .map(Target::targetClass)
                .collect(Collectors.toSet());
    }

}
