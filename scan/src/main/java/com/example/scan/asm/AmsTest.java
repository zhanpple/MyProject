package com.example.scan.asm;


import com.example.scan.asm2.AsmTools;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

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
            String myPath = "file:////Users/zmp/android/JavaProject/java-project/AAA.class";
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
        ClassVisitor cv = new MyClassVisitor(Opcodes.ASM7, cw);
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
                return new RouteMethodVisitor(api, methodVisitor, access, name, descriptor);
            }
            return methodVisitor;
        }


        static class RouteMethodVisitor extends AdviceAdapter {


            /**
             * Constructs a new {@link AdviceAdapter}.
             *
             * @param api           the ASM API version implemented by this visitor. Must be one of {@link
             *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
             * @param methodVisitor the method visitor to which this adapter delegates calls.
             * @param access        the method's access flags (see {@link Opcodes}).
             * @param name          the method's name.
             * @param descriptor    the method's descriptor (see {@link Type Type}).
             */
            protected RouteMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
                super(api, methodVisitor, access, name, descriptor);
            }


            @Override
            protected void onMethodEnter() {
                super.onMethodEnter();
                getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
                visitLdcInsn("参数----------------------------------onMethodEnter");
                invokeVirtual(Type.getType(PrintStream.class), new Method("println", "(Ljava/lang/String;)V"));
            }

            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode);
                /***
                 * int var2 = 123222;
                 * int var3 = 456222;
                 * System.out.println(var2 - var3);
                 */
                //创建变量
                int i = newLocal(Type.INT_TYPE);
                /**
                 * 封装了赋值操作
                 * LDC SIPUSH BIPUSH等
                 */
                push(123222);
                //ISTORE 用i接受上面等变量
                storeLocal(i);

                int i2 = newLocal(Type.INT_TYPE);
                push(45622342);
                storeLocal(i2);

                /**
                 * GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
                 */
                getStatic(Type.getType(System.class),
                        "out", Type.getType(PrintStream.class));
                loadLocal(i);
                loadLocal(i2);
                visitInsn(Opcodes.ISUB);
                /**
                 * INVOKEVIRTUAL java/io/PrintStream.println (I)V
                 */
                invokeVirtual(Type.getType(PrintStream.class),
                        new Method("println", "(I)V"));


                invokeStatic(Type.getType(AsmTools.class), new Method("println", "()V"));
                push("我是参数");
                invokeStatic(Type.getType("Lcom/example/scan/asm2/AsmTools;"), new Method("println", "(Ljava/lang/String;)V"));

            }

//            @Override
//            public void visitCode() {
//                super.visitCode();
//                mv.visitFieldInsn(Opcodes.GETSTATIC,
//                        Type.getInternalName(System.class), //"java/lang/System"
//                        "out",
//                        Type.getDescriptor(PrintStream.class) //"Ljava/io/PrintStream;"
//                );
//                mv.visitLdcInsn("参数----------------------------------visitCode");//参数
//
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        Type.getInternalName(PrintStream.class), //"java/io/PrintStream"
//                        "println",
//                        "(Ljava/lang/String;)V",//方法描述符
//                        false);
//                mv.visitFieldInsn(Opcodes.GETSTATIC,
//                        "java/lang/System",
//                        "out", "Ljava/io/PrintStream;");
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
//                        "currentTimeMillis", "()J", false);
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                        "java/io/PrintStream",
//                        "println", "(J)V", false);
//
//
//
//            }
//
//            @Override
//            public void visitInsn(int opcode) {
//                if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
//                    mv.visitFieldInsn(Opcodes.GETSTATIC,
//                            Type.getInternalName(System.class), //"java/lang/System"
//                            "out",
//                            Type.getDescriptor(PrintStream.class) //"Ljava/io/PrintStream;"
//                    );
//                    mv.visitLdcInsn("参数----------------------------------visitInsn");//参数
//
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
//                            Type.getInternalName(PrintStream.class), //"java/io/PrintStream"
//                            "println",
//                            "(Ljava/lang/String;)V",//方法描述符
//                            false);
//
//                    mv.visitInsn(Opcodes.ICONST_5);
//                    mv.visitVarInsn(Opcodes.ISTORE,3);
//                    mv.visitInsn(Opcodes.ICONST_5);
//                    mv.visitVarInsn(Opcodes.ISTORE,4);
//                }
//                super.visitInsn(opcode);
//            }

            //
            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                //需要MAXLOCALS个方法栈 最大MAXSTACK个数 栈溢出stack overflow Exception
                //MAXSTACK必须大于MAXLOCALS
                super.visitMaxs(maxStack + Integer.MAX_VALUE, maxLocals);
            }
        }
    }

}
