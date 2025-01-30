package com.zilch.zilch_coding_challenge.runner;

import com.zilch.zilch_coding_challenge.utils.ReferenceStatus;
import com.zilch.zilch_coding_challenge.utils.Reference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class SuiteMeta {
    private static final Logger logger = LogManager.getLogger(SuiteMeta.class);
    private static final String NOT_DEFINED = "Not Defined";

    private Map<Reference, ReferenceStatus> referenceMap;
    private List<BrowserMeta> browserMetaList;
    private final List<String> groups;
    private final Long startTime;
    private Long endTime;

    private SuiteMeta(Builder builder) {
        browserMetaList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        groups = builder.groups;
        referenceMap = readReferences(System.getProperty("user.dir") + "/scenario.references");
    }

    private Map<Reference, ReferenceStatus> readReferences(String fileLocation) {
        Map<Reference, ReferenceStatus> localReferenceMap = new TreeMap<>();
        List<Reference> referenceList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileLocation)))) {
            String localString;
            StringTokenizer stringTokenizer;

            while ((localString = bufferedReader.readLine()) != null) {
                stringTokenizer = new StringTokenizer(localString, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    referenceList.add(new Reference(stringTokenizer.nextToken(), stringTokenizer.nextToken()));
                }
            }
            Collections.sort(referenceList);

            for (Reference reference : referenceList) {
                localReferenceMap.put(reference, ReferenceStatus.PENDING);
            }
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }

        return localReferenceMap;
    }

    public static class Builder {
        private List<String> groups;

        public Builder() {
            groups = new ArrayList<>();
        }

        public Builder withGroups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public SuiteMeta build() {
            return new SuiteMeta(this);
        }
    }

    public void close() {
        endTime = System.currentTimeMillis();
        referenceMap.forEach((key, value) -> {
            if (value == ReferenceStatus.PENDING) {
                referenceMap.replace(key, value, ReferenceStatus.ABSENT);
            }
        });
    }

    public void addBrowserMeta (BrowserMeta browserMeta) {
        browserMetaList.add(browserMeta);
    }

    public List<BrowserMeta> getBrowserMetaList() {
        return browserMetaList;
    }

    public String getTimeTaken() {
        return "" + ((endTime - startTime) / 1000);
    }

    public String getStartTime() {
        return "" + new Date(startTime);
    }

    public String getEndTime() {
        return "" + new Date(endTime);
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean isRunnable(List<String> testGroups) {
        boolean runnable = false;

        if (!groups.isEmpty()) {
            for (String queriedGroups : groups) {
                for (String testGroup : testGroups) {
                    if (queriedGroups.equalsIgnoreCase(testGroup)) {
                        runnable = true;
                        break;
                    }
                }
                if (runnable) {
                    break;
                }
            }
        } else {
            runnable = true;
        }
        return runnable;
    }

    public void registerMissingReference(String key) {
       Reference reference = new Reference(key, "This com.ppl salesforce test automation script was not included in the references file. Consider adding it?");
       referenceMap.put(reference, ReferenceStatus.UNKNOWN);
    }

    public boolean containsReference(String referenceKey) {
        boolean checker = false;
        for (Reference references : referenceMap.keySet()) {
            if (references.getTestCase().equals(referenceKey)) {
                checker = true;
                break;
            }
        }
        return checker;
    }

    public void updateReferenceMap(String key, ReferenceStatus referenceStatus) {
        for (Reference references : referenceMap.keySet()) {
            if (references.getTestCase().equals(key)) {
                referenceMap.replace(references, ReferenceStatus.PENDING, referenceStatus);
                break;
            }
        }
    }

    public Set<Reference> getReferenceMapAsList() {
        return referenceMap.keySet();
    }

    public ReferenceStatus getReferenceStatus(String key) {
        ReferenceStatus referenceStatus = ReferenceStatus.PENDING;

        for (Map.Entry<Reference, ReferenceStatus> reference : referenceMap.entrySet()) {
            if (!reference.getKey().getTestCase().equals(key)) {
                referenceStatus = ReferenceStatus.UNKNOWN;
            } else {
                referenceStatus = referenceMap.get(reference.getKey());
                break;
            }
        }

        return referenceStatus;
    }
}
