package com.example.plugin.tools;

import org.gradle.api.Project;

public class Logger {
    static org.gradle.api.logging.Logger logger;

    public static void make(Project project) {
        logger = project.getLogger();
    }

    public static void i(String info) {
        if (null != info && null != logger) {
            logger.info("MyPlugin::Register >>> " + info);
        }
    }

    public static void e(String error) {
        if (null != error && null != logger) {
            logger.error("MyPlugin::Register >>> " + error);
        }
    }

    public static void w(String warning) {
        if (null != warning && null != logger) {
            logger.warn("MyPlugin::Register >>> " + warning);
        }
    }
}
