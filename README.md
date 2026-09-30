# QuickGO: Back-End
This repository contains the back-end logic (indexing in solr and REST API) for QuickGO Project.

Project webpage. https://www.ebi.ac.uk/QuickGO/

# Health Checks (Actuator)
Each REST service (`annotation-rest`, `ontology-rest`, `geneproduct-rest`, `quickgo-client`)
exposes a Kubernetes-ready health endpoint from spring-boot-actuator on a separate
management port (default `9090`, override with the `MANAGEMENT_PORT` env var):

```
GET http://<host>:9090/actuator/health   ->  {"status": "UP"}
```

The health status is aggregated by `uk.ac.ebi.quickgo.rest.health.SolrHealthIndicator`
(rest-common), which pings the Solr collection configured per service via
`health.solr.collection` (`annotation`, `ontology`, `geneproduct`).

`deployments/src/main/distros/rest/bin/status-check-on-vm` supplements its TCP port
check with `curl http://127.0.0.1:$MANAGEMENT_PORT/actuator/health` and fails if the
health status is not `UP`.

# License
Distributed under the Apache License, Version 2.0.
