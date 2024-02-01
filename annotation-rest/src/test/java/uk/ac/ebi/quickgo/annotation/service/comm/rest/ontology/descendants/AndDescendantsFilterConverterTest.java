package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter.AndDescendantsFilterConverter;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.*;

class AndDescendantsFilterConverterTest {
  private OntologyRelatives response;
  private AndDescendantsFilterConverter converter;
  private final String field = AnnotationFields.Searchable.GP_RELATED_GO_IDS;
  private final String goField = AnnotationFields.Searchable.GO_ID;

  @BeforeEach
  void setUp() {
    response = new OntologyRelatives();
    response.setResults(new ArrayList<>());
    converter = new AndDescendantsFilterConverter();
  }


  @Test
  void descendantsFromSingleResourceAreConvertedToQuickGOQuery() {
    String id1 = createGoId(1);
    String desc1 = createGoId(2);

    addResponseDescendant(id1, desc1);
    ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

    QuickGOQuery expected = and(QuickGOQuery.createQuery(field, desc1),QuickGOQuery.createQuery(goField, desc1));
    assertThat(convertedFilter.getConvertedValue(), is(expected));
    assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
  }

  @Test
  void differentDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
    String id1 = createGoId(1);
    String id2 = createGoId(2);
    String desc1 = createGoId(11);
    String desc2 = createGoId(22);

    addResponseDescendant(id1, desc1);
    addResponseDescendant(id2, desc2);

    ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

    QuickGOQuery allowedGos = or(QuickGOQuery.createQuery(goField, desc1),QuickGOQuery.createQuery(goField, desc2));
    QuickGOQuery expected = and(QuickGOQuery.createQuery(field, desc1),QuickGOQuery.createQuery(field, desc2), allowedGos);
    assertThat(convertedFilter.getConvertedValue(), is(expected));
    assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
  }

  @Test
  void sameDescendantsFromMultipleResourcesAreConvertedToQuickGOQuery() {
    String id1 = createGoId(1);
    String id2 = createGoId(2);
    String desc1 = createGoId(11);

    addResponseDescendant(id1, desc1);
    addResponseDescendant(id2, desc1);

    ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

    QuickGOQuery expected = and(QuickGOQuery.createQuery(field, desc1),QuickGOQuery.createQuery(goField, desc1));
    assertThat(convertedFilter.getConvertedValue(), is(expected));
    assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
  }

  @Test
  void nullResultsMeansFilterEverything() {
    response.setResults(null);
    ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

    assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
  }

  @Test
  void emptyResultsMeansFilterEverything() {
    ConvertedFilter<QuickGOQuery> convertedFilter = converter.transform(response);

    assertThat(convertedFilter.getConvertedValue(), is(not(QuickGOQuery.createAllQuery())));
    assertThat(convertedFilter.getFilterContext(), is(Optional.empty()));
  }

  private void addResponseDescendant(String termId, String descendantId) {
    for (OntologyRelatives.Result result : response.getResults()) {
      if (result.getId().equals(termId)) {
        result.getDescendants().add(descendantId);
        return;
      }
    }

    OntologyRelatives.Result newResult = new OntologyRelatives.Result();
    newResult.setId(termId);
    List<String> descList = new ArrayList<>();
    descList.add(descendantId);
    newResult.setDescendants(descList);
    response.getResults().add(newResult);
  }
}

