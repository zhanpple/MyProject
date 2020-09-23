package com.example.plugin.tools;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ScanUtil {

    public static final String FIND_CLASS_PACKAGE_NAME = "com/example/router";

    public static final String INTERFACE_NAME = "com/example/basemoudle/IRouter";

    public static final String ASM_CLASS_NAME = "com/example/basemoudle/RouterTools.class";

    public static final String ASM_CLASS_NAME_TYPE = "Lcom/example/basemoudle/RouterTools;";

    public static final ArrayList<String> CLASS_NAMES = new ArrayList<>();

    public static final String GENERATE_TO_METHOD_NAME = "loadRouterMap";

    public static File fileContainsInitClass;

    /**
     * scan jar file
     *
     * @param jarFile  All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    public static void scanJar(File jarFile, File destFile) {
        try {
            JarFile file = new JarFile(jarFile);
            Enumeration<JarEntry> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(FIND_CLASS_PACKAGE_NAME)) {
                    Logger.w("scanJar-----------------" + jarEntry);
                    InputStream inputStream = file.getInputStream(jarEntry);
                    scanClass(inputStream);
                    inputStream.close();
                } else if (ASM_CLASS_NAME.equals(entryName)) {
                    //找到要插桩的类
                    fileContainsInitClass = destFile;
                } else {
                    Logger.w("scanJar:" + entryName);
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.copyFile(jarFile, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean shouldProcessPreDexJar(String path) {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository");
    }

    public static boolean shouldProcessClass(String entryName) {
        Logger.e("entryName:" + entryName);
        return entryName != null && entryName.endsWith(".class") && entryName.startsWith(FIND_CLASS_PACKAGE_NAME);
    }

    /**
     * scan class file
     *
     * @param file class
     */
    public static void scanClass(File file, File desFile) {
        if (file.getAbsolutePath().contains("com/example/basemoudle/RouterTools")) {
            fileContainsInitClass = desFile;
            return;
        }
        try {
            scanClass(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void scanClass(InputStream inputStream) {
        try {
            ClassReader cr = new ClassReader(inputStream);
            ClassWriter cw = new ClassWriter(cr, 0);
            ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM7, cw);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            if (interfaces != null) {
                for (String anInterface : interfaces) {
                    if (anInterface.equals(INTERFACE_NAME)) {
                        //fix repeated inject init code when Multi-channel packaging
                        if (!CLASS_NAMES.contains(name)) {
                            CLASS_NAMES.add(name);
                            Logger.w("add:------------------:" + name);
                        }
                    }
                }
            }
        }
    }
}