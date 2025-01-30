package com.zilch.zilch_coding_challenge.step_definitions;

import com.google.gson.JsonObject;
import com.zilch.zilch_coding_challenge.pages.BookStore;
import com.zilch.zilch_coding_challenge.pages.WebForm;
import com.zilch.zilch_coding_challenge.reporting.ReportOverview;
import com.zilch.zilch_coding_challenge.runner.*;
import com.zilch.zilch_coding_challenge.utils.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {
    private final Logger logger = LogManager.getLogger(StepDefinitions.class);
    private BrowserRunTime browserRunTime;
    private BrowserActions browserActions;
    private BrowserMeta browserMeta;
    private static SuiteMeta suiteMeta;
    private static boolean isSetup = false;
    private static boolean isRun = false;
    private static int iterator = 1;

    @Before
    public void setupEnvironment(Scenario scenario) throws IOException, AWTException {
        CustomScreenRecorder customScreenRecorder = new CustomScreenRecorder(new File(System.getProperty("user.dir") + "/report/"));
        List<Map<String, String>> propertyList = FileProperties.readPropertiesAsList(System.getProperties());

        for(Map<String, String> propertyMap : propertyList) {
            for(Map.Entry<String, String> propertyMapEntry : propertyMap.entrySet()) {
                if(propertyMapEntry.getKey().equals("cucumber.options")) {
                    for (String tagName : scenario.getSourceTagNames()) {
                        if (propertyMapEntry.getValue().contains(tagName)) {
                            isRun = true;
                            if (!isSetup) {
                                suiteMeta = new SuiteMeta.Builder().withGroups(Collections.singletonList(System.getProperties().toString())).build();
                                Runtime.getRuntime().addShutdownHook(new Thread(this::startClosure));
                                isSetup = true;

                            }

                            String featureName = "Feature-" + scenario.getId();
                            String scenarioName = "Scenario-" + iterator +"-" + scenario.getName().replace(" ", "-");
                            EnvironmentPropertyReader environmentPropertyReader = new EnvironmentPropertyReader("Zilch");
                            browserMeta = new BrowserMeta.Builder()
                                    .withConfiguration("Zilch")
                                    .withReference("Test Reference")
                                    .withDescription("Zilch Coding Challenge Tech Task")
                                    .withUrl(environmentPropertyReader.getProperties().getProperty("desktopUrl"))
                                    .withGroups("Zilch")
                                    .withSimulator("Not Specified")
                                    .withProperties(environmentPropertyReader.getProperties())
                                    .withInvokingClass(featureName)
                                    .withMethodName(scenarioName)
                                    .withPropertiesList(null)
                                    .build();

                            String pattern = "dd-MM-yyyy";
                            String dateInString = new SimpleDateFormat(pattern).format(new Date());
                            customScreenRecorder.startRecording(browserMeta.getClassName() + "-"
                                    + environmentPropertyReader.getProperties().getProperty("browserType") + "-"
                                    + browserMeta.getMethodName() + "-"
                                    + browserMeta.getSimulator().replace(" ", "_") + "-"
                                    + dateInString +"-Recording");
                            browserRunTime = new BrowserRunTime.Builder().withBrowserMeta(browserMeta).build();
                            browserActions = new BrowserActions();
                            iterator++;
                        }
                    }
                }
            }
        }
    }

    @Given("^I want to know if a book exists in the book store$")
    public void iWantToKnowIfABookExistsInTheBookStore() {
        if (isRun) {
            Utils.setupApiTest("BookStore/V1/book/");
        }
    }

    @Given("^I start up my browser$")
    public void iStartUpMyBrowser() {
        if (isRun) {
            logger.info("Starting Selenium Webdriver");
            browserActions.setCucumberFlag(true);
            browserActions.setDriver(BrowserRunTime.getDriver());
        }
    }

    @When("^I navigate to the ToolsQA Demo Book Store$")
    public void iNavigateToTheToolsQADemoBookStore() {
        if (isRun) {
            browserActions.changeUrl("https://demoqa.com/books");
        }
    }

    @When("I request the book via the ISBN {string}")
    public void iRequestTheBookViaTheISBN(String isbn) {
        if (isRun) {
            Utils.getBookByISBN(isbn);
        }
    }

    @When("^I navigate to the ToolsQA Demo Web Form$")
    public void iNavigateToTheToolsQADemoWebForm() {
        if (isRun) {
            browserActions.changeUrl("https://demoqa.com/automation-practice-form");
        }
    }

    @When("^I fill in my details correctly$")
    public void iFillInMyDetailsCorrectly() throws BrowserException {
        if (isRun) {
            browserActions.focus(Locator.tag(WebForm.HEADER));
            browserActions.focus(Locator.id(WebForm.FIRST_NAME))
                    .compose("Test")
                    .focus(Locator.id(WebForm.LAST_NAME))
                    .compose("User");
            browserActions.focus(Locator.id(WebForm.USER_EMAIL))
                    .compose("foo@bar.com");
            browserActions.focus(Locator.id(WebForm.GENDER_WRAPPER))
                    .collect(HtmlTags.INPUT)
                    .select("Male")
                    .touch();
            browserActions.focus(Locator.id(WebForm.USER_TELEPHONE_NUMBER))
                    .compose("07234567890");
            browserActions.focus(Locator.id(WebForm.USER_DATE_OF_BIRTH))
                    .touch()
                    .focus(Locator.xpath(WebForm.DATE_OF_BIRTH_YEAR))
                    .collect(HtmlTags.OPTION)
                    .select(115)
                    .touch()
                    .focus(Locator.xpath(WebForm.DATE_OF_BIRTH_DAY))
                    .touch();
            browserActions.scrollToElement(Locator.id(WebForm.SUBMIT_BUTTON))
                    .focus(Locator.id(WebForm.SUBMIT_BUTTON))
                    .touch();
        }
    }

    @Then("^I confirm that the details have been submitted$")
    public void iConfirmThatTheDetailsHaveBeenSubmitted() throws BrowserException {
        if (isRun) {
            browserActions.focus(Locator.id(WebForm.MODAL_SUBMIT));
            assertEquals("thanksforsubmittingtheform", browserActions.getText());
        }
    }

    @Then("^I confirm that the search bar is present$")
    public void iConfirmThatTheSearchBarIsPresent() throws BrowserException {
        if (isRun) {
            browserActions.focus(Locator.id(BookStore.SEARCH_BOX));
            assertEquals("typetosearch", browserActions.getText());
        }
    }

    @Then("the book will be {string}")
    public void theBookWillBe(String title) {
        if (isRun) {
            JsonObject returnedObject = Utils.verifyBookTitle(title);
            assertEquals(title, returnedObject.get("title").toString());
        }
    }

    @After
    public void doAfterActions() {
        if (isRun) {
            browserRunTime.quit();
            browserActions.generateReport();
            isRun = false;
        }
    }

    private void startClosure() {
        suiteMeta.close();
        ReportOverview reportOverview = new ReportOverview(suiteMeta);
        reportOverview.run();
        if (System.getProperty("openreport").equals("true")) {
            new BrowserRunTime("file:///" + System.getProperty("user.dir") + "/report/reportOverview.html");
        }
    }
}