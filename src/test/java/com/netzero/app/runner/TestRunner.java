package com.netzero.app.runner;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@RunWith(Cucumber.class)
@Configuration
@ComponentScan(basePackages = { "com.netzero.app" })
@CucumberOptions(
        plugin = {"pretty","html:target/cucumber-reports"}, //to generate different types of reporting
        features = "src/test/java/features", //the path of the feature files
        glue = {"com.netzero.app"}, //the path of the step definition files
        tags = "@smoke"
)

public class TestRunner {

}
