package me.nova.eventsystem;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class BaseListenerStripper extends ClassVisitor {
    public BaseListenerStripper(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals("getType") || name.equals("getGenericType")) {
            access = access & ~Opcodes.ACC_FINAL;
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}