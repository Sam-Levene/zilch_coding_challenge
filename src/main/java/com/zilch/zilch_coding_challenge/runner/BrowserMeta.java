package com.zilch.zilch_coding_challenge.runner;

import com.zilch.zilch_coding_challenge.utils.TestState;
import java.util.*;

public class BrowserMeta {
    private List<Map<String,String>> propertiesList;
    private String configuration;
    private String reference;
    private String description;
    private String url;
    private String methodName;
    private String simulator;
    private String groups;
    private String invokingClass;
    private Integer stepsPassed;
    private Integer stepsFailed;
    private Properties properties;
    private TestState testState;
    private Calendar date;
    private long startTime;
    private long endTime;

    private BrowserMeta(Builder builder) {
        this.configuration = builder.configuration;
        this.reference = builder.reference;
        this.description = builder.description;
        this.url = builder.url;
        this.methodName = builder.methodName;
        this.simulator = builder.simulator;
        this.groups = builder.groups;
        this.invokingClass = builder.invokingClass;
        this.properties = builder.properties;
        this.propertiesList = builder.propertiesList;
        stepsPassed = 0;
        stepsFailed = 0;
        date = Calendar.getInstance();
        testState = TestState.SUCCESS;
        startTime = System.currentTimeMillis();

    }

    public String getClassName() {
        int length = invokingClass.length();
        int classIndex = invokingClass.lastIndexOf('.');
        return invokingClass.substring(classIndex + 1, length);
    }

    public String getDuration() {
        long duration = (this.endTime - this.startTime)/1000;
        return Long.toString(duration);
    }

    public String getDate() {
        return this.date.getTime().toString();
    }

    public String getBrowserType() {
        return properties.getProperty("browserType");
    }

    public void close() {
        this.endTime = System.currentTimeMillis();
    }

    public void setStepsPassed(int passedSteps) {
        this.stepsPassed = passedSteps;
    }

    public void setStepsFailed(int failedSteps) {
        this.stepsFailed = failedSteps;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Map<String,String>> getPropertiesList() {
        return propertiesList;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getReference() {
        return reference;
    }

    public String getUrl() {
        return url;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSimulator() {
        return simulator;
    }

    public String getGroups() {
        return groups;
    }

    public String getInvokingClass() {
        return invokingClass;
    }

    public Integer getStepsPassed() {
        return stepsPassed;
    }

    public Integer getStepsFailed() {
        return stepsFailed;
    }

    public TestState getTestState() {
        return testState;
    }

    public void setTestState(TestState testState) {
        this.testState = testState;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalSteps() {
        return Integer.toString((stepsFailed + stepsPassed));
    }

    public static class Builder {
        private String configuration;
        private String reference;
        private String description;
        private String url;
        private String methodName;
        private String simulator;
        private String groups;
        private String invokingClass;
        private Properties properties;
        private List<Map<String,String>> propertiesList;

        public Builder withConfiguration(String configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Builder withSimulator(String simulator) {
            this.simulator = simulator;
            return this;
        }

        public Builder withGroups(String groups) {
            this.groups = groups;
            return this;
        }

        public Builder withInvokingClass(String invokingClass) {
            this.invokingClass = invokingClass;
            return this;
        }

        public Builder withProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder withPropertiesList(List<Map<String, String>> propertiesList) {
            this.propertiesList = propertiesList;
            return this;
        }

        public BrowserMeta build() {
            return new BrowserMeta(this);
        }
    }
}
