package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

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
public class RequiredStatisticsWithGeneProductTest {

    private StatisticsTypeConfigurer configurer = mock(StatisticsTypeConfigurer.class);

    @Test
    public void isConstructedProperly() {
        List<RequiredStatisticType> requiredStatisticTypes = new ArrayList<>();
        when(configurer.getConfiguredStatsTypes(anyList())).thenReturn(requiredStatisticTypes);

        RequiredStatisticsWithGeneProduct withGeneProduct = new RequiredStatisticsWithGeneProduct(configurer);
        assertThat(withGeneProduct.getStatsTypes(), hasSize(8));
    }
}
