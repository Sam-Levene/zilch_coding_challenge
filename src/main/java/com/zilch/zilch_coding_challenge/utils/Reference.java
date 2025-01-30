package com.zilch.zilch_coding_challenge.utils;

public class Reference implements Comparable<Reference> {

    private final String testCase;
    private final String description;

    public Reference(String testCase, String description) {
        this.testCase = testCase;
        this.description = description;
    }

    public String getTestCase() {
        return testCase;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Reference reference) {
        String[] parameterPart = reference.getTestCase().split("(?<=\\D)(?<=\\D)");
        String[] referencePart = this.testCase.split("(?<=\\D)(?<=\\D)");
        return Integer.parseInt(referencePart[3].replace(",", "")) - Integer.parseInt(parameterPart[3].replace(",", ""));
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
