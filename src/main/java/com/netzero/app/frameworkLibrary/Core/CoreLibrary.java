package com.netzero.app.frameworkLibrary.Core;

import com.netzero.app.DataHolder.DataHolder;
import com.netzero.app.frameworkLibrary.ConfigHelper.ConfigHelper;
import com.netzero.app.frameworkLibrary.ConfigHelper.LoggerHelper;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Scope("cucumber-glue")
public class CoreLibrary {

    WebDriver driver;
    @Autowired
    private ConfigHelper configHelper;

    @Autowired
    private DataHolder dataHolder;

    private static Logger log = LoggerHelper.getLogInstance(CoreLibrary.class);

    public WebDriver getDriverInstance() {
        String browser = configHelper.readProps("browser");
        switch (browser) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\java\\resources\\chromedriver.exe");
                driver = new ChromeDriver(getChromeOptions());
                break;
            case "firefox":
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\java\\resources\\chromedriver.exe");
                driver = new ChromeDriver(getChromeOptions());
                break;
            default:
                System.out.println("Invalid Browser");
        }
        return driver;
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
 /*       File ChromeApp = new File(configHelper.readProps("chromeBinaryLocal"));
        if (ChromeApp.exists()) {
            options.setBinary(configHelper.readProps("chromeBinaryLocal"));
        } else {
            options.setBinary(configHelper.readProps("chromeBinary"));
        }*/
        Map<String, Object> prefs = new HashMap<String, Object>();
        creatDir();
        prefs.put("download.default_directory", dataHolder.getFolderPathForDownload().getAbsolutePath());
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.directory_upgrade", true);
        prefs.put("download.prompt_for_download", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("profile.default_content_settings.exceptions.automatic_download.*.setting", 1);
        options.setExperimentalOption("prefs", prefs);
        //options.addArguments("headless");
        options.addArguments("window-size=1382,744");
        return options;
    }


    private void creatDir() {
        String folderName = UUID.randomUUID().toString();
        dataHolder.setFolderPathForDownload(new File(System.getProperty("user.dir") + "\\target\\" + folderName));
        boolean folderCreated = dataHolder.getFolderPathForDownload().mkdir();
        if (folderCreated) {
            System.out.println("Directory Created Successfully");
        } else {
            System.out.println("Unable to Create Directory");
        }
    }


    @Bean
    @Scope("cucumber-glue")
    public WebDriver webDriver() {
        return getDriverInstance();
    }
}
