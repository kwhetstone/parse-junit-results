# parse-junit-results
A Jenkins plugin to create combination reports from different junit test results based on a provided fingerprint.  After installing as a plugin on Jenkins, you can use as follows:

`testReportForFingerprint {fingerprint}`

This will generate a HTML report file for use with the HTML Publisher plugin (or just to download).  There are a few advanced options which create an XML object representing the report (results.xml), a JSON object (results.json), and a report summary in JIRA markup (results.txt).

`testReportForFingerprint fingerprint: {fingerprint}, publishForJIRA: true, publishJSON: true, publishXML: true`

This feature must be run in a `node`, and due to writing files, is best run inside it's own folder.

### Advanced 
If you have a series of known test reports you'd like to see reflected in the test report regardless of having results associated with this fingerprint, you can explicitly note them in the environment variable `DEFAULT_TEST_SUITES`.  This is useful if you need to know if a particular test suite has been skipped. 


### Features
- [x] Save HTML report - index.html
- [x] Publish HTML report - available on front page of task
- [x] Save XML
- [x] Save JSON
- [x] Save text (for JIRA ticket)
- [ ] Customize report names?

### Todo
- [ ] Add better documentation
- [ ] Add unit tests
- [ ] Add internationalization
- [ ] Generic-ize? Would this be helpful to others as a way to look up fingerprints?