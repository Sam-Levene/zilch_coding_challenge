package com.zilch.zilch_coding_challenge.reporting;

import com.zilch.zilch_coding_challenge.runner.BrowserAction;
import com.zilch.zilch_coding_challenge.runner.BrowserMeta;
import com.zilch.zilch_coding_challenge.utils.Utils;
import j2html.tags.ContainerTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

import static j2html.TagCreator.*;

public class ReportGenerator {
    private final List<BrowserAction> browserActionList;
    private final BrowserMeta browserMeta;
    private final String browserName;
    private static final String DATA_TOGGLE = "data-toggle";
    private static final String DATA_TARGET = "data-target";
    private static final String CARD_TEXT = "card-text";
    private static final String CENTERED = "centered";
    private static final String COLLAPSE = "collapse";
    private static final String BUTTON = "button";
    private static final String MODAL = "modal";
    private static final Logger logger = LogManager.getLogger(ReportGenerator.class);
    private Integer pass = 0;
    private Integer fail = 0;
    private Integer modalCounter = 1;

    public ReportGenerator(BrowserMeta browserMeta, List<BrowserAction> reportList, String browserName) {
        this.browserActionList = reportList;
        this.browserMeta = browserMeta;
        this.browserName = browserName;
        this.browserMeta.getDuration();
    }

    public void run() {
        numberOfObject();

        String html = html(
                head(
                        title("Salesforce Seasonal Release Test Automation Report"),
                        link().withRel("stylesheet").withHref("./bootstrap/css/salesforce.css"),
                        link().withRel("stylesheet").withHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"),
                        script().withSrc("https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"),
                        script().withSrc("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"),
                        script().withSrc("https://www.gstatic.com/charts/loader.js")
                ),
                body(
                        generateReportHead(),
                        div().withClasses(".container-fluid topMargin").with(
                                h1().withClasses(CENTERED).withText("Salesforce Seasonal Release Test Automation Report"),
                                div().withClasses("row").with(
                                        div().withClasses("col-md-4").with(
                                                getReportMetaData(),
                                                div().withId("pieChartPlaceholder").withClasses("tableAdjust")
                                        ),
                                        div().withClasses("col-md-8 mainContent topMargin").with(
                                                h3().withClasses(CENTERED).withId("testSteps").withText("Test Steps"),
                                                each(browserActionList, this::generateCode)
                                        ),
                                        drawPieChart()
                                )
                        ),
                        script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js")
                )
        ).render();

        File file = new File(System.getProperty("user.dir") + "/report/" + browserMeta.getClassName() + "-" + this.browserName + "-" + browserMeta.getMethodName() + "-" + browserMeta.getSimulator().replace(" ", "_") + ".html");
        Utils.fileWriter(file, html);
    }

    private ContainerTag drawPieChart() {
        return script().withType("text/javascript").with(
                rawHtml(
                        "google.charts.load('current', {'packages':['corechart']}); \n" +
                                "google.charts.setOnLoadCallback(drawChart); \n" +
                                "function drawChart() { var data = google.visualization.arrayToDataTable([\n" +
                                "['Task', 'Hours per Day']," +
                                "['Success', "+pass+"]," +
                                "['Fail', "+fail+"]," +
                                "]); \n" +

                                "var options = {'title':'Salesforce Seasonal Release Test Results', 'width':550, 'height':400, 'is3D':true, 'colors': ['#009900', '#990000']};\n" +

                                "var chart = new google.visualization.PieChart(document.getElementById('pieChartPlaceholder'));\n" +
                                "chart.draw(data, options);}"
                )
        );
    }

    private ContainerTag generateCode(BrowserAction browserAction) {
        if (Boolean.TRUE.equals(browserAction.getSuccessStatus())) {
            return div().withClasses("row centered").with(
                    p().withClasses("centered btn-lg btn-block").with(
                            button().withClasses("btn btn-success centered btn-lg btn-block").withType(BUTTON)
                                    .attr(DATA_TOGGLE, COLLAPSE)
                                    .attr(DATA_TARGET, "#success-" + browserAction.getActionId())
                                    .attr("aria-expanded", "false")
                                    .attr("aria-controls", "success-" + browserAction.getActionId())
                                    .withText(browserAction.getMethodName() + ":" + browserAction.getActionInfo()),
                            div().withClasses("collapse center-card").withId("success-"+browserAction.getActionId()).with(
                                    div().withClasses("card card-body").with(
                                            checkVideoTaken(browserAction)
                                    )
                            )
                    )
            );
        } else {
            return div().withClasses("row centered").with(
                    p().withClasses("centered btn-lg btn-block").with(
                            button().withClasses("btn btn-danger centered btn-lg btn-block").withType(BUTTON)
                                    .attr(DATA_TOGGLE, COLLAPSE)
                                    .attr(DATA_TARGET, "#error-" + browserAction.getActionId())
                                    .attr("aria-expanded", "false")
                                    .attr("aria-controls", "error-" + browserAction.getActionId())
                                    .withText(browserAction.getMethodName() + ":" + browserAction.getActionInfo()),
                            div().withClasses("collapse center-card").withId("error-"+browserAction.getActionId()).with(
                                    div().withClasses("card card-body").with(
                                            checkVideoTaken(browserAction)
                                    )
                            )
                    )
            );
        }
    }

    private ContainerTag checkVideoTaken(BrowserAction browserAction) {
        if (Boolean.TRUE.equals(browserAction.getVideoTaken())) {
            return getContentWithScreenshot(browserAction);
        } else {
            return getContent(browserAction);
        }
    }

    private ContainerTag getContent(BrowserAction browserAction) {
        return div().with(
                p().withClass(CARD_TEXT).with(rawHtml(browserAction.getStatusLine())),
                p().withClass(CARD_TEXT).with(rawHtml(browserAction.getStackTrace())),
                p().withClass(CARD_TEXT).withText(browserAction.getDuration() + " ms")
        );
    }

    private ContainerTag getContentWithScreenshot(BrowserAction browserAction) {
        return div().with(
                img().withSrc(browserAction.getVideoIdThumb()).attr(DATA_TARGET, "#expand-" + modalCounter).attr(DATA_TOGGLE, MODAL).withAlt("Click to expand."),
                p().withClass(CARD_TEXT).with(rawHtml(browserAction.getStatusLine())),
                p().withClass(CARD_TEXT).with(rawHtml(browserAction.getStackTrace())),
                p().withClass(CARD_TEXT).withText(browserAction.getDuration() + " ms"),
                generateModal(browserAction, modalCounter++)
        );
    }

    private ContainerTag generateModal(BrowserAction browserAction, Integer integer) {
        if (browserAction.getSuccessStatus()) {
            return div().withClasses("modal fade").withId("expand-" + integer).attr("role", "dialog").with(
                    div().withClasses("modal-dialog").with(
                            div().withClasses("modal-content").with(
                                    div().withClasses("modal-header").with(
                                            h4().withClasses("modal-title about").withText("Screenshot").with(
                                                    img().withSrc("./images/greenball.jpg").withTitle("Step Passed")
                                            )
                                    ),
                                    div().withClasses("modal-body about smalltext").with(
                                            img().attr("width", "1280").withSrc(browserAction.getVideoId())
                                    ),
                                    div().withClasses("modal-footer").with(
                                            button().withType(BUTTON).withClasses("btn btn-default").attr("data-dismiss", MODAL).withText("Close")
                                    )
                            )
                    )
            );
        } else {
            return div().withClasses("modal fade").withId("expand-" + integer).attr("role", "dialog").with(
                    div().withClasses("modal-dialog").with(
                            div().withClasses("modal-content").with(
                                    div().withClasses("modal-header").with(
                                            h4().withClasses("modal-title about").withText("Screenshot").with(
                                                    img().withSrc("./images/redball.jpg").withTitle("Step Failed")
                                            )
                                    ),
                                    div().withClasses("modal-body about smalltext").with(
                                            img().attr("width", "1280").withSrc(browserAction.getVideoId())
                                    ),
                                    div().withClasses("modal-footer").with(
                                            button().withType(BUTTON).withClasses("btn btn-default").attr("data-dismiss", MODAL).withText("Close")
                                    )
                            )
                    )
            );
        }
    }

    private ContainerTag getReportMetaData() {
        return table().withClasses("table table-responsive topMargin tableAdjust").with(
                tr().withClass("table-primary").with(
                        th().withClass(CENTERED).attr("colspan", "2").withText("Test Details")
                ),
                tr().with(
                        td().withText("Type"),
                        getBrowserImage(browserMeta.getBrowserType())
                ),
                tr().with(
                        td().withText("Date"),
                        td().withText(browserMeta.getDate())
                ),
                tr().with(
                        td().withText("Description"),
                        td().withText(browserMeta.getDescription())
                ),
                tr().with(
                        td().withText("Invoking Class"),
                        td().withText(browserMeta.getInvokingClass())
                ),
                tr().with(
                        td().withText("Method Name"),
                        td().withText(browserMeta.getMethodName())
                ),
                tr().with(
                        td().withText("Simulated Mode"),
                        td().withText(browserMeta.getSimulator())
                ),
                tr().with(
                        td().withText("TR Case"),
                        td().withText(browserMeta.getReference())
                ),
                tr().with(
                        td().withText("URL"),
                        td().with(a(browserMeta.getUrl()).withHref(browserMeta.getUrl()).withTarget("_blank"))
                ),
                tr().with(
                        td().withText("Duration"),
                        td().withText(browserMeta.getDuration() + " second(s)")
                )
        );
    }

    private ContainerTag getBrowserImage(String browserType) {
        switch(browserType) {
            case "Chrome":
                return td().with(img().withSrc("./images/chrome3d_small.jpg").withTitle("Chrome"));
            case "Firefox":
                return td().with(img().withSrc("./images/firefox_small.jpg").withTitle("Firefox"));
            case "Safari":
                return td().with(img().withSrc("./images/safari_small.jpg").withTitle("Safari"));
            case "Opera":
                return td().with(img().withSrc("./images/opera_3d_small.jpg").withTitle("Opera"));
            default:
                return td();
        }
    }

    private ContainerTag generateReportHead() {
        return nav().withClasses("navbar navbar-expand-md navbar-dark bg-dark").with(
                a().withClass("navbar-brand").withText("Salesforce Seasonal Release Automation Test: " + browserMeta.getClassName()).withHref("#"),
                a().withClass("navbar-brand").withText("< Back to Overview").withHref("reportOverview.html"),
                h1().withClasses("navbar-brand centered").withText("Browser Report")
        );
    }

    private void numberOfObject() {
        logger.info("Size of report list is: [{}]", browserActionList.size());

        for (BrowserAction browserAction : browserActionList) {
            if (Boolean.TRUE.equals(browserAction.getSuccessStatus())) {
                pass++;
            } else {
                fail++;
            }
        }

        logger.info("Number of passed : [{}]", pass);
        logger.info("Number of failed : [{}]", fail);
    }
}
