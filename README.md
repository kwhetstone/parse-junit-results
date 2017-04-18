# parse-junit-results
A Jenkins plugin to create combination reports from different junit test results based on provided fingerprints.

# Work in progress
Currently this will take a fingerprint and generate a composite test report of all 

### Features
- [x] Save HTML report - index.html
- [x] Publish HTML report - available on front page of task
- [x] Save XML
- [x] Save JSON
- [x] Save text (for JIRA ticket)
- [ ] Publish to Confluence (?) pretty dependent on the Confluence plugin; definite stretch goal

### Todo
- [ ] Add better documentation
- [ ] Add unit tests
- [ ] Add internationalization
- [ ] Generic-ize? Would this be helpful to others as a way to look up fingerprints?