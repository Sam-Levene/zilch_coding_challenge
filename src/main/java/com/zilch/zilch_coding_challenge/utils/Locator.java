package com.zilch.zilch_coding_challenge.utils;

import org.openqa.selenium.By;

public class Locator {

    private Locator() {
        // Not Used.
    }

    public static By className(String className) {
        return By.className(className);
    }

    public static By id(String id) {
        return By.id(id);
    }

    public static By tag(String tag) {
        return  By.tagName(tag);
    }

    public static By name(String name) {
        return By.name(name);
    }

    public static By linkText(String linkText) {
        return By.linkText(linkText);
    }

    public static By css(String css) {
        return By.cssSelector(css);
    }

    public static By xpath(String xpath) {
        return By.xpath(xpath);
    }
}
