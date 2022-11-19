package com.netzero.app.stepdef;


import com.netzero.app.runner.TestRunner;
import com.netzero.app.commonLibrary.CommonLibrary;
import com.netzero.app.frameworkLibrary.ConfigHelper.LoggerHelper;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {TestRunner.class})
@Scope("cucumber-glue")
public class Login {

    @Autowired
    private WebDriver driver;
    @Autowired
    private CommonLibrary commonLibrary;

    private static Logger log = LoggerHelper.getLogInstance(Login.class);

    @Given("Launch Google site")
    public void launchGoogleSite() {
        commonLibrary.launchApp();
        log.info("Application Launched");
    }

    @Then("Verify Title`")
    public void verifyTitle() {
        log.info(driver.getTitle());


    }

    @After
    public void screenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png", "screenshot");
        }
        driver.quit();
    }

}
