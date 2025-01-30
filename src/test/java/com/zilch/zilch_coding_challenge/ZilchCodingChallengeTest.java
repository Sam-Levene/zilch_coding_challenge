package com.zilch.zilch_coding_challenge;

import io.cucumber.junit.*;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features={"src/test/resources/features"},glue={"classpath:com.zilch.zilch_coding_challenge.step_definitions"},plugin={"json:target/cuke-results.json"})
public class ZilchCodingChallengeTest {
}