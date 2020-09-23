package com.example.plugin.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Created at 9:37 2020/9/23
 *
 * @author zmp
 * <p>
 * des:
 */
public class MyBaseTransform extends Transform {

    protected Project project;

    public MyBaseTransform(Project project) {
        this.project = project;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //当前是否是增量编译
//        boolean isIncremental = transformInvocation.isIncremental();
//        Logger.w("apply:init-----------------"+isIncremental);
//        if (isIncremental) {
//            return;
//        }
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                transformJar(jarInput.getFile(), dest);
            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                String parentPath = directoryInput.getFile().getAbsolutePath();
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                //FileUtils.copyDirectory(directoryInput.getFile(), dest)

                transformDir(parentPath,directoryInput.getFile(), dest);
            }
        }

        insertInitCodeTo();
    }

    protected void insertInitCodeTo() {
    }

    protected void transformJar(File input, File dest) {
        try {
            FileUtils.copyDirectory(input, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void transformDir(String parentPath, File input, File dest) {
        try {
            if (dest.exists()) {
                FileUtils.forceDelete(dest);
            }
            FileUtils.forceMkdir(dest);
            String srcDirPath = input.getAbsolutePath();
            String destDirPath = dest.getAbsolutePath();
            for (File file : Objects.requireNonNull(input.listFiles())) {
                String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
                File destFile = new File(destFilePath);
                if (file.isDirectory()) {
                    transformDir(parentPath, file, destFile);
                } else if (file.isFile()) {
                    FileUtils.touch(destFile);
                    transformSingleFile(parentPath,file, destFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void transformSingleFile(String srcDirPath, File input, File dest) {
        try {
            if (input.getName().endsWith(".class")) {
                weaveClass(input, dest);
            } else {
                FileUtils.copyFile(input, dest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void weaveClass(File input, File dest) {
        try {
            FileUtils.copyFile(input, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }
}
