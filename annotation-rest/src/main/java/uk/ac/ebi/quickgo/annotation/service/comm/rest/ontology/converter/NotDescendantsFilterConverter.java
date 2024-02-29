package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.converter;

import com.google.common.base.Strings;
import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;
import uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.model.OntologyRelatives;
import uk.ac.ebi.quickgo.rest.search.RetrievalException;
import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.converter.ConvertedFilter;

import java.util.Set;

import static java.util.Objects.nonNull;
import static uk.ac.ebi.quickgo.common.validator.OntologyIdPredicate.isValidGOTermId;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.not;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.or;

public class NotDescendantsFilterConverter extends AbstractOntologyFilterConverter {
  @Override protected boolean validResult(OntologyRelatives.Result result) {
    return nonNull(result.getDescendants());
  }

  @Override protected void processResult(OntologyRelatives.Result result, Set<QuickGOQuery> queries) {
    result.getDescendants().stream()
      .filter(desc -> !Strings.isNullOrEmpty(desc))
      .forEach(desc -> queries.add(createQuery(desc)));
  }

  @Override protected ConvertedFilter<QuickGOQuery> createFilter(Set<QuickGOQuery> queries) {
    if (!queries.isEmpty()) {
      return new ConvertedFilter<>(not(or(queries.toArray(new QuickGOQuery[queries.size()]))));
    } else {
      return FILTER_EVERYTHING;
    }
  }

  private QuickGOQuery createQuery(String id) {
    if (isValidGOTermId().test(id)) {
      return QuickGOQuery.createQuery(AnnotationFields.Searchable.GP_RELATED_GO_IDS, id);
    } else {
      throw new RetrievalException(UNKNOWN_ID_FORMAT.formatted(id));
    }
  }
}
