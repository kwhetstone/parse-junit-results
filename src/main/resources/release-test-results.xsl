 <!-- Attempt at xslt for my report -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" omit-xml-declaration="no" indent="yes"/>

    <xsl:variable name="testedJenkinsCoordinates" select="/report/testedCoreCoordinates/coord[groupId='org.jenkins-ci.plugins']" />
    <xsl:variable name="testedHudsonCoordinates" select="/report/testedCoreCoordinates/coord[groupId='org.jvnet.hudson.plugins']" />
    <xsl:variable name="otherCoreCoordinates" select="/report/testedCoreCoordinates/coord[not(groupId='org.jenkins-ci.plugins') and not(groupId='org.jvnet.hudson.plugins')]" />

    <xsl:template match="/">
<html>
    <head>
        <title>Plugin compat tester report</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous"/>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous"/>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
        <script type="text/javascript">
var latestDialog = null;
      </script>

        <style type="text/css">
th.pluginname{
    width: 200px;
}
th.version {
    width: 50px;
}
        </style>
    </head>
    <body>
        <!--<script src="js/bootstrap.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>-->
        <h1>Test Summary for Fingerprint</h1>
        <h3>Success Rate</h3>
        <p><xsl:value-of select="/com.cloudbees.jenkins.plugins.testreportgenerator.ReleaseTestResults/percentPassed"/>%</p>
        <table class="table">
            <tr>
              <th>Suite</th>
              <th>Passed</th>
              <th>Failed</th>
              <th>Skipped</th>
              <th>Total</th>
            </tr>
            <xsl:for-each select="/com.cloudbees.jenkins.plugins.testreportgenerator.ReleaseTestResults/testSuites/com.cloudbees.jenkins.plugins.testreportgenerator.ReleaseTestResults_-TestSuiteSummary">
                <tr>
                    <td>
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="link"/>
                            </xsl:attribute>
                            <xsl:value-of select="suite"/>
                        </xsl:element>
                    </td>
                    <td><xsl:value-of select="passed"/></td>
                    <td><xsl:value-of select="failed"/></td>
                    <td><xsl:value-of select="skipped"/></td>
                    <td><xsl:value-of select="total"/></td>
                </tr>

            </xsl:for-each>
        </table>
        <br/><br/>
        <h3>Failing Tests for the Test Suites</h3>
        <xsl:for-each select="/com.cloudbees.jenkins.plugins.testreportgenerator.ReleaseTestResults/failedTests/entry">
            <details>
                <summary><strong><xsl:value-of select="string"/></strong></summary>
                <ul class="list-group">
                    <xsl:for-each select="list/string">
                        <li class="list-group-item"><xsl:value-of select="current()"/></li>
                    </xsl:for-each>
                </ul>
            </details>
            <br/><br/>
        </xsl:for-each>

        <!-- JSON representation for storage's sake -->
        <input type="hidden" name="inputJason" value="{/com.cloudbees.jenkins.plugins.testreportgenerator.ReleaseTestResults/asJson}"/> 
    </body>
</html>
</xsl:template>
</xsl:stylesheet>