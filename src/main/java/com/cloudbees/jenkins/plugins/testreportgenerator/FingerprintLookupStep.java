package com.cloudbees.jenkins.plugins.testreportgenerator;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.util.Collections;
import java.util.Set;


public class FingerprintLookupStep extends Step {

    // The fingerprint that is the focus of the different reports
    public final String fingerprint;

    // If report result should be published in XML
    @DataBoundSetter
    public boolean publishXML;

    // If report result should be published in JSON 
    @DataBoundSetter
    public boolean publishJSON;

    // If report result should be published in plain text for JIRA
    @DataBoundSetter
    public boolean publishForJIRA;

    @DataBoundConstructor
    public FingerprintLookupStep(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /*public String getFingerprint() {
        return fingerprint;
    }*/

    @Override public StepExecution start(StepContext context) throws Exception {
        return new FingerprintLookupExecution(this, context);
    }


    @Extension(optional = true)
    public static class DescriptorImpl extends StepDescriptor  {

        @Override
        public String getFunctionName() {
            return "testReportForFingerprint";
        }

        @Override
        public String getDisplayName() {
            //return Messages.MockLoadBuilder_DisplayName();
            return "Create a comprehensive test report for the file associated with the given fingerprint";
        }

        @Override public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(FilePath.class);
        }
    }


    public static final class FingerprintLookupExecution extends SynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 1L;

        private transient FingerprintLookupStep step;

        FingerprintLookupExecution(FingerprintLookupStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            getContext().get(TaskListener.class).getLogger().println("Search for the fingerprint " + step.fingerprint);
            String jenkinsUrl = getContext().get(EnvVars.class).get("JENKINS_URL");
            String defaultTestSuites = getContext().get(EnvVars.class).get("DEFAULT_TEST_SUITES"); //comma separated
            TestReportParser parser;
            if(defaultTestSuites != null) {
                parser = new TestReportParser(step.fingerprint, jenkinsUrl, defaultTestSuites);
            } else {
                parser = new TestReportParser(step.fingerprint, jenkinsUrl);
            }
            
            
            // Publish Reports
            String totalHTMLReport = parser.getReportHTML();
            String filename = "index.html"; //TODO: make a param?
            getContext().get(FilePath.class).child(filename).write(totalHTMLReport, null);
            getContext().get(TaskListener.class).getLogger().println("HTML Report written to: "+filename);

            if(step.publishXML) {
                String totalXMLReport = parser.getReportXML();
                String xmlname = "results.xml"; //TODO: make a param?
                getContext().get(FilePath.class).child(xmlname).write(totalXMLReport, null);
                getContext().get(TaskListener.class).getLogger().println("XML Report written to: "+xmlname);
            }

            if(step.publishJSON) {
                String totalJSONReport = parser.getReportJSON();
                String jsonname = "results.json"; //TODO: make a param?
                getContext().get(FilePath.class).child(jsonname).write(totalJSONReport, null);
                getContext().get(TaskListener.class).getLogger().println("JSON Report written to: "+jsonname);
            }

            if(step.publishForJIRA) {
                String totalJIRAReport = parser.getReportJIRA();
                String jiraname = "results.txt"; //TODO: make a param?
                getContext().get(FilePath.class).child(jiraname).write(totalJIRAReport, null);
                getContext().get(TaskListener.class).getLogger().println("JIRA Report written to: "+jiraname);
            }

            // Finished
            getContext().get(TaskListener.class).getLogger().println("Reporting complete!");
            return null;
        }
    }
}