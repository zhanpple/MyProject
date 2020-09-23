package com.example.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.example.plugin.tools.Logger;
import com.example.plugin.transform.MyRouterTransform;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 9:25 2020/9/23
 *
 * @author zmp
 * <p>
 * des:
 */
public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        Logger.make(project);
        Logger.w("apply:init-----------------");
        boolean hasAppPlugin = project.getPlugins().hasPlugin(AppPlugin.class);
        Logger.w("apply:init-----------------" + hasAppPlugin);
        if (hasAppPlugin) {

            Logger.w("apply:init-----------------" + hasAppPlugin);
            AppExtension android = project.getExtensions().getByType(AppExtension.class);

            //register this plugin
            android.registerTransform(new MyRouterTransform(project));
        }
    }

}
