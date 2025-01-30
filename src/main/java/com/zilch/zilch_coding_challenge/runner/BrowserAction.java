package com.zilch.zilch_coding_challenge.runner;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BrowserAction {
    private Long startTime;
    private Long endTime;
    private String methodName;
    private boolean successStatus = true;
    private boolean videoTaken = false;
    private String videoId;
    private String videoIdThumb;
    private String statusLine;
    private Integer actionId;
    private String actionInfo;
    private static Integer actionIdCounter = 0;
    private String stackTrace;
    private static final Logger logger = LogManager.getLogger(BrowserAction.class);
    private static final String END_ITALIC_PARAGRAPH = "</i></p>";

    BrowserAction() {
        startTime = System.currentTimeMillis();
        methodName = setMethodName();
        stackTrace = setStackTrace();
        this.statusLine = "Success";
        this.actionId = actionIdCounter++;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean getSuccessStatus() {
        return successStatus;
    }

    public String getActionInfo() {
        return actionInfo;
    }

    public void setActionInfo(String actionInfo) {
        this.actionInfo = actionInfo;
    }

    public void setSuccessStatus(boolean successStatus) {
        this.successStatus = successStatus;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoIdThumb() {
        return videoIdThumb;
    }

    public void setVideoIdThumb(String videoIdThumb) {
        this.videoIdThumb = videoIdThumb;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public boolean getVideoTaken() {
        return videoTaken;
    }

    public void setVideoTaken(boolean videoTaken) {
        this.videoTaken = videoTaken;
    }

    public String getDuration() {
        long duration = this.endTime - this.startTime;
        return Long.toString(duration);
    }

    public void close() {
        this.endTime = System.currentTimeMillis();
    }

    public Integer getActionId() {
        return actionId;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void resetMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String setStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement1 = stackTraceElements[3];
        StackTraceElement stackTraceElement2 = stackTraceElements[4];

        this.stackTrace = "<p>File Package : <i>" + stackTraceElement1.getFileName() + END_ITALIC_PARAGRAPH +
                "<p>Class Name : <i>" + stackTraceElement1.getClassName() + END_ITALIC_PARAGRAPH +
                "<p>Method Name : <i>" + stackTraceElement1.getMethodName() + END_ITALIC_PARAGRAPH +
                "<p>Line Number : <b><font color=\"blue\"" + stackTraceElement1.getLineNumber() + "</font></b></p>" +
                "<p>File Package : <i>" + stackTraceElement2.getFileName() + END_ITALIC_PARAGRAPH +
                "<p>Class Name : <i>" + stackTraceElement2.getClassName() + END_ITALIC_PARAGRAPH +
                "<p>Method Name : <i>" + stackTraceElement2.getMethodName() + END_ITALIC_PARAGRAPH +
                "<p>Line Number : <b><font color=\"blue\"" + stackTraceElement2.getLineNumber() + "</font></b></p>";
        return this.stackTrace;
    }

    public String setMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[3];
        logger.info("Method name is: " + stackTraceElement.getMethodName());
        methodName = stackTraceElement.getMethodName();
        return this.methodName;
    }

    public void takeVideo(WebDriver driver) {
        String dateTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String screenshotFullSize = System.getProperty("user.dir") + "/report/screen/" + dateTime + ".png";
        String screenshotThumbnail = System.getProperty("user.dir") + "/report/screen/" + dateTime + "-t.png";

        TakesScreenshot ts = (TakesScreenshot)driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(source, new File(screenshotFullSize));
            BufferedImage bufferedImage = new BufferedImage(200,320,BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics().drawImage(ImageIO.read(new File(screenshotFullSize))
                    .getScaledInstance(200,320, Image.SCALE_SMOOTH),0,0,null);
            ImageIO.write(bufferedImage, "png", new File(screenshotThumbnail));

            String subStr1 = "/report";
            int screenShotFullLen = screenshotFullSize.length();
            int screenShotThumbLen = screenshotThumbnail.length();
            String modifyFile = "..";

            String newScreenshotFullSizePath = (String)screenshotFullSize.subSequence(screenshotFullSize.indexOf(subStr1), screenShotFullLen);
            String newScreenshotThumbnailPath = (String)screenshotThumbnail.subSequence(screenshotThumbnail.indexOf(subStr1), screenShotThumbLen);

            screenshotFullSize = modifyFile + newScreenshotFullSizePath;
            screenshotThumbnail = modifyFile + newScreenshotThumbnailPath;

            setVideoId(screenshotFullSize);
            setVideoIdThumb(screenshotThumbnail);
            setVideoTaken(true);
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }
}
