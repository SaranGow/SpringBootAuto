package com.netzero.app.commonLibrary;

import com.netzero.app.frameworkLibrary.ConfigHelper.ConfigHelper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Component
@Scope("cucumber-glue")
public class CommonLibrary {
    @Autowired
    private WebDriver driver;
    @Autowired
    private ConfigHelper configHelper;

    public void launchApp() {
        System.out.println(configHelper.readProps("url"));
        driver.get(configHelper.readProps("url"));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Toolkit tk = Toolkit.getDefaultToolkit();
        int Width = (int) tk.getScreenSize().getWidth();
        int Height = (int) tk.getScreenSize().getHeight();
        System.out.println(Width + " " + Height);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
}
