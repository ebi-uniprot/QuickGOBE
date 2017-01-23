package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Responsible for converting {@link Annotation} instances into GAF model instances, whilst also
 * providing a method to retrieve header information.
 *
 * Created 19/01/17
 * @author Edd
 */
public class GAFAnnotationConverter {
    // todo: this could be a bean in future where we have something like:
    /*
   @Scheduled(cron = "0 30 4 * * *") // 4:30 am each day
   public void scheduled() { // should not have arguments
        updateOntologyTimeStamp();
   }

   // reads the version of the ontology from ontology_iri.dat.gz
   private void updateOntologyTimeStamp() { ... }

     */

    public List<String> getHeaderLines(QueryResult<Annotation> result) {
        return asList("# GAF", "# Date: "+ new Date());
    }

    public String convert(Annotation annotation) {
        return annotation.geneProductId+"\t"+annotation.goId+"\tvalue2\tvalue3\tvalue4";
    }
}