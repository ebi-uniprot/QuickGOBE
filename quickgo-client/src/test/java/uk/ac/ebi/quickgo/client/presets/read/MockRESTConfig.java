package uk.ac.ebi.quickgo.client.presets.read;

import uk.ac.ebi.quickgo.client.presets.read.assignedby.AssignedByRelevancyResponseType;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestOperations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created 02/09/16
 * @author Edd
 */
@Configuration
public class MockRESTConfig {
    static final AssignedByRelevancyResponseType DEFAULT_RELEVANT_ASSIGNED_BYS;

    static final String UNIPROT_KB = "UniProtKB";
    static final String ENSEMBL = "ENSEMBL";
    static final String SUCCESSFUL_FETCHING = "successfulFetching";
    static final String FAILED_FETCHING = "failedFetching";

    static {
        DEFAULT_RELEVANT_ASSIGNED_BYS = new AssignedByRelevancyResponseType();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms = new AssignedByRelevancyResponseType.Terms();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy = new ArrayList<>();
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add(UNIPROT_KB);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add("1000");
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add(ENSEMBL);
        DEFAULT_RELEVANT_ASSIGNED_BYS.terms.assignedBy.add("100");
    }

    @Bean @Profile(SUCCESSFUL_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations restOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);

        when(mockRestOperations.getForObject(
                anyString(),
                isA(Class.class),
                any(HashMap.class)))
                .thenReturn(DEFAULT_RELEVANT_ASSIGNED_BYS);

        return mockRestOperations;
    }

    @Bean @Profile(FAILED_FETCHING)
    @SuppressWarnings("unchecked")
    public RestOperations badRestOperations() {
        RestOperations mockRestOperations = mock(RestOperations.class);
        doThrow(new RuntimeException())
                .when(mockRestOperations)
                .getForObject(
                        anyString(),
                        isA(Class.class),
                        any(HashMap.class));
        return mockRestOperations;
    }
}
