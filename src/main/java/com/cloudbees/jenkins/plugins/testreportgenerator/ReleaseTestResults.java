package com.cloudbees.jenkins.plugins.testreportgenerator;

import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.junit.TestResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Summary of test results of the jobs that are associated with 
 * a given fingerprint.
 *
 */
public class ReleaseTestResults implements Serializable {
    private int percentPassed = 0;
    private List<TestSuiteSummary> testSuites = new ArrayList<TestSuiteSummary>();
    private Map<String, List<String>> failedTests = new HashMap<String, List<String>>();
    private String asJson = "";

    public ReleaseTestResults() {}

    public ReleaseTestResults(String defaultTestSuites) {
        String[] splitSuites = defaultTestSuites.trim().split(",");
        for(int x=0; x < splitSuites.length; x++) {
            testSuites.add(new TestSuiteSummary(splitSuites[x]));
        }
    }

    public void addSuite(String s, String bn, String st, String l, TestResultAction tra) {
        TestResult testResult = tra.getResult();
        TestSuiteSummary suiteSummary = findOrNewSummary(s);
        suiteSummary.update(bn, st, testResult.getTotalCount(), testResult.getPassCount(), testResult.getFailCount(), testResult.getSkipCount(), l);
        testSuites.add(suiteSummary);
        percentPassed = calculatePercentPassed();
        
        failedTests.put(s, convertToNames(testResult.getFailedTests()));
    }

    private TestSuiteSummary findOrNewSummary(String s) {
        for(TestSuiteSummary tss :  testSuites) {
            if(tss.suite.equals(s)) {
                return tss;
            }
        }
        return new TestSuiteSummary(s);
    }

    private int calculatePercentPassed() {
        float total = 0;
        int passed = 0;
        for(TestSuiteSummary ts : testSuites) {
            total += ts.total;
            passed += ts.passed;
        }
        return Math.round(((passed/total) * 100));
    }

    private List<String> convertToNames(List<CaseResult> failingTests) {
        List<String> failingTestNames = new ArrayList<String>();
        for(CaseResult cr : failingTests) {
            failingTestNames.add(cr.getFullDisplayName());
        }
        return failingTestNames;
    }

    public boolean hasTrackedResults() {
        return (testSuites.size() > 0);
    }

    public void setAsJson(String json) {
        asJson = json;
    }

    /**
     * Quick overview of the included test summaries including links.
     */
    public String jiraSummary() {
        String total = "";
        for(TestSuiteSummary tss : testSuites) {
            total += "* " + tss.suite + ": " + tss.status + " [#" + tss.buildNum + "|" + tss.link + "]\n";
        }
        return total;
    }


    /***********************************************************************/
    public int getPercentPassed() {
        return percentPassed;
    }
    public List<TestSuiteSummary> getTestSuites() {
        return testSuites;
    }
    public Map<String, List<String>> getFailedTests() {
        return failedTests;
    }
    public String getAsJson() {
        return asJson;
    }
    /***********************************************************************/

    
    /**
     * Holds summary information from a test suite.
     */
    public class TestSuiteSummary implements Serializable {
        public String suite;
        public String buildNum;
        public String status;
        public int total;
        public int passed;
        public int failed;
        public int skipped;
        public String link;

        public TestSuiteSummary(String s) {
            suite = s;
            buildNum = "";
            status = "";
            total = 0;
            passed = 0;
            failed = 0;
            skipped = 0;
            link = "";
        }

        public TestSuiteSummary(String s, String bn, String st, int t, int p, int f, int sk, String l) {
            suite = s;
            buildNum = bn;
            status = st;
            total = t;
            passed = p;
            failed = f;
            skipped = sk;
            link = l;
        }

        public void update(String bn, String st, int t, int p, int f, int sk, String l) {
            buildNum = bn;
            status = st;
            total = t;
            passed = p;
            failed = f;
            skipped = sk;
            link = l;
        }
    }
}