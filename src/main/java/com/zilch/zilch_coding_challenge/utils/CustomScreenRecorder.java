package com.zilch.zilch_coding_challenge.utils;

import org.monte.media.*;
import org.monte.media.math.*;
import org.monte.screenrecorder.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class CustomScreenRecorder extends ScreenRecorder {
    private String fileName;
    private File currentFile;

    public CustomScreenRecorder(File movieFolder) throws IOException, AWTException {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(),
                new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height),
                new Format(
                        MediaTypeKey, FormatKeys.MediaType.FILE,
                        MimeTypeKey, MIME_AVI),
                new Format(
                        MediaTypeKey, FormatKeys.MediaType.VIDEO,
                        EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24,
                        FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                new Format(
                        MediaTypeKey, MediaType.VIDEO,
                        EncodingKey, "black",
                        FrameRateKey, Rational.valueOf(30)),
                null,
                movieFolder);
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }

        currentFile = getFileWithUniqueName(movieFolder.getAbsolutePath() + File.separator + fileName + "." + Registry.getInstance().getExtension(fileFormat));
        return currentFile;
    }

    private File getFileWithUniqueName(String fileName) {
        String extension = "";
        String name = "";

        int idxOfDot = fileName.lastIndexOf('.'); // Get the last index of . to separate extension
        extension = fileName.substring(idxOfDot + 1);
        name = fileName.substring(0, idxOfDot);

        Path path = Paths.get(fileName);
        int counter = 1;
        while(Files.exists(path)){
            fileName = name + "-" + counter + "." + extension;
            path = Paths.get(fileName);
            counter++;
        }
        return new File(fileName);
    }

    public void startRecording(String fileName) throws IOException {
        this.fileName = fileName;
        start();
    }

    public void stopRecording(boolean keepFile) throws IOException {
        stop();
        if (!keepFile) {
            deleteRecording();
        }
    }

    private void deleteRecording() {
        boolean deleted = false;
        try{
            if (currentFile.exists()) {
                deleted = currentFile.delete();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (deleted)
            currentFile = null;
        else
            System.out.println("Could not delete the screen-record!");
    }

}
