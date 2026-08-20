package uk.ac.ebi.quickgo.client.controller;

import io.swagger.v3.oas.annotations.Operation;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.LaxRedirectStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/internal/xrefMetaData")
public class XrefMetaDataController {
  private final RestTemplate restTemplate;

  public XrefMetaDataController(RestTemplate followRedirect) {
    this.restTemplate = followRedirect;
  }

  @Operation(summary = "Fetch data from gene ontology website and provide it to FE consumption")
  @GetMapping( produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> externalXrefMetaData() {
    final String uri = "https://snapshot.geneontology.org/metadata/db-xrefs.json";
    return ResponseEntity.ok(restTemplate.getForObject(uri, String.class));
  }

  @Bean("followRedirect")
  public static RestTemplate restTemplate() {
    // 1. Configure Timeouts on RequestConfig (Spring 6.1+ requirement)
    RequestConfig requestConfig = RequestConfig.custom()
      .setConnectTimeout(Timeout.ofMilliseconds(5000))
      .setResponseTimeout(Timeout.ofMilliseconds(10000)) // Replaces setReadTimeout
      .build();

    // 2. Build HttpClient with LaxRedirectStrategy and RequestConfig
    HttpClient httpClient = HttpClientBuilder.create()
      .setRedirectStrategy(new LaxRedirectStrategy()) // Handles HTTP -> HTTPS
      .setDefaultRequestConfig(requestConfig)
      .build();

    // 3. Pass the configured HttpClient to the Factory
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

    return new RestTemplate(factory);
  }
}
