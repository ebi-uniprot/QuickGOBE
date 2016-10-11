package uk.ac.ebi.quickgo.ontology.common.coterms;

import uk.ac.ebi.quickgo.ontology.common.coterm.CoTerm;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermRepositorySimpleMap;
import uk.ac.ebi.quickgo.ontology.common.coterm.CoTermSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * @author Tony Wardell
 * Date: 11/10/2016
 * Time: 14:57
 * Created with IntelliJ IDEA.
 */
public class CoTermRepositorySimpleMapTest {

    private CoTermRepositorySimpleMap simpleMap;
    private Map<String, List<CoTerm>> coTermsAll;
    private Map<String, List<CoTerm>> coTermsManual;

    @Before
    public void setup(){
        coTermsAll = new HashMap<>();
        coTermsAll.put("GO:0001234", Arrays.asList(new CoTerm("GO:0001234", "GO:0001234", 79f, 46f, 838, 933),
               new CoTerm("GO:0001234", "GO:0003870", 54f, 55f, 335, 9424),
               new CoTerm("GO:0001234", "GO:0009058", 24f, 24f, 5732, 355)));
        coTermsAll.put("GO:0016857", Arrays.asList(new CoTerm("GO:0016857", "GO:0001234", 34f, 66f, 556, 872)));


        coTermsManual = new HashMap<>();
        coTermsManual.put("GO:0001234", Arrays.asList(new CoTerm("GO:0001234", "GO:0009999", 99f, 47f, 34356, 456),
                new CoTerm("GO:0001234", "GO:0055085", 24f, 4f, 465, 4564)));

        simpleMap = new CoTermRepositorySimpleMap(coTermsAll, coTermsManual);
    }

    @Test
    public void retrievalIsSuccessfulFromAll(){
        List<CoTerm> results = simpleMap.findCoTerms("GO:0001234", CoTermSource.ALL, 7, t -> true);
        assertThat(results, hasSize(3));
    }

    @Test
    public void retrievalIsSuccessfulWithFilteringPredicate(){
        Predicate<CoTerm> filter =ct -> ct.getProbabilityRatio()==54f;
        List<CoTerm> results = simpleMap.findCoTerms("GO:0001234", CoTermSource.ALL, 7, filter);
        assertThat(results, hasSize(1));
        assertThat(results.get(0).getId(), is("GO:0001234"));
        assertThat(results.get(0).getCompare(), is("GO:0003870"));
    }

    @Test
    public void retrievalIsSuccessfulWithLimitLessThanNumberOfRecordsThatWouldOtherwiseBeReturned(){
        List<CoTerm> results = simpleMap.findCoTerms("GO:0001234", CoTermSource.ALL, 1, t -> true);
        assertThat(results, hasSize(1));
    }

    @Test
    public void retrievalIsSuccessfulFromManual(){
        List<CoTerm> results = simpleMap.findCoTerms("GO:0001234", CoTermSource.MANUAL, 7, t -> true);
        assertThat(results, hasSize(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfCoTermSourceIsNull(){
        simpleMap.findCoTerms("GO:0001234", null, 7, t -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfSearchIdIsNull(){
        simpleMap.findCoTerms(null, CoTermSource.ALL, 7, t -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownIfFilterIsNull(){
        simpleMap.findCoTerms("GO:0001234", CoTermSource.ALL, 7, null);
    }
}
