package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.OntologyDocConverter;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.data.domain.Pageable;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return {@link OBOTerm} subclasses.
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyServiceImpl<T extends OBOTerm> implements OntologyService<T> {
    private OntologyRepository ontologyRepository;
    private OntologyDocConverter<T> converter;
    private String ontologyType;

    // necessary for Spring to create a proxy class
    private OntologyServiceImpl() {}

    public OntologyServiceImpl(
            OntologyRepository repository,
            OntologyDocConverter<T> converter,
            OntologyType type) {

        Preconditions.checkArgument(repository != null, "Ontology repository can not be null");
        Preconditions.checkArgument(type != null, "Ontology type can not be null");
        Preconditions.checkArgument(converter != null, "Ontology converter can not be null");

        this.ontologyType = type.name();
        this.ontologyRepository = repository;
        this.converter = converter;
    }

    @Override public Optional<T> findCompleteInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findCompleteByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findCoreInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findCoreByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findHistoryInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findHistoryByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findXRefsInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findXRefsByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findTaxonConstraintsInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findTaxonConstraintsByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findXORelationsInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findXOntologyRelationsByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    @Override public Optional<T> findAnnotationGuideLinesInfoByOntologyId(String id) {
        return convertOptionalDoc(ontologyRepository.findAnnotationGuidelinesByTermId(ontologyType,
                ClientUtils.escapeQueryChars(id)));
    }

    protected Optional<T> convertOptionalDoc(Optional<OntologyDocument> optionalDoc) {
        return optionalDoc.map(converter::convert);
    }

    @Override public List<T> findAll(Pageable pageable) {
        return StreamSupport.stream(
                ontologyRepository.findAll(pageable)
                        .map(converter::convert)
                        .spliterator(), false)
                .collect(Collectors.toList());
    }
}
