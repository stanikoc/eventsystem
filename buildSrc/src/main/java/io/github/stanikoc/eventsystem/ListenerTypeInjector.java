package io.github.stanikoc.eventsystem;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class ListenerTypeInjector extends ClassVisitor {
    private final String primaryInternalName;
    private final String genericInternalName;
    private String className;

    public ListenerTypeInjector(ClassVisitor cv, Class<?> primaryClass, Class<?> genericClass) {
        super(Opcodes.ASM9, cv);
        this.primaryInternalName = Type.getInternalName(primaryClass);
        this.genericInternalName = genericClass != null ? Type.getInternalName(genericClass) : null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        int publicAccess = (access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
        super.visit(version, publicAccess, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (name.equals(this.className)) {
            access = (access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
        }

        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals("getType") && descriptor.equals("()Ljava/lang/Class;")) {
            return null;
        }

        if (name.equals("getGenericType") && descriptor.equals("()Ljava/lang/Class;")) {
            return null;
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        MethodVisitor typeMv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "getType", "()Ljava/lang/Class;", null, null);
        typeMv.visitCode();
        typeMv.visitLdcInsn(Type.getObjectType(primaryInternalName));
        typeMv.visitInsn(Opcodes.ARETURN);
        typeMv.visitMaxs(1, 1);
        typeMv.visitEnd();

        MethodVisitor genMv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "getGenericType", "()Ljava/lang/Class;", null, null);
        genMv.visitCode();
        if (genericInternalName != null) {
            genMv.visitLdcInsn(Type.getObjectType(genericInternalName));
        } else {
            genMv.visitInsn(Opcodes.ACONST_NULL);
        }

        genMv.visitInsn(Opcodes.ARETURN);
        genMv.visitMaxs(1, 1);
        genMv.visitEnd();

        super.visitEnd();
    }

}