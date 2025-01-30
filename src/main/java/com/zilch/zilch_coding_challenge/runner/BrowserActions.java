package com.zilch.zilch_coding_challenge.runner;

import com.zilch.zilch_coding_challenge.reporting.ReportGenerator;
import com.zilch.zilch_coding_challenge.utils.HtmlTags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class BrowserActions implements BrowserActionsInterface {
    private static final Logger logger = LogManager.getLogger(BrowserActions.class);
    private WebElement focusedElement;
    private List<WebElement> focusedList;
    private static final List<BrowserAction> reportList = new ArrayList<>();
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean listFlag = false;
    private boolean cucumberFlag = false;

    private static final String VALUE = "value";
    private static final String VERSUS = " Vs: ";
    private static final String STORE = "*** store: ";
    private static final String SELECT = "*** select: ";
    private static final String ABSENT = "*** absent: ";
    private static final String NO_MATCH = "No match: ";
    private static final String PRESENT = "*** present: ";
    private static final String CONTAINS = "*** contains: ";
    private static final String RETRIEVE = "*** retrieve: ";
    private static final String PLACEHOLDER = "placeholder";
    private static final String ACTIVE_TEXT = "Active text: \"";
    private static final String REGEX_STRING = "[^a-zA-Z0-9@.+]";
    private static final String SELECT_FAIL = "Cannot select by text";
    private static final String ELEMENT_VALUE_OF = "Element's value of ";
    private static final String COMPARE_NO_MATCH = "*** compare: No match: ";
    private static final String MATCH_EXPECTED_TEXT = "\" matched text of \"";
    private static final String NOT_EXPECTED_TO = "\" but was not expected to";
    private static final String TEXT_ACTUALLY = "\" but the text found was actually \"";
    private static final String NO_MATCH_EXPECTED_TEXT = "\" did not match expected text of \"";
    private static final String CONTENT_NOT_MATCHING = " does not match the expected content of ";
    private static final String EXPECTED_FIND_CONTENT = "*** matches(): Expected to find content \"";
    private static final String DID_NOT_COMPARE = "\" did not compare against focused element's text: \"";
    private static final String ABSENT_ELEMENT_TEXT_OF = "*** BrowserActions: absent(): Element text of \"";
    private static final String PRESENT_ELEMENT_TEXT_OF = "*** BrowserActions: present(): Element text of \"";

    private enum Probe { EXECUTE, IGNORE, REPEAT }

    private Probe probe = Probe.EXECUTE;
    private String activeText = null;
    private String storedText = null;
    private final Map<String, String> textMap = new HashMap<>();

    public BrowserActions changeFrames(String frameName) {
        driver.switchTo().frame(frameName);
        return this;
    }

    public void changeUrl(String url) {
        driver.get(url);
    }

    public BrowserActionsInterface scrollToElement(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setActionInfo(by.toString());
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            } catch (Exception e) {
                logger.error("Element wasn't scrollable in the DOM: {}", by);
                report.setStatusLine("*** focus: Element wasn't found in the DOM");
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActions() {
        focusedList = new ArrayList<>();
    }

    public BrowserActionsInterface origin() {
        focusedElement = driver.findElement(HtmlTags.HTML);
        return this;
    }

    public BrowserActionsInterface focus(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setActionInfo(by.toString());
            try {
                focusedElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));
                report.setStatusLine(getElementInfo(focusedElement));
            } catch (Exception e) {
                logger.error("Element wasn't found in the DOM: {}", by);
                report.setStatusLine("*** focus: Element wasn't found in the DOM");
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface ascend() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement = focusedElement.findElement(By.xpath(".."));
                report.setStatusLine("*** ascend to: " + getElementInfo(focusedElement));
                report.setActionInfo(focusedElement.getText());
            } catch (Exception e) {
                logger.error("Element cannot ascend any further");
                report.setStatusLine("*** ascend: Element cannot ascend any further");
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface descend() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement = focusedElement.findElement(By.xpath("/"));
                report.setStatusLine("*** descend to child node");
                report.setActionInfo("V");
            } catch (Exception e) {
                logger.error("Element cannot descend any further");
                report.setStatusLine("*** ascend: Element cannot descend any further");
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface descend(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement = focusedElement.findElement(by);
                report.setStatusLine("*** descend to: " + getElementInfo(focusedElement));
            } catch (Exception e) {
                logger.error("Element cannot descend any further for the locator: {}", by.toString());
                report.setStatusLine("*** ascend: Element cannot descend any further");
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface collect(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** collect: " + by.toString());
            focusedList = focusedElement.findElements(by);
            report.setActionInfo(by.toString());
            if (focusedList.isEmpty()) {
                try {
                    throw new BrowserException("BrowserActions : collect() : no collection with collector [ " + by + " ]");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    report.setStatusLine("*** collect: " + e.getMessage());
                    fail(report, e);
                }
            } else {
                logger.info("BrowserActions : collect() : Collection of size [ {} ] with collector [ {} ]", focusedList.size(), by);
                int i = 0;
                for (WebElement webElement : focusedList) {
                    logger.info("Collection index [ {} ] with text [ {} ]", i, webElement.getText());
                    i++;
                }
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface count(Integer size) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** count: " + size.toString());
            if (focusedList.size() != size) {
                try {
                    throw new BrowserException("BrowserActions : count() : sizes don't match \n Collection size: " + focusedList.size() + "\nSize checked against " + size);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    report.setStatusLine("*** count: " + e.getMessage());
                    fail(report, e);
                }
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface depart(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** depart: " + by.toString());
            report.setActionInfo(by.toString());
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
            } catch (Exception e) {
                logger.error("Element did not depart: {}", by.toString());
                report.setStatusLine("*** depart: " + e.getMessage());
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface touch() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement = wait.until(ExpectedConditions.elementToBeClickable(focusedElement));
                report.setActionInfo("^");
                report.setStatusLine("*** touch: " + focusedElement.toString());
                focusedElement.click();
            } catch (Exception e) {
                logger.error("Focussed element could not be clicked");
                report.setActionInfo("^");
                report.setStatusLine("*** touch: " + e.getMessage());
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface compose(String keysToSend) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement.sendKeys(keysToSend);
                report.setStatusLine("*** compose: " + keysToSend);
                report.setActionInfo(keysToSend);
            } catch (Exception e) {
                logger.error("Cannot compose text {}", keysToSend);
                report.setStatusLine("*** compose: " + e.getMessage());
                report.setActionInfo(keysToSend);
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface enabled() throws BrowserException {
        if (execute()) {
            enabledValidator(focusedElement);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                enabledValidator(element);
            }
        }
        return this;
    }

    public BrowserActionsInterface disabled() throws BrowserException {
        if (execute()) {
            disabledValidator(focusedElement);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                disabledValidator(element);
            }
        }
        return this;
    }

    public BrowserActionsInterface selected() throws BrowserException {
        if (execute()) {
            selectedValidator(focusedElement);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                selectedValidator(element);
            }
        }
        return this;
    }

    public BrowserActionsInterface matches() throws BrowserException {
        if (execute()) {
            hasContentValidator(focusedElement);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                hasContentValidator(element);
            }
        }
        return this;
    }

    public BrowserActionsInterface matches(String content) throws BrowserException {
        if (execute()) {
            hasSpecificContentValidator(focusedElement, content);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                hasSpecificContentValidator(element, content);
            }
        }
        return this;
    }

    public BrowserActionsInterface contains(String content) throws BrowserException {
        if (execute()) {
            containsContentValidator(focusedElement, content);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                containsContentValidator(element, content);
            }
        }
        return this;
    }

    public BrowserActionsInterface notContains(String content) throws BrowserException {
        if (execute()) {
            notContainsContentValidator(focusedElement, content);
        } else if (checkRepeat()) {
            for (WebElement element : focusedList) {
                notContainsContentValidator(element, content);
            }
        }
        return this;
    }

    public BrowserActionsInterface repeat() {
        BrowserAction report = new BrowserAction();
        report.setStatusLine("*** repeat: Starting repetition");
        probe = Probe.REPEAT;
        report.setStatusLine("v");
        report.close();
        reportList.add(report);
        return this;
    }

    public BrowserActionsInterface endRepeat() {
        BrowserAction report = new BrowserAction();
        report.setStatusLine("*** repeat: Ending repetition");
        probe = Probe.EXECUTE;
        report.setStatusLine("^");
        report.close();
        reportList.add(report);
        return this;
    }

    public BrowserActionsInterface present(String text) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            if (focusedList.isEmpty()) {
                try {
                    throw new BrowserException("*** BrowserActions: present(): Collection is not defined");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    report.setStatusLine(PRESENT + e.getMessage());
                    report.setActionInfo(text);
                    listFlag = false;
                    fail(report, e);
                }
            } else {
                boolean breakFlag = false;
                for (WebElement element : focusedList) {
                    if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) == null
                            && element.getDomAttribute(VALUE) == null) {
                        try {
                            throw new BrowserException("*** BrowserActions: present(): Element has no text that can be present");
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                        }
                    } else if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) == null
                            && element.getDomAttribute(VALUE) != null) {
                        if (!Objects.equals(element.getDomAttribute(VALUE), text)) {
                            try {
                                throw new BrowserException(PRESENT_ELEMENT_TEXT_OF + element.getDomAttribute(VALUE) + NO_MATCH_EXPECTED_TEXT + text + "\"");
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    } else if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) != null
                            && element.getDomAttribute(VALUE) == null) {
                        if (!Objects.equals(element.getDomAttribute(PLACEHOLDER), text)) {
                            try {
                                throw new BrowserException(PRESENT_ELEMENT_TEXT_OF + element.getDomAttribute(PLACEHOLDER) + NO_MATCH_EXPECTED_TEXT + text + "\"");
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    } else {
                        if (!element.getText().equals(text)) {
                            try {
                                throw new BrowserException(PRESENT_ELEMENT_TEXT_OF + element.getText() + NO_MATCH_EXPECTED_TEXT + text + "\"");
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    }
                    if (breakFlag) {
                        break;
                    }
                }
                if (!listFlag) {
                    try {
                        throw new BrowserException("*** BrowserActions: present(): Text of \"" + text + "\" was not present in the element supplied");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        report.setStatusLine(PRESENT + e.getMessage());
                        report.setActionInfo(text);
                        listFlag = false;
                        fail(report, e);
                    }
                } else {
                    report.setStatusLine(PRESENT + text);
                    report.setActionInfo(text);
                }
            }
            listFlag = false;
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface absent(String text) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            if (focusedList.isEmpty()) {
                try {
                    throw new BrowserException("*** BrowserActions: absent(): Collection is not defined");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    report.setStatusLine(ABSENT + e.getMessage());
                    report.setActionInfo(text);
                    fail(report, e);
                }
            } else {
                boolean breakFlag = false;
                for (WebElement element : focusedList) {
                    if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) == null
                            && element.getDomAttribute(VALUE) == null) {
                        try {
                            throw new BrowserException("*** BrowserActions: absent(): Element has no text that can be absent");
                        } catch (Exception e) {
                            logger.warn(e.getMessage());
                        }
                    } else if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) == null
                            && element.getDomAttribute(VALUE) != null) {
                        if (Objects.equals(element.getDomAttribute(VALUE), text)) {
                            try {
                                throw new BrowserException(ABSENT_ELEMENT_TEXT_OF + element.getDomAttribute(VALUE) + MATCH_EXPECTED_TEXT + text + NOT_EXPECTED_TO);
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    } else if (element.getText().isEmpty()
                            && element.getDomAttribute(PLACEHOLDER) != null
                            && element.getDomAttribute(VALUE) == null) {
                        if (Objects.equals(element.getDomAttribute(PLACEHOLDER), text)) {
                            try {
                                throw new BrowserException(ABSENT_ELEMENT_TEXT_OF + element.getDomAttribute(PLACEHOLDER) + MATCH_EXPECTED_TEXT + text + NOT_EXPECTED_TO);
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    } else {
                        if (element.getText().equals(text)) {
                            try {
                                throw new BrowserException(ABSENT_ELEMENT_TEXT_OF + element.getText() + MATCH_EXPECTED_TEXT + text + NOT_EXPECTED_TO);
                            } catch (Exception e) {
                                logger.warn(e.getMessage());
                            }
                        } else {
                            listFlag = true;
                            breakFlag = true;
                        }
                    }
                    if (breakFlag) {
                        break;
                    }
                }
                if (!listFlag) {
                    try {
                        throw new BrowserException("*** BrowserActions: absent(): Text of \"" + text + "\" was present in the element supplied or a fatal error occurred.");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        report.setStatusLine(ABSENT + e.getMessage());
                        report.setActionInfo(text);
                        listFlag = false;
                        fail(report, e);
                    }
                } else {
                    report.setStatusLine(ABSENT + text);
                    report.setActionInfo(text);
                }
            }
            listFlag = false;
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface select(String text) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine(SELECT + text);
            try {
                for (WebElement element : focusedList) {
                    String str = element.getText();
                    if (str.equals(text)) {
                        focusedElement = element;
                        break;
                    }
                }
                report.setActionInfo("Text at: " + text);
            } catch (Exception e) {
                logger.error(SELECT_FAIL);
                report.setStatusLine(SELECT + e.getMessage());
                fail(report, e);
            }
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface select(Integer index) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine(SELECT + index);
            try {
                focusedElement = focusedList.get(index);
            } catch (Exception e) {
                logger.error("Cannot select by index: {}", index);
                report.setStatusLine(SELECT + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo("Index at: " + index.toString());
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface range(Integer size) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** range");
            try {
                if(focusedList.size() != size) {
                    throw new BrowserException("*** range(): The collection of elements in the list was not of size \"" + size + "\" instead, it was \"" + focusedList.size() + "\"");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine("*** range: " + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo("Range of: " + size);
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface range() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** range");
            try {
                if(focusedList.isEmpty()) {
                    throw new BrowserException("*** range(): The collection of elements in the list has no size");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine("*** range: " + e.getMessage());
                fail(report, e);
            }
            report.setStatusLine("*** range: The collection has a size of \"" + focusedList.size() + "\"");
            report.setActionInfo("range()");
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface store() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** store");
            try {
                if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(PLACEHOLDER)) && isNullOrEmpty(focusedElement.getDomAttribute(VALUE))) {
                    throw new BrowserException("The current focused element \"" + focusedElement.toString() + "\" has no text to store");
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(PLACEHOLDER))) {
                    storedText = focusedElement.getDomAttribute(VALUE);
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(VALUE))) {
                    storedText = focusedElement.getDomAttribute(PLACEHOLDER);
                } else {
                    storedText = focusedElement.getText();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(STORE + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo("^");
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface store(String key) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine(STORE + key);
            try {
                if (isNullOrEmpty(key)) {
                    throw new BrowserException("The key you provided did not have any text in it");
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(PLACEHOLDER))) {
                   textMap.put(key,focusedElement.getDomAttribute(VALUE));
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(VALUE))) {
                    textMap.put(key,focusedElement.getDomAttribute(PLACEHOLDER));
                } else {
                    textMap.put(key,focusedElement.getText());
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(STORE + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo(key);
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface probe(By by) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            try {
                focusedElement = wait.until(ExpectedConditions.presenceOfElementLocated(by));
                report.setStatusLine("*** probe: " + getElementInfo(focusedElement));
            } catch (TimeoutException te) {
                logger.info("Probe could not find the object in the DOM.");
                probe = Probe.IGNORE;
                report.setStatusLine("*** probe: Probe could not find the object in the DOM: " + te.getMessage());
            } catch (Exception e) {
                report.setStatusLine("*** probe: Probe could not find the object in the DOM: " + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo(by.toString());
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface endProbe() {
        probe = Probe.EXECUTE;
        return this;
    }

    public BrowserActionsInterface retrieve() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** retrieve");
            try {
                if (isNullOrEmpty(storedText)) {
                    throw new BrowserException("There was no text that has been stored");
                } else {
                    activeText = storedText;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(RETRIEVE + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo("^");
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface retrieve(String key) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine(RETRIEVE + key);
            try {
                if (isNullOrEmpty(key)) {
                    throw new BrowserException("The key has not been provided");
                } else if (isNullOrEmpty(textMap.get(key))) {
                    throw new BrowserException("The key did not match any text stored");
                } else {
                    activeText = textMap.get(key);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(RETRIEVE + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo(key);
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface compare() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** compare");
            try {
                if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(PLACEHOLDER)) && isNullOrEmpty(focusedElement.getDomAttribute(VALUE))) {
                    throw new BrowserException("The current focused element \"" + focusedElement.getTagName() + "\" has no text to compare");
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(PLACEHOLDER))) {
                   if (!activeText.equals(focusedElement.getDomAttribute(VALUE))) {
                       report.setStatusLine(COMPARE_NO_MATCH + activeText + VERSUS + focusedElement.getDomAttribute(VALUE));
                       report.setActionInfo(NO_MATCH + activeText + VERSUS + focusedElement.getDomAttribute(VALUE));
                       throw new BrowserException(ACTIVE_TEXT + activeText + DID_NOT_COMPARE + focusedElement.getDomAttribute(VALUE) + "\"");
                   }
                } else if (isNullOrEmpty(focusedElement.getText()) && isNullOrEmpty(focusedElement.getDomAttribute(VALUE))) {
                    if (!activeText.equals(focusedElement.getDomAttribute(PLACEHOLDER))) {
                        report.setStatusLine(COMPARE_NO_MATCH + activeText + VERSUS + focusedElement.getDomAttribute(PLACEHOLDER));
                        report.setActionInfo(NO_MATCH + activeText + VERSUS + focusedElement.getDomAttribute(PLACEHOLDER));
                        throw new BrowserException(ACTIVE_TEXT + activeText + DID_NOT_COMPARE + focusedElement.getDomAttribute(PLACEHOLDER) + "\"");
                    }
                } else {
                    if (!activeText.equals(focusedElement.getText())) {
                        report.setStatusLine(COMPARE_NO_MATCH + activeText + VERSUS + focusedElement.getText());
                        report.setActionInfo(NO_MATCH + activeText + VERSUS + focusedElement.getText());
                        throw new BrowserException(ACTIVE_TEXT + activeText + DID_NOT_COMPARE + focusedElement.getText() + "\"");
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine("*** compare: " + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo(activeText);
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface clear() throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** clear");
            try {
                wait.until(ExpectedConditions.elementToBeClickable(focusedElement));
                focusedElement.clear();
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine("*** clear: " + e.getMessage());
                fail(report, e);
            }
            report.setActionInfo("^");
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public BrowserActionsInterface pause(Integer seconds) {
        BrowserAction report = new BrowserAction();
        report.setStatusLine("*** pause");
        try {
            Thread.sleep((long)seconds * 1000);
            report.setActionInfo(seconds + " second(s)");
        } catch (InterruptedException ie) {
            logger.error(ie.getMessage());
            Thread.currentThread().interrupt();
            report.setStatusLine("*** pause: " + ie.getMessage());
        }
        report.close();
        reportList.add(report);
        return this;
    }

    public String getText() throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.setStatusLine("*** getText");
        String focusedElementText = "";

        if (focusedElement.getText().isEmpty()
                && focusedElement.getDomAttribute(PLACEHOLDER) == null
                && focusedElement.getDomAttribute(VALUE) == null) {
            logger.error("focusedElement had no text to compare against");
        } else if (focusedElement.getText().isEmpty()
                && focusedElement.getDomAttribute(PLACEHOLDER) == null
                && focusedElement.getDomAttribute(VALUE) != null) {
            focusedElementText = focusedElement.getDomAttribute(VALUE);
        } else if (focusedElement.getText().isEmpty()
                && focusedElement.getDomAttribute(PLACEHOLDER) != null
                && focusedElement.getDomAttribute(VALUE) == null) {
            focusedElementText = focusedElement.getDomAttribute(PLACEHOLDER);
        } else {
            focusedElementText = focusedElement.getText();
        }

        focusedElementText = focusedElementText.replaceAll(REGEX_STRING, "").toLowerCase();

        if (focusedElementText.isEmpty()) {
            try {
                throw new BrowserException(ELEMENT_VALUE_OF + focusedElementText + " is empty");
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(ELEMENT_VALUE_OF + focusedElementText + " is empty");
                fail(report, e);
            }
        }
        report.setActionInfo("getText() - Getting current element's text");
        report.close();
        reportList.add(report);
        return focusedElementText;
    }

    public BrowserActionsInterface capture() {
        BrowserAction report = new BrowserAction();
        report.setStatusLine("*** capture");
        report.setActionInfo("Capture of: " + driver.getCurrentUrl());
        report.takeVideo(driver);
        report.close();
        reportList.add(report);
        return this;
    }

    public BrowserActionsInterface perform(RunnableItem runnableItem) throws BrowserException {
        if (execute()) {
            BrowserAction report = new BrowserAction();
            report.setStatusLine("*** perform: " + runnableItem.getDescription());
            report.setActionInfo("Start: " + runnableItem.getClass().getName());
            report.close();
            reportList.add(report);

            report = new BrowserAction();
            report.setActionInfo("End: " + runnableItem.getClass().getName());
            report.setStatusLine("*** performed: " + runnableItem.getDescription());

            runnableItem.run();

            report.setSuccessStatus(runnableItem.getStatus());
            report.close();
            reportList.add(report);
        }
        return this;
    }

    public void setCucumberFlag(Boolean flag) {
        this.cucumberFlag = flag;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void generateReport() {
        for (BrowserAction browserAction : reportList) {
            if (Boolean.FALSE.equals(browserAction.getSuccessStatus())) {
                BrowserRunTime.fail();
                break;
            }
        }
        BrowserRunTime.getBrowserMeta().close();

        int passedSteps = 0;
        int failedSteps = 0;

        for (BrowserAction browserAction : reportList) {
            if (Boolean.TRUE.equals(browserAction.getSuccessStatus())) {
                passedSteps++;
            } else {
                failedSteps++;
            }
        }
        BrowserRunTime.getBrowserMeta().setStepsPassed(passedSteps);
        BrowserRunTime.getBrowserMeta().setStepsFailed(failedSteps);

        ReportGenerator reportGenerator = new ReportGenerator(BrowserRunTime.getBrowserMeta(), reportList, BrowserRunTime.getBrowserMeta().getProperties().getProperty("browserType"));
        reportGenerator.run();

        reportList.clear();
    }

    private void notContainsContentValidator(WebElement element, String content) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("contains");
        report.setStatusLine(CONTAINS + element.toString() + " not containing content " + content);
        if (element.getText().toLowerCase().contains(content.toLowerCase())) {
            try {
                throw new BrowserException(ELEMENT_VALUE_OF + element.getText().toLowerCase() + " contains the value of " + content.toLowerCase());
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(CONTAINS + element.getText() + "contains the value of " + content + "\"");
                fail(report, e);
            }
        }
        report.setActionInfo("Not containing search for " + content);
        report.close();
        reportList.add(report);
    }

    private void containsContentValidator(WebElement element, String content) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("contains");
        report.setStatusLine(CONTAINS + element.toString() + " containing content " + content);
        String elementText = "";

        if (element.getText().isEmpty()
                && element.getDomAttribute(PLACEHOLDER) == null
                && element.getDomAttribute(VALUE) == null) {
            logger.error("Element had no text to compare against");
        } else if (element.getText().isEmpty()
                && element.getDomAttribute(PLACEHOLDER) == null
                && element.getDomAttribute(VALUE) != null) {
            elementText = element.getDomAttribute(VALUE);
        } else if (element.getText().isEmpty()
                && element.getDomAttribute(PLACEHOLDER) != null
                && element.getDomAttribute(VALUE) == null) {
            elementText = element.getDomAttribute(PLACEHOLDER);
        } else {
            elementText = element.getText();
        }

        elementText = elementText.replaceAll(REGEX_STRING, "").toLowerCase();
        content = content.replaceAll(REGEX_STRING, "").toLowerCase();

        if (!elementText.contains(content)) {
            try {
                throw new BrowserException(ELEMENT_VALUE_OF + elementText + " does not contain the value of " + content);
            } catch (Exception e) {
                logger.error(e.getMessage());
                report.setStatusLine(CONTAINS + elementText + " does not contain the value of " + content + "\"");
                fail(report, e);
            }
        }
        report.setActionInfo("Containing search for " + content);
        report.close();
        reportList.add(report);
    }

    private void hasSpecificContentValidator(WebElement element, String content) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("matches");
        report.setStatusLine("*** matches: " + element.toString() + " with content: " + content);
        if (element.getText().isEmpty()) {
            if (element.getDomAttribute(PLACEHOLDER) == null) {
                if (element.getTagName().equals("input")) {
                    if (element.getDomAttribute(VALUE) == null) {
                        try {
                            throw new BrowserException("Element has no value " + element.getTagName());
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            report.setStatusLine(EXPECTED_FIND_CONTENT + content + "\" but no text was visible");
                            fail(report, e);
                        }
                    } else {
                        String elementText = (element.getDomAttribute(VALUE));
                        elementText = elementText.replaceAll(REGEX_STRING, "").toLowerCase();
                        content = content.replaceAll(REGEX_STRING, "").toLowerCase();
                        if (!content.equals(elementText)) {
                            try {
                                throw new BrowserException(ELEMENT_VALUE_OF + elementText + CONTENT_NOT_MATCHING + content);
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                                report.setStatusLine(EXPECTED_FIND_CONTENT + content + TEXT_ACTUALLY + elementText + "\"");
                                fail(report, e);
                            }
                        }
                    }
                } else {
                    try {
                        throw new BrowserException("Element was expected to contain content of " + content + " but had no content");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        report.setStatusLine(EXPECTED_FIND_CONTENT + content + "\" but no text was visible");
                        fail(report, e);
                    }
                }
            } else {
                String elementText = "";
                if (element.getDomAttribute(PLACEHOLDER) != null) {
                    elementText = element.getDomAttribute(PLACEHOLDER);
                }
                elementText = elementText.replaceAll(REGEX_STRING, "").toLowerCase();
                content = content.replaceAll(REGEX_STRING, "").toLowerCase();
                if (!content.equals(elementText)) {
                    try {
                        throw new BrowserException(ELEMENT_VALUE_OF + elementText + CONTENT_NOT_MATCHING + content);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        report.setStatusLine(EXPECTED_FIND_CONTENT + content + TEXT_ACTUALLY + elementText + "\"");
                        fail(report, e);
                    }
                }
            }
        } else {
            String elementText = element.getText();
            elementText = elementText.replaceAll(REGEX_STRING, "").toLowerCase();
            content = content.replaceAll(REGEX_STRING, "").toLowerCase();
            if (!content.equals(elementText)) {
                try {
                    throw new BrowserException(ELEMENT_VALUE_OF + elementText + CONTENT_NOT_MATCHING + content);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    report.setStatusLine(EXPECTED_FIND_CONTENT + content + TEXT_ACTUALLY + elementText + "\"");
                    fail(report, e);
                }
            }
        }
        report.setActionInfo("Matching search for " + content);
        report.close();
        reportList.add(report);
    }

    private void hasContentValidator(WebElement element) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("matches");
        report.setStatusLine("*** matches: " + element.toString());
        if (element.getText().isEmpty() && element.getDomAttribute(PLACEHOLDER) == null) {
            try {
                throw new BrowserException("Element has no content to validate: " + element.getTagName());
            } catch (Exception e) {
                logger.error(e.getMessage());
                fail(report, e);
            }
        }
        report.setActionInfo(getElementInfo(element));
        report.close();
        reportList.add(report);
    }

    private void selectedValidator(WebElement element) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("selected");
        report.setStatusLine("*** selected: " + element.toString());
        try {
            wait.until(ExpectedConditions.elementToBeSelected(element));
            report.setActionInfo(getElementInfo(element));
            element.isSelected();
        } catch (Exception e) {
            logger.error("Element is not selected");
            report.setActionInfo(getElementInfo(element));
            fail(report, e);
        }
        report.close();
        reportList.add(report);
    }

    private void disabledValidator(WebElement element) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("disabled");
        report.setStatusLine("*** disabled: " + getElementInfo(element));
        try {
            if (!element.isEnabled()) {
                logger.info("Element is disabled");
                report.setActionInfo(getElementInfo(element));
            } else {
                throw new BrowserException("Element is not disabled");
            }
        } catch (Exception e) {
            logger.error("Element is not disabled");
            report.setActionInfo(getElementInfo(element));
            fail(report, e);
        }
        report.close();
        reportList.add(report);
    }

    private void enabledValidator(WebElement element) throws BrowserException {
        BrowserAction report = new BrowserAction();
        report.resetMethodName("enabled");
        report.setStatusLine("*** enabled: " + getElementInfo(element));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            report.setActionInfo("^");
            element.isEnabled();
        } catch (Exception e) {
            logger.error("Element is not enabled");
            report.setActionInfo("^");
            fail(report, e);
        }
        report.close();
        reportList.add(report);
    }

    private boolean execute() {
        return probe.equals(Probe.EXECUTE);
    }

    private boolean checkRepeat() {
        return probe.equals(Probe.REPEAT);
    }

    private String getElementInfo(WebElement webElement) {
        String elementText = webElement.getText();

        if (!elementText.isEmpty()) {
            elementText = "TEXT: " + elementText;
        } else {
            elementText = webElement.getDomAttribute(PLACEHOLDER);
            if (!isNullOrEmpty(elementText)) {
                elementText = "PLACEHOLDER: " + elementText;
            } else {
                elementText = "TAG-NAME: " + webElement.getTagName();
            }
        }
        return elementText;
    }

    private void fail(BrowserAction report, Exception e) throws BrowserException {
        if (cucumberFlag) {
            report.setSuccessStatus(false);
            report.takeVideo(driver);
            report.setVideoTaken(true);
            report.close();
            reportList.add(report);
            throw new BrowserException(e.getMessage());
        } else {
            report.setSuccessStatus(false);
            report.takeVideo(driver);
            report.setVideoTaken(true);
            logger.error(e.getStackTrace());
        }
    }
}