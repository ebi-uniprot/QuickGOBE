# QuickGO: Back-End
This repository contains the back-end logic (indexing in solr and REST API) for QuickGO Project.

Project webpage. https://www.ebi.ac.uk/QuickGO/

# Developer Notes
## Running Integration Tests
We use solr test container to run our integration tests. i-e `mvn clean verify`
1. Download desktop test container app from https://testcontainers.com/desktop/
2. As of today (16 Sep 2026) above app has embedded runtime (experimental feature) but doesn't work.
3. EBI support Rancher Desktop as container run time for developers
4. Start Rancher Desktop on your machine with following settings
   1. Select Preferences -> Virtual Machine -> Volumes -> reverse-sshfs
   2. Select Preferences -> Virtual Machine -> Emulation -> QEMU
5. Start Testcontainers Desktop on your machine with following settings
   1. Select Testcontainers Desktop -> containers running locally -> rancher-desktop

# License
Distributed under the Apache License, Version 2.0.
