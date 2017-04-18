package com.cloudbees.jenkins.plugins.testreportgenerator;

import hudson.model.Fingerprint;
import hudson.model.Fingerprint.RangeSet;
import hudson.model.FingerprintMap;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Run.Artifact;
import jenkins.model.Jenkins;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

//processing numbers from TestResultsAction
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.junit.TestResult;
import hudson.model.Action;
import hudson.model.TaskListener;

// conversion into html
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.TraxSource;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

public class TestReportParser {
    private String jenkinsUrl = "";
    private ReleaseTestResults rtr = new ReleaseTestResults();
    
    public TestReportParser(String fingerprint, String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
        rtr = new ReleaseTestResults();
        createCumulativeTestReport(fingerprint);
        rtr.setAsJson(getReportJSON());
    }

    public TestReportParser(String fingerprint, String jenkinsUrl, String defaultTestSuites) {
        this.jenkinsUrl = jenkinsUrl;
        rtr = new ReleaseTestResults(defaultTestSuites);
        createCumulativeTestReport(fingerprint);
        rtr.setAsJson(getReportJSON());
    }

    /**
     * Generates the given report in XML (XStream-specific) format.
     */
    public String getReportXML() {
        XStream xs = new XStream();
        String xmlObj = xs.toXML(rtr);
        return xmlObj;
    }

    /**
     * Generates the given report in JSON format.
     */
    public String getReportJSON() {
        if(rtr.getAsJson().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json =  mapper.writeValueAsString(rtr);
                rtr.setAsJson(json);
                return json;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else  {
            return rtr.getAsJson();
        }
    }

    /**
     * Generates the given report in HTML format.
     */
    public String getReportHTML() {
        XStream xs = new XStream();
        TraxSource traxSource = new TraxSource(rtr, xs);
        Writer buffer = new StringWriter();
        try {
            InputStream in = getClass().getResourceAsStream("/release-test-results.xsl");
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(in));
            transformer.transform(traxSource, new StreamResult(buffer));
            return ((StringWriter)buffer).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a report for posting to JIRA.  Looks slightly different than 
     * other reports since we have the benefit of linking directly to the
     * test results.
     */
    public String getReportJIRA() {
        // String looks like: [link title|http://example.com]
        return "Automated test suites\n\n" + rtr.jiraSummary();
    }
    

    /**
     * Populate the test results for ease of reporting later.
     */
    private void createCumulativeTestReport(String fingerprint) {

        Jenkins j = Jenkins.getInstance();
        try {
            Fingerprint fp = j._getFingerprint(fingerprint);
            Hashtable<String, RangeSet> allJobs = fp.getUsages();
            
            // Skip massive TestResultAction due to compiler situation.
            for (String jobname : allJobs.keySet()) {
                String mostRecent = allJobs.get(jobname).listNumbersReverse().iterator().next().toString();
                Job job = (Job)j.getItem(jobname);
                Run build = job.getBuild(mostRecent);

                TestResultAction tra = build.getAction(TestResultAction.class);
                if(tra != null) { 
                    // Calculate link location - follows the standard Jenkins conventions
                    TestResult testResult = tra.getResult();
                    rtr.addSuite(jobname, mostRecent, build.getResult().toString(), build.getAbsoluteUrl(), tra);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }   

}
