package uk.ac.ebi.quickgo.ontology.service;

import uk.ac.ebi.quickgo.ontology.common.OntologyRepository;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;
import uk.ac.ebi.quickgo.ontology.service.converter.OntologyDocConverter;

import com.google.common.base.Preconditions;
import java.util.List;
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

    @Override public List<T> findCompleteInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCompleteByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findCoreInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findCoreByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findHistoryInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findHistoryByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findXRefsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXRefsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findTaxonConstraintsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findTaxonConstraintsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findXORelationsInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findXOntologyRelationsByTermId(ontologyType, buildIdList(ids)));
    }

    @Override public List<T> findAnnotationGuideLinesInfoByOntologyId(List<String> ids) {
        return convertDocs(ontologyRepository.findAnnotationGuidelinesByTermId(ontologyType, buildIdList(ids)));
    }

    protected List<T> convertDocs(List<OntologyDocument> docs) {
        return docs.stream().map(converter::convert).collect(Collectors.toList());
    }

    @Override public List<T> findAll(Pageable pageable) {
        return StreamSupport.stream(
                ontologyRepository.findAll(pageable)
                        .map(converter::convert)
                        .spliterator(), false)
                .collect(Collectors.toList());
    }

    protected static List<String> buildIdList(List<String> ids) {
        Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

        return ids.stream().map(ClientUtils::escapeQueryChars).collect(Collectors.toList());
    }
}
