package uk.ac.ebi.quickgo.client.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/internal/xrefMetaData")
public class XrefMetaDataController {

  @ApiOperation(value = "Fetch data from gene ontology website and provide it to FE consumption")
  @GetMapping( produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> externalXrefMetaData() {
    final String uri = "http://snapshot.geneontology.org/metadata/db-xrefs.json";
    return ResponseEntity.ok(new RestTemplate().getForObject(uri, String.class));
  }
}
