package com.codezee.app.frameworkLibrary.ConfigHelper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@Component
@Scope("cucumber-glue")
public class ConfigHelper {

    Properties prop = new Properties();

    public String readProps(String propKey) {
        InputStream propFile;
        try {
            propFile = new FileInputStream((System.getProperty("user.dir") + "\\src\\test\\java\\resources\\config.properties"));
            prop.load(propFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop.getProperty(propKey);

    }

    public String getPassword(String userName) {

        String environment = System.getProperty("env");

        if (environment != null && environment.contains("PROD") || environment.contains("UAT")) {
            return System.getenv(getProdUserType(userName) + "Pwd");
        } else {
            return readProps("password");
        }
    }

    private String getProdUserType(String userName) {
        if (userName.contains("Dinesh")) {
            return "admin";
        } else {
            return "user";
        }
    }

}
