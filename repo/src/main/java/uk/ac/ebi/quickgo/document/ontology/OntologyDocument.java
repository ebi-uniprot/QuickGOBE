package uk.ac.ebi.quickgo.document.ontology;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Solr document class defining all fields within the ontology core.
 *
 * See {@link http://docs.spring.io/spring-data/solr/docs/current/reference/html/}.
 *
 * TODO: Discuss
 * I have tried modelling multiple docTypes in different XDocument classes, using
 * basic inheritance/interfaces -- however, this does not work cleanly, and this
 * is not really the place for distinguishing different document types even though
 * they all reside within the same core.
 *
 *      We could possibly have multiple Repositories accessing the same core, for adding/retrieving different
 *      docTypes,
 *      e.g.,
 *      SynonymDocument/TermDocument. However, the CrudRepositories won't then work for queries that fetch
 *      information across multiple Repos (e.g., fetch synonyms for a term).
 *
 *      I believe we should retain a single OntologyRepository, which can have complex queries
 *      defined, spanning whatever docTypes we want. Then, at the service layer, we can
 *      transform results into whatever we want, on demand. I'd suggest transforming into
 *      DTOs. E.g., given results:
 *      [SynDoc1, TermDoc1, SynDoc2, TermDoc2] -> [SynModel1, TermModel1, SynModel2, TermModel2]
 *      where *Model is an object that can be produced from a *Doc object, e.g., using Spring's
 *      converter libraries.
 *
 *      See http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html
 *
 *      Moreover, the DTOs can then be handled at the REST layer, and serialized to whatever format required,
 *      on demand, e.g., json, xml, etc.
 *
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyDocument {
    @Field
    public String id;
    @Field
    public String docType;
    @Field
    public String name;
    @Field
    public String ontology;
    @Field
    public String category;
    @Field
    public boolean isObsolete;
    @Field
    public String definition;
    @Field("definitionXref")
    public List<String> definitionXrefs;
    @Field
    public String comment;
    @Field("secondaryId")
    public List<String> secondaryIds;
}
