package com.alibaba.android.arouter.register.core

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AsmTransform extends Transform {

    Project project

    AsmTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("===== ASM Transform =====")
        println("${transformInvocation.inputs}")
        println("${transformInvocation.referencedInputs}")
        println("${transformInvocation.outputProvider}")
        println("${transformInvocation.incremental}")

        //当前是否是增量编译
        boolean isIncremental = transformInvocation.isIncremental()
        println("=== isIncremental ===$isIncremental")
//        if (isIncremental){
//            return
//        }
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs()
        //引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs()
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR)
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                transformJar(jarInput.getFile(), dest)
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                println("== DI = " + directoryInput.file.listFiles().toArrayString())
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY)
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                //FileUtils.copyDirectory(directoryInput.getFile(), dest)
                transformDir(directoryInput.getFile(), dest)
            }
        }
    }

    @Override
    String getName() {
        return AsmTransform.simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    private static void transformJar(File input, File dest) {
        println("=== transformJar ===")
        FileUtils.copyFile(input, dest)
    }

    private static void transformDir(File input, File dest) {
        if (dest.exists()) {
            FileUtils.forceDelete(dest)
        }
        FileUtils.forceMkdir(dest)
        String srcDirPath = input.getAbsolutePath()
        String destDirPath = dest.getAbsolutePath()
        println("=== transform dir = " + srcDirPath + ", " + destDirPath)
        for (File file : input.listFiles()) {
            String destFilePath = file.absolutePath.replace(srcDirPath, destDirPath)
            File destFile = new File(destFilePath)
            if (file.isDirectory()) {
                transformDir(file, destFile)
            } else if (file.isFile()) {
                FileUtils.touch(destFile)
                transformSingleFile(file, destFile)
            }
        }
    }

    private static void transformSingleFile(File input, File dest) {
        println("=== transformSingleFile ===")
        if (input.getName().endsWith(".class")) {
            weave(input.getAbsolutePath(), dest.getAbsolutePath())
        } else {
            println("=== endsWith ===" + input)
            FileUtils.copyFile(input, dest)
        }
    }

    private static void weave(String inputPath, String outputPath) {
        try {
            FileInputStream is = new FileInputStream(inputPath)
            ClassReader cr = new ClassReader(is)
            ClassWriter cw = new ClassWriter(cr, 0)
//            TestMethodClassAdapter adapter = new TestMethodClassAdapter(cw)
//            cr.accept(adapter, 0)
            ClassVisitor cv = new MyClassVisitor(Opcodes.ASM5, cw)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            FileOutputStream fos = new FileOutputStream(outputPath)
            fos.write(cw.toByteArray())
            fos.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    static class MyClassVisitor extends ClassVisitor {

        MyClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }


    }
}
