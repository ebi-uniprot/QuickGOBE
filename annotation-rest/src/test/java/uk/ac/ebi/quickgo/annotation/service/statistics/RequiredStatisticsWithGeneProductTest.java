package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

/**
 * Test the operation of the RequiredStatisticsWithGeneProduct class.
 * @author Tony Wardell
 * Date: 22/12/2017
 * Time: 11:05
 * Created with IntelliJ IDEA.
 */
class RequiredStatisticsWithGeneProductTest {

    private final StatisticsTypeConfigurer configurer = mock(StatisticsTypeConfigurer.class);

    @Test
    void isConstructedProperly() {
        List<RequiredStatisticType> requiredStatisticTypes = new ArrayList<>();
        when(configurer.getConfiguredStatsTypes(anyList())).thenReturn(requiredStatisticTypes);

        RequiredStatisticsWithGeneProduct withGeneProduct = new RequiredStatisticsWithGeneProduct(configurer);
        assertThat(withGeneProduct.getStatsTypes(), hasSize(7));
    }
}
