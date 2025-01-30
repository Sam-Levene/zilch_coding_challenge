package com.zilch.zilch_coding_challenge.runner;

import org.openqa.selenium.By;

public interface BrowserActionsInterface {

    BrowserActionsInterface origin();

    BrowserActionsInterface focus(By by) throws BrowserException;

    BrowserActionsInterface scrollToElement(By by) throws BrowserException;

    BrowserActionsInterface ascend() throws BrowserException;

    BrowserActionsInterface descend() throws  BrowserException;

    BrowserActionsInterface descend(By by) throws BrowserException;

    BrowserActionsInterface collect(By by) throws BrowserException;

    BrowserActionsInterface count(Integer size) throws BrowserException;

    BrowserActionsInterface depart(By by) throws BrowserException;

    BrowserActionsInterface touch() throws BrowserException;

    BrowserActionsInterface compose(String keysToSend) throws BrowserException;

    BrowserActionsInterface enabled() throws BrowserException;

    BrowserActionsInterface disabled() throws BrowserException;

    BrowserActionsInterface selected() throws BrowserException;

    BrowserActionsInterface matches() throws BrowserException;

    BrowserActionsInterface matches(String content) throws BrowserException;

    BrowserActionsInterface contains(String content) throws BrowserException;

    BrowserActionsInterface notContains(String content) throws BrowserException;

    BrowserActionsInterface repeat();

    BrowserActionsInterface endRepeat();

    BrowserActionsInterface present(String text) throws BrowserException;

    BrowserActionsInterface absent(String text) throws BrowserException;

    BrowserActionsInterface select(String text) throws BrowserException;

    BrowserActionsInterface select(Integer index) throws BrowserException;

    BrowserActionsInterface range(Integer size) throws BrowserException;

    BrowserActionsInterface range() throws BrowserException;

    BrowserActionsInterface store() throws BrowserException;

    BrowserActionsInterface store(String key) throws BrowserException;

    BrowserActionsInterface probe(By by) throws BrowserException;

    BrowserActionsInterface endProbe();

    BrowserActionsInterface retrieve() throws BrowserException;

    BrowserActionsInterface retrieve(String key) throws BrowserException;

    BrowserActionsInterface compare() throws BrowserException;

    BrowserActionsInterface clear() throws BrowserException;

    BrowserActionsInterface pause(Integer seconds);

    BrowserActionsInterface capture();

    BrowserActionsInterface perform(RunnableItem runnableItem) throws BrowserException;

    BrowserActionsInterface changeFrames(String frameNumber) throws BrowserException;
}
