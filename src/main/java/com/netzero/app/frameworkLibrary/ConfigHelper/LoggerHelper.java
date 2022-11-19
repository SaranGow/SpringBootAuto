package com.netzero.app.frameworkLibrary.ConfigHelper;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerHelper {

    public static Logger getLogInstance(Class cls) {
        return LogManager.getLogger(cls);
    }

}
