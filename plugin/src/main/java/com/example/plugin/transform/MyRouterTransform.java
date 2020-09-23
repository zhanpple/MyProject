package com.example.plugin.transform;

import com.example.plugin.tools.Logger;
import com.example.plugin.tools.ScanUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;


/**
 * Created at 9:41 2020/9/23
 *
 * @author zmp
 * <p>
 * des:
 */
public class MyRouterTransform extends MyBaseTransform {

    public MyRouterTransform(Project project) {
        super(project);
    }


    protected void transformJar(File input, File dest) {
        if (ScanUtil.shouldProcessPreDexJar(input.getAbsolutePath())) {
            ScanUtil.scanJar(input, dest);
        } else {
            super.transformJar(input, dest);
        }
    }


    protected void transformSingleFile(String parentPath, File input, File dest) {
        if (!parentPath.endsWith(File.separator)) {
            parentPath += File.separator;
        }
        String replace = input.getAbsolutePath().replace(parentPath, "");
        if (!File.separator.equals("/")) {
            replace = replace.replaceAll("\\\\", "/");
        }
        try {
            if (ScanUtil.shouldProcessClass(replace)) {
                ScanUtil.scanClass(input, dest);
            }
            FileUtils.copyFile(input, dest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void insertInitCodeTo() {
        File containsInitClass = ScanUtil.fileContainsInitClass;
        Logger.e("containsInitClass：" + containsInitClass);
        Logger.e("containsInitClass：" + Arrays.toString(ScanUtil.CLASS_NAMES.toArray()));
        if (containsInitClass != null) {
            insertInitCodeIntoJarFile(containsInitClass);
        }
    }

    /**
     * generate code into jar file
     *
     * @param jarFile the jar file which contains LogisticsCenter.class
     * @return
     */
    private void insertInitCodeIntoJarFile(File jarFile) {
        try {
            File optJar = new File(jarFile.getParent(), jarFile.getName() + ".opt");
            if (optJar.exists()) {
                optJar.delete();
            }
            JarFile file = new JarFile(jarFile);
            Enumeration<JarEntry> enumeration = file.entries();
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar));

            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = file.getInputStream(jarEntry);
                jarOutputStream.putNextEntry(zipEntry);
                if (ScanUtil.ASM_CLASS_NAME.equals(entryName)) {
                    byte[] bytes = referHackWhenInit(inputStream);
                    jarOutputStream.write(bytes);
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                inputStream.close();
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
            file.close();

            if (jarFile.exists()) {
                jarFile.delete();
            }
            optJar.renameTo(jarFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //refer hack class when object init
    private byte[] referHackWhenInit(InputStream inputStream) {
        Logger.e("referHackWhenInit");
        byte[] bytes = null;
        try {
            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(cr, 0);
            ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            bytes = cw.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            //generate code into this method
            Logger.e("visitMethod:" + name);
            if (name.equals(ScanUtil.GENERATE_TO_METHOD_NAME)) {
                mv = new RouteMethodVisitor(api, mv, access, name, desc);
            }
            return mv;
        }
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
         * @param descriptor    the method's descriptor (see  Type).
         */
        protected RouteMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            for (String className : ScanUtil.CLASS_NAMES) {
                push(className);
                invokeStatic(Type.getType(ScanUtil.ASM_CLASS_NAME_TYPE), new Method("register", "(Ljava/lang/String;)V"));
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + Integer.MAX_VALUE, maxLocals);
        }
    }

}
