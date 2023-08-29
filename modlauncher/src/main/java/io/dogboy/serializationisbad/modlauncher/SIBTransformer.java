package io.dogboy.serializationisbad.modlauncher;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import io.dogboy.serializationisbad.core.Patches;
import io.dogboy.serializationisbad.core.config.PatchModule;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SIBTransformer implements ITransformer<ClassNode> {
    private final PatchModule patchModule;

    public SIBTransformer(PatchModule patchModule) {
        this.patchModule = patchModule;
    }

    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        // do not look at this weird fucking shit
        // we need to write -> patch -> read the classes, so we can use the core shadowed ASM lib properly

        byte[] classBytes = SIBTransformer.writeClassNode(input);
        byte[] transformedClassBytes = Patches.patchClass(classBytes, input.name.replace('/', '.'), true);
        return SIBTransformer.readClassNode(transformedClassBytes);
    }

    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public Set<Target> targets() {
        return Stream.concat(this.patchModule.getClassesToPatch().stream(),
                        this.patchModule.getCustomOISClasses().stream())
                .map(Target::targetClass)
                .collect(Collectors.toSet());
    }

    private static ClassNode readClassNode(byte[] classBytecode) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytecode);
        classReader.accept(classNode, 0);
        return classNode;
    }

    private static byte[] writeClassNode(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

}
