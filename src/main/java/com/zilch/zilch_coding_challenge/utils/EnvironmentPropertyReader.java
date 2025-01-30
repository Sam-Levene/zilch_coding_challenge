package com.zilch.zilch_coding_challenge.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

public class EnvironmentPropertyReader {
    private Properties properties = new Properties();
    private static final Logger logger = LogManager.getLogger(EnvironmentPropertyReader.class);

    public EnvironmentPropertyReader(String fileName) {
        try(FileInputStream fileInputStream = new FileInputStream(fileName + ".properties")) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
