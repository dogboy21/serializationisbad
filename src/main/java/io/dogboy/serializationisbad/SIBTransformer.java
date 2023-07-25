package io.dogboy.serializationisbad;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class SIBTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (Patches.getPatchModuleForClass(transformedName) == null) return basicClass;

        SerializationIsBad.logger.info("Applying patches to " + transformedName);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            InsnList instructions = methodNode.instructions;
            for (int i = 0; i < instructions.size(); i++) {
                AbstractInsnNode instruction = instructions.get(i);
                if (instruction.getOpcode() == Opcodes.NEW
                        && instruction instanceof TypeInsnNode && "java/io/ObjectInputStream".equals(((TypeInsnNode) instruction).desc)) {
                    ((TypeInsnNode) instruction).desc = "io/dogboy/serializationisbad/ClassFilteringObjectInputStream";

                    SerializationIsBad.logger.info("(1/2) Redirecting ObjectInputStream to ClassFilteringObjectInputStream in method " + methodNode.name);
                } else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL
                        && instruction instanceof MethodInsnNode && "java/io/ObjectInputStream".equals(((MethodInsnNode) instruction).owner)
                        && "<init>".equals(((MethodInsnNode) instruction).name)) {
                    ((MethodInsnNode) instruction).owner = "io/dogboy/serializationisbad/ClassFilteringObjectInputStream";
                    ((MethodInsnNode) instruction).desc = "(Ljava/io/InputStream;Lio/dogboy/serializationisbad/config/PatchModule;)V";

                    InsnList additionalInstructions = new InsnList();
                    additionalInstructions.add(new LdcInsnNode(transformedName));
                    additionalInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "io/dogboy/serializationisbad/Patches",
                            "getPatchModuleForClass", "(Ljava/lang/String;)Lio/dogboy/serializationisbad/config/PatchModule;", false));

                    instructions.insertBefore(instruction, additionalInstructions);

                    SerializationIsBad.logger.info("(2/2) Redirecting ObjectInputStream to ClassFilteringObjectInputStream in method " + methodNode.name);
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
