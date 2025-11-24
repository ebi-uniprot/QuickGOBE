package uk.ac.ebi.quickgo.client.controller;

import io.swagger.annotations.ApiOperation;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/internal/xrefMetaData")
public class XrefMetaDataController {
  private final RestTemplate restTemplate;

  public XrefMetaDataController(RestTemplate followRedirect) {
    this.restTemplate = followRedirect;
  }

  @ApiOperation(value = "Fetch data from gene ontology website and provide it to FE consumption")
  @RequestMapping(method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> externalXrefMetaData() {
    final String uri = "https://snapshot.geneontology.org/metadata/db-xrefs.json";
    return ResponseEntity.ok(restTemplate.getForObject(uri, String.class));
  }

  @Bean("followRedirect")
  public static RestTemplate restTemplate() {
    HttpClient httpClient = HttpClientBuilder.create()
      .setRedirectStrategy(new LaxRedirectStrategy()) // This handles HTTP->HTTPS redirects
      .build();

    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(10000);

    return new RestTemplate(factory);
  }
}
