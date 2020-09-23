package com.zmp.libhook;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * selenium chrome驱动初始化
 */
public class DriverFactory {

    public static WebDriver create(String chromeDriver) {
        return create(chromeDriver,true);
    }


    public static WebDriver create(String chromeDriver,boolean isShowImage) {
        System.setProperty("webdriver.chrome.driver", chromeDriver);
        ChromeOptions options = new ChromeOptions();
         if (!isShowImage) {
             options.addArguments("blink-settings=imagesEnabled=false"); //不加载图片, 提升速度
         }
//        options.addArguments("--test-type", "--start-maximized", "--lang=" + "zh_CN.UTF-8", "--headless"); //不打开浏览器
        options.addArguments("--test-type", "--start-maximized", "--lang=zh_CN.UTF-8"); // 打开浏览器

        options.addArguments("disable-infobars");     // Chrome正在收到自动测试软件的控制
//        chrome_options.add_argument('window-size=1920x3000') #指定浏览器分辨率
//        options.addArguments("--disable-gpu"); //谷歌文档提到需要加上这个属性来规避bug
//        chrome_options.add_argument('--hide-scrollbars') #隐藏滚动条, 应对一些特殊页面
//        chrome_options.add_argument('--headless') #浏览器不提供可视化页面. linux下如果系统不支持可视化不加这条会启动失败

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        WebDriver driver = new ChromeDriver(options);

        return driver;

//        WebDriver driver = null;      //在运行测试之前单独启动ChromeDriver服务器，并使用Remote WebDriver连接到它
//        try {
//            driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return driver;
    }
}