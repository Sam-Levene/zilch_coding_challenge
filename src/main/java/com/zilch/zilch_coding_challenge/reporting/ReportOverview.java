package com.zilch.zilch_coding_challenge.reporting;

import com.zilch.zilch_coding_challenge.runner.BrowserMeta;
import com.zilch.zilch_coding_challenge.runner.SuiteMeta;
import com.zilch.zilch_coding_challenge.utils.Reference;
import com.zilch.zilch_coding_challenge.utils.ReferenceStatus;
import com.zilch.zilch_coding_challenge.utils.TestState;
import com.zilch.zilch_coding_challenge.utils.Utils;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

import static j2html.TagCreator.*;

public class ReportOverview {
    private static final Logger logger = LogManager.getLogger(ReportOverview.class);
    private static final String TABLE_BORDERED = "table-bordered";
    private static final String COL_MD_12 = "col-md-12";
    private static final String SECONDS = " second(s)";

    private final List<BrowserMeta> browserMetaList;
    private final SuiteMeta suiteMeta;

    private int pass = 0;
    private int fail = 0;
    private int executed = 0;
    private int ignored = 0;
    private int absent = 0;
    private int unknown = 0;

    public ReportOverview(SuiteMeta suiteMeta) {
        this.browserMetaList = suiteMeta.getBrowserMetaList();
        this.suiteMeta = suiteMeta;
    }

    public void run() {
        numberOfObjects();
        numberOfReferences();

        String htmlDocument = html(
                head(
                        title("Salesforce Seasonal Release Test Automation Report Overview"),
                        link().withRel("stylesheet").withHref("./bootstrap/css/salesforce.css"),
                        link().withRel("stylesheet").withHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"),
                        script().withSrc("https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"),
                        script().withSrc("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"),
                        script().withSrc("https://www.gstatic.com/charts/loader.js"),
                        drawPieChart()
                ),
                body(
                        generateReportHead(),
                        div().withClasses(".container-fluid topMargin").with(
                                h1().withClasses("centered").withText("Salesforce Seasonal Automation Report"),
                                div().withClasses("row").with(
                                        div().withClasses(COL_MD_12).with(
                                                getReportMetaData()
                                        )
                                ),
                                div().withClasses("float").with(
                                        div().withId("pieChartPlaceholder").withClasses("tableAdjust")
                                ),
                                div().withClasses("row topMargin").with(
                                        div().withClasses(COL_MD_12).with(
                                                getTestResults()
                                        )
                                ),
                                div().withClasses("float2").with(
                                        div().withId("pieChartPlaceholder2").withClasses("tableAdjust")
                                ),
                                div().withClasses("row topMargin").with(
                                        div().withClasses(COL_MD_12).with(
                                                getReferenceChecks()
                                        )
                                )
                        ),
                        script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js")
                )
        ).render();

        File file = new File(System.getProperty("user.dir") + "/report/" + "reportOverview.html");
        Utils.fileWriter(file, htmlDocument);
    }

    private ContainerTag getReferenceChecks() {
        return table().withClasses("table table-responsive tableWidth topMargin overviewTable").with(
                thead().withClasses(TABLE_BORDERED).with(
                        tr().withClass("table-secondary").with(
                                th().attr("colspan", "999").withText("Test Reference Status")
                        ),
                        tr().with(
                                TagCreator.each( suiteMeta.getReferenceMapAsList(), references ->
                                        th().with(
                                                span().withText(references.getTestCase()).withClasses("badge badge-success").withTitle(references.getDescription())
                                        )
                                )
                        )
                ),
                tbody().withClasses(TABLE_BORDERED).with(
                        tr().with(
                                TagCreator.each( suiteMeta.getReferenceMapAsList(), references ->
                                        checkStatus(references.getTestCase())
                                )
                        )
                )
        );
    }

    private ContainerTag checkStatus(String testCase) {
        if (suiteMeta.getReferenceStatus(testCase) == ReferenceStatus.ABSENT) {
            return td().with(img().withSrc("./images/cross.png").withTitle("Test is present in the References, but is absent from the tested scenarios"));
        } else if (suiteMeta.getReferenceStatus(testCase) == ReferenceStatus.IGNORED) {
            return td().with(img().withSrc("./images/ignored.png").withTitle("Test is present in References but has been ignored from the tested scenarios"));
        } else if (suiteMeta.getReferenceStatus(testCase) == ReferenceStatus.UNKNOWN) {
            return td().with(img().withSrc("./images/question.png").withTitle("Test is not present in References but has been included in the tested scenarios anyway"));
        } else if (suiteMeta.getReferenceStatus(testCase) == ReferenceStatus.EXECUTED) {
            return td().with(img().withSrc("./images/tick.png").withTitle("Test is present in References and has been executed with the tested scenarios"));
        } else {
            return td();
        }
    }

    private ContainerTag getTestResults() {
        return table().withClasses("table table-responsive table-bordered header-fixed tableWidth topMargin overviewTable").with(
                thead().with(
                        tr().with(
                                th().withText("Test Name"),
                                th().withText("Status"),
                                th().withText("Browser"),
                                th().withText("Duration"),
                                th().withText("Description"),
                                th().withText("Method Name"),
                                th().withText("Reference"),
                                th().withText("URL"),
                                th().withText("Total Steps"),
                                th().withText("Steps Passed"),
                                th().withText("Steps Failed"),
                                th().withText("Configuration FilePackage"),
                                th().withText("Group")
                        )
                ),
                tbody().with(
                        each(browserMetaList, list ->
                                tr().with(
                                        getTestName(list),
                                        getTestState(list.getTestState()),
                                        getBrowserImage(list.getBrowserType()),
                                        td().withText(list.getDuration() + SECONDS),
                                        td().withText(list.getDescription()),
                                        td().withText(list.getMethodName()),
                                        td().with(span().withText(list.getReference()).withClasses("badge badge-primary").withTitle("TC Reference: " + list.getDescription())),
                                        td().with(a().withTarget("_blank").with(img().withSrc("./images/earth2-2.jpg")).withHref(list.getUrl()).withTitle("URL Reference")),
                                        td().withText(list.getTotalSteps()),
                                        td().withText(list.getStepsPassed().toString()),
                                        td().withText(list.getStepsFailed().toString()),
                                        td().withText(list.getConfiguration()),
                                        getGroupNames(list)
                                )
                        )
                )
        );
    }

    private ContainerTag getGroupNames(BrowserMeta browserMeta) {
        return td().with(
                each(Utils.stringToList(browserMeta.getGroups()), group ->
                        span(
                                span().withText(group).withClasses("badge badge-info"),
                                rawHtml("&nbsp;")
                        ).withTitle("Group Names")
                )
        );
    }

    private ContainerTag getBrowserImage(String browserType) {
        switch(browserType) {
            case "Chrome":
                return td().with(img().withSrc("./images/chrome3d_small.jpg").withTitle("Chrome Browser"));
            case "Firefox":
                return td().with(img().withSrc("./images/firefox_small.jpg").withTitle("Firefox Browser"));
            case "Opera":
                return td().with(img().withSrc("./images/opera_3d_small.jpg").withTitle("Opera Browser"));
            case "Safari":
                return td().with(img().withSrc("./images/safari_small.jpg").withTitle("Safari Browser"));
            default:
                return td();
        }
    }

    private ContainerTag getTestState(TestState testState) {

        switch(testState) {
            case FAIL:
                return td().with(img().withSrc("./images/redball.jpg").withTitle("Test Failed"));
            case IGNORE:
                return td().with(img().withSrc("./images/greyball.jpg").withTitle("Test Ignored"));
            case SUCCESS:
                return td().with(img().withSrc("./images/greenball.jpg").withTitle("Test Passed"));
            default:
                return td();
        }
    }

    private ContainerTag getTestName(BrowserMeta list) {
        if (list.getTestState().equals(TestState.IGNORE)) {
            return td().withText(list.getClassName());
        } else {
            return td().with(a().withText(list.getClassName()).withHref(list.getClassName() + "-" + list.getProperties().getProperty("browserType") + "-" + list.getMethodName() + "-" + list.getSimulator().replace(" ", "_") + ".html"));
        }
    }

    private ContainerTag getReportMetaData() {
        return table().withClasses("table table-responsive tableWidth topMargin tableAdjust").with(
                thead().withClasses(TABLE_BORDERED).with(
                        tr().withClass("table-secondary").with(
                                th().attr("colspan", "4").withText("Test Suite Data")
                        ),
                        tr().with(
                                th().withText("Time Test Suite Started"),
                                th().withText("Time Test Suite Ended"),
                                th().withText("Total Time Taken")
                        )
                ),
                tbody().withClasses(TABLE_BORDERED).with(
                        tr().with(
                                td().withText(suiteMeta.getStartTime()),
                                td().withText(suiteMeta.getEndTime()),
                                td().withText(splitTime(suiteMeta.getTimeTaken()))
                        )
                )
        );
    }

    private String splitTime(String timeTaken) {
        if (Integer.parseInt(timeTaken) > 90) {
            double timeInMinutes = Math.round(Integer.parseInt(timeTaken) / 60.0);
            int remainder = Integer.parseInt(timeTaken) % 60;

            return (timeInMinutes) + " minute(s), " + remainder + SECONDS;
        } else {
            return timeTaken + SECONDS;
        }
    }

    private ContainerTag generateReportHead() {
        return nav().withClasses("navbar navbar-expand-md navbar-dark bg-dark").with(
                a().withClass("navbar-brand").withText("Salesforce Seasonal Release Automation Report").withHref("#")
        );
    }

    private ContainerTag drawPieChart() {
        return script().withType("text/javascript").with(
                rawHtml(
                        "google.charts.load('current', {'packages':['corechart']}); \n" +
                                "google.charts.setOnLoadCallback(drawChart1); \n" +
                                "google.charts.setOnLoadCallback(drawChart2); \n" +
                                "function drawChart1() { var data = google.visualization.arrayToDataTable([\n" +
                                "['Task', 'Hours per Day']," +
                                "['Success', "+pass+"]," +
                                "['Fail', "+fail+"]," +
                                "]); \n" +

                                "var options = {'title':'Salesforce Seasonal Release Automation Test State', 'width':550, 'height':400, 'is3D':true, 'colors': ['#009900', '#990000']};\n" +

                                "var chart = new google.visualization.PieChart(document.getElementById('pieChartPlaceholder'));\n" +
                                "chart.draw(data, options);}\n" +

                                "function drawChart2() { var data = google.visualization.arrayToDataTable([\n" +
                                "['Task', 'Hours per Day']," +
                                "['Executed', "+executed+"]," +
                                "['Absent', "+absent+"]," +
                                "['Ignored', "+ignored+"]," +
                                "['Unknown', "+unknown+"]," +
                                "]); \n" +
                                "var options = {'title':'Reference Overview', 'width':550, 'height':400, 'is3D':true, 'colors': ['#009900', '#990000', '#999900', '#999999']};\n" +

                                "var chart = new google.visualization.PieChart(document.getElementById('pieChartPlaceholder2'));\n" +
                                "chart.draw(data, options);}"
                )
        );
    }

    private void numberOfReferences() {
        logger.info("Size of reference list is: [{}]", suiteMeta.getReferenceMapAsList().size());

        for (Reference reference : suiteMeta.getReferenceMapAsList()) {
            if (suiteMeta.getReferenceStatus(reference.getTestCase()).equals(ReferenceStatus.UNKNOWN)) {
                unknown++;
            } else if (suiteMeta.getReferenceStatus(reference.getTestCase()).equals(ReferenceStatus.IGNORED)) {
                ignored++;
            } else if (suiteMeta.getReferenceStatus(reference.getTestCase()).equals(ReferenceStatus.EXECUTED)) {
                executed++;
            } else if (suiteMeta.getReferenceStatus(reference.getTestCase()).equals(ReferenceStatus.ABSENT)) {
                absent++;
            }
        }

        logger.info("Number of executed tests: [{}]", executed);
        logger.info("Number of unknown tests: [{}]", unknown);
        logger.info("Number of ignored tests: [{}]", ignored);
        logger.info("Number of absent tests: [{}]", absent);
    }

    private void numberOfObjects() {
        logger.info("Size of report list is: [{}]", browserMetaList.size());

        for (BrowserMeta browserMeta : browserMetaList) {
            if (browserMeta.getTestState() == TestState.SUCCESS) {
                pass++;
            } else if (browserMeta.getTestState() == TestState.FAIL) {
                fail++;
            }
        }

        logger.info("Number of passed tests: [{}]", pass);
        logger.info("Number of failed tests: [{}]", fail);
    }
}
