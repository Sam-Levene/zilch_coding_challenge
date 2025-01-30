package com.zilch.zilch_coding_challenge.utils;

import java.util.*;

public class FileProperties {

    private FileProperties() {
        // Not Used.
    }

    public static List<Map<String, String>> readPropertiesAsList(Properties properties) {
        List<Map<String, String>> propertyList = new ArrayList<>();
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            Map<String, String> pair = new HashMap<>();
            pair.put(key, properties.getProperty(key));
            propertyList.add(pair);
        }
        return propertyList;
    }
}
