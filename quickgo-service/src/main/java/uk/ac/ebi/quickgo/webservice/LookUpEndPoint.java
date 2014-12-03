package uk.ac.ebi.quickgo.webservice;

import org.springframework.ws.server.endpoint.annotation.Endpoint;

/**
 * @deprecated We are providing REST web services. This class would be useful
 *             in case we wanted to provide SOAP services
 * @author cbonill
 * 
 */

@Endpoint
public class LookUpEndPoint {
	 private static final String NAMESPACE_URI = "http://www.ebi.ac.uk/QuickGO/ws";

	/*private GOTermService goTermService;

	@Autowired
	public LookUpEndPoint(GOTermService goTermService) {
		this.goTermService = goTermService;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "findByIdRequest")
	@ResponsePayload
	public FindByIdResponse getCountry(@RequestPayload FindByIdRequest request) {
		FindByIdResponse response = new FindByIdResponse();
		GOTerm term  = goTermService.retrieveGOTerm(request.getId());
		Term termResponse = new Term();
		termResponse.setId(term.getId());
		termResponse.setName(term.getName());		
		response.setTerm(termResponse);

		return response;
	}*/
}