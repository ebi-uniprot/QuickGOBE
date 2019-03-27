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

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.and;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

public class AndDescendantsFilterConverter extends AbstractOntologyFilterConverter {

  @Override
  public ConvertedFilter<QuickGOQuery> transform(OntologyRelatives response) {
    if (nonNull(response.getResults())) {
      Set<Set<QuickGOQuery>> queries = new HashSet<>();

      for (OntologyRelatives.Result result : response.getResults()) {
        if (validResult(result)) {
          queries.add(process(result));
        } else {
          addIdWithNoRelative(result.getId());
        }
      }

      convertedFilter = getFilter(queries);
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

  private Set<QuickGOQuery> process(OntologyRelatives.Result result) {
    return result.getDescendants().stream()
      .filter(desc -> !Strings.isNullOrEmpty(desc))
      .map(this::createQuery)
      .collect(Collectors.toSet());
  }

  private ConvertedFilter<QuickGOQuery> getFilter(Set<Set<QuickGOQuery>> queries) {
    if (!queries.isEmpty()) {
      Set<QuickGOQuery> andSet = queries.stream().map(orSet -> or(orSet.toArray(new QuickGOQuery[orSet.size()]))).collect(Collectors.toSet());
      return new ConvertedFilter<>(and(andSet.toArray(new QuickGOQuery[queries.size()])));
    } else {
      return FILTER_EVERYTHING;
    }
  }

  private QuickGOQuery createQuery(String id) {
    if (isValidGOTermId().test(id)) {
      return QuickGOQuery.createQuery(AnnotationFields.Searchable.GP_RELATED_GO_IDS, id);
    } else {
      throw new RetrievalException(String.format(UNKNOWN_ID_FORMAT, id));
    }
  }
}
