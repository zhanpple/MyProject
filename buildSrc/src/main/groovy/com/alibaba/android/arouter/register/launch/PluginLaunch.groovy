package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.MyRegisterTransform
import com.alibaba.android.arouter.register.utils.Logger
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.alibaba.android.arouter.register.core.RegisterTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Simple version of AutoRegister plugin for ARouter
 * @author billy.qi email: qiyilike@163.com
 * @since 17/12/06 15:35
 */
public class PluginLaunch implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        Logger.make(project)
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        def isApp = project.plugins.hasPlugin(AppPlugin)
        Logger.w('Project enable arouter-register plugin$AppPlugin')
        //only application module needs this plugin to generate register code
        if (isApp) {
            System.out.println("AAAAAAAAAAAAAAA-isApp-AAAAAAAAAAAAAAAAAAA")
            Logger.make(project)

            Logger.e('Project enable arouter-register plugin')

            def android = project.extensions.getByType(AppExtension)

            //register this plugin
            android.registerTransform(new MyRegisterTransform(project))
        }
    }

}
