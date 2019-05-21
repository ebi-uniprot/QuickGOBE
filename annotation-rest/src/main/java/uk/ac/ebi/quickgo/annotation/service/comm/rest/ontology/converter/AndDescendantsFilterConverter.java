package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import com.google.common.base.Strings;
import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

public class AndDescendantsFilterConverter extends AbstractOntologyFilterConverter {

  @Override
  public ConvertedFilter<QuickGOQuery> transform(OntologyRelatives response) {
    if (nonNull(response.getResults())) {
      Set<Set<QuickGOQuery>> gpRelatedGoIdQueries = new HashSet<>();
      Set<Set<QuickGOQuery>> goIdQueries = new HashSet<>();

      for (OntologyRelatives.Result result : response.getResults()) {
        if (validResult(result)) {
          gpRelatedGoIdQueries.add(produceQueries(result, AnnotationFields.Searchable.GP_RELATED_GO_IDS));
          goIdQueries.add(produceQueries(result, AnnotationFields.Searchable.GO_ID));
        } else {
          addIdWithNoRelative(result.getId());
        }
      }

      convertedFilter = getFilter(gpRelatedGoIdQueries, goIdQueries);
      handleInvalidIds();
    }

    return convertedFilter;
  }

  @Override protected boolean validResult(OntologyRelatives.Result result) {
    return nonNull(result.getDescendants());
  }

  @Override
  protected void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries) {
    throw new UnsupportedOperationException();
  }

  private Set<QuickGOQuery> produceQueries(OntologyRelatives.Result result, String fieldName) {
    return result.getDescendants().stream()
      .filter(desc -> !Strings.isNullOrEmpty(desc))
      .map(id -> createQuery(id, fieldName))
      .collect(Collectors.toSet());
  }

  private ConvertedFilter<QuickGOQuery> getFilter(Set<Set<QuickGOQuery>> gpRelatedGoIdQueries, Set<Set<QuickGOQuery>> goIdQueries) {
    if (!gpRelatedGoIdQueries.isEmpty()) {
      Set<QuickGOQuery> gpRelatedOrPerResult = convertToOrQuerySet(gpRelatedGoIdQueries);
      Set<QuickGOQuery> goIdsOrSet = convertToOrQuerySet(goIdQueries);
      QuickGOQuery goIdsQuery = or(goIdsOrSet.toArray(new QuickGOQuery[goIdsOrSet.size()]));

      return new ConvertedFilter<>(and(getArray(goIdsQuery, gpRelatedOrPerResult)));
    } else {
      return FILTER_EVERYTHING;
    }
  }

  private QuickGOQuery[] getArray(QuickGOQuery goIdsQuery, Set<QuickGOQuery> gpRelatedOrPerResult) {
    Set<QuickGOQuery> tmp = new HashSet<>();
    tmp.add(goIdsQuery);
    tmp.addAll(gpRelatedOrPerResult);
    return tmp.toArray(new QuickGOQuery[0]);
  }

  private Set<QuickGOQuery> convertToOrQuerySet(Set<Set<QuickGOQuery>> queries){
    return queries.stream().map(orSet -> or(orSet.toArray(new QuickGOQuery[orSet.size()]))).collect(Collectors.toSet());
  }

  private QuickGOQuery createQuery(String goId, String fieldName) {
    if (isValidGOTermId().test(goId)) {
      return QuickGOQuery.createQuery(fieldName, goId);
    } else {
      throw new RetrievalException(String.format(UNKNOWN_ID_FORMAT, goId));
    }
  }
}
