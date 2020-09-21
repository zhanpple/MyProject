package com.example.scan.asm;


import org.objectweb.asm.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created at 15:17 2020/9/21
 *
 * @author zmp
 * <p>
 * des:
 */
public class AmsTest {

    public static void main(String[] args) throws IOException {
        AmsDemo.println();
        byte[] bytes = referHackWhenInit(new FileInputStream("AmsDemo.class"));
        FileOutputStream outputStream = new FileOutputStream("AAA.class");
        outputStream.write(bytes);
        outputStream.close();

        try {
            Class<?> aClass = new MyClassLoader().findClass("com.example.scan.asm.AmsDemo");
            aClass.getMethod("println").invoke(aClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class MyClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) {
            String myPath = "file:///F:/giteeApp/JavaProject/AAA.class";
            System.out.println(myPath);
            byte[] cLassBytes = null;
            Path path = null;
            try {
                path = Paths.get(new URI(myPath));
                cLassBytes = Files.readAllBytes(path);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            Class clazz = defineClass(name, cLassBytes, 0, cLassBytes.length);
            return clazz;
        }
    }


    //refer hack class when object init
    private static byte[] referHackWhenInit(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    static class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            System.out.println(name);
            if (name.equals("println")) {
                return new RouteMethodVisitor(Opcodes.ASM5, methodVisitor);
            }
            return methodVisitor;
        }


        static class RouteMethodVisitor extends MethodVisitor {

            RouteMethodVisitor(int api, MethodVisitor mv) {
                super(api, mv);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                System.out.println("visitEnd");
            }


            @Override
            public void visitCode() {
                super.visitCode();
                mv.visitFieldInsn(Opcodes.GETSTATIC,
                        Type.getInternalName(System.class), //"java/lang/System"
                        "out",
                        Type.getDescriptor(PrintStream.class) //"Ljava/io/PrintStream;"
                );
                mv.visitLdcInsn("参数----------------------------------visitCode");//参数

                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        Type.getInternalName(PrintStream.class), //"java/io/PrintStream"
                        "println",
                        "(Ljava/lang/String;)V",//方法描述符
                        false);
                mv.visitFieldInsn(Opcodes.GETSTATIC,
                        "java/lang/System",
                        "out", "Ljava/io/PrintStream;");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                        "currentTimeMillis", "()J", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        "java/io/PrintStream",
                        "println", "(J)V", false);



            }

            @Override
            public void visitInsn(int opcode) {
                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
                    mv.visitFieldInsn(Opcodes.GETSTATIC,
                            Type.getInternalName(System.class), //"java/lang/System"
                            "out",
                            Type.getDescriptor(PrintStream.class) //"Ljava/io/PrintStream;"
                    );
                    mv.visitLdcInsn("参数----------------------------------visitInsn");//参数

                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                            Type.getInternalName(PrintStream.class), //"java/io/PrintStream"
                            "println",
                            "(Ljava/lang/String;)V",//方法描述符
                            false);
                    mv.visitInsn(Opcodes.ICONST_2);
                    mv.visitVarInsn(Opcodes.ISTORE,3);
                    mv.visitInsn(Opcodes.ICONST_2);
                    mv.visitVarInsn(Opcodes.ISTORE,4);
                }
                super.visitInsn(opcode);
            }


            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                super.visitMaxs(maxStack + 4, maxLocals);
            }
        }
    }

}
