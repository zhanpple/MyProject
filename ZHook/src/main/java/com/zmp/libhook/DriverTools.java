package com.zmp.libhook;


import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DriverTools {

    public static Properties getProperties() {
        Properties config = new Properties();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("chrome.properties");
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return config;
    }

    public static WebDriver getWebDriver(String chrome,String url) throws InterruptedException {
        return getWebDriver(chrome, true,url);
    }

    public static WebDriver getWebDriver(String chrome, boolean isShowImage,String url) throws InterruptedException {
        WebDriver webDriver = DriverFactory.create(chrome, isShowImage);
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().setScriptTimeout(500, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(500, TimeUnit.SECONDS);
        try {
            webDriver.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(3000);  //暂停3秒
        return webDriver;
    }

    public static void writeFileString(String str, File file, boolean isAppend) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, isAppend), StandardCharsets.UTF_8));
            bufferedWriter.write(str);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getFileUrls(String file) {
        ArrayList<String> strings = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            while (true) {
                String s1 = bufferedReader.readLine();
                if (s1 == null) {
                    break;
                }
                String s2 = bufferedReader.readLine();
                strings.add(s2);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

}
