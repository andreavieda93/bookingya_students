package com.project.bookingya.bdd.runners;
import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/submit_form.feature",
        glue = "com.project.bookingya.bdd.stepsdefinitions",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class SubmitFormRunner {}