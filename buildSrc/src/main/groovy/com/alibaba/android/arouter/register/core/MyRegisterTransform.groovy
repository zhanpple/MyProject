package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.alibaba.android.arouter.register.utils.ScanUtil
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * transform api
 * <p>
 *     1. Scan all classes to find which classes implement the specified interface
 *     2. Generate register code into class file: {@link ScanSetting#GENERATE_TO_CLASS_FILE_NAME}
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/21 11:48
 */
class MyRegisterTransform extends Transform {

    Project project
    static ArrayList<ScanSetting> registerList
    static File fileContainsInitClass;

    MyRegisterTransform(Project project) {
        this.project = project
    }

    /**
     * name of this transform
     * @return
     */
    @Override
    String getName() {
        return ScanSetting.PLUGIN_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * The plugin will scan all classes in the project
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


//    @Override
//    void transform(Context context, Collection<TransformInput> inputs
//                   , Collection<TransformInput> referencedInputs
//                   , TransformOutputProvider outputProvider
//                   , boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        Logger.make(project)
//        Logger.e('Start scan register info in jar file.')
//        long startTime = System.currentTimeMillis()
//
//        Logger.e("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
//        System.out.println("")
//    }
//
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
                Logger.make(project)
        Logger.e('Start scan register info in jar file.')
        long startTime = System.currentTimeMillis()

        Logger.e("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
        System.out.println("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")


    }
}
