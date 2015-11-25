package uk.ac.ebi.quickgo.service.ontology.impl;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;
import uk.ac.ebi.quickgo.model.ontology.converter.OntologyDocConverter;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return {@link OBOTerm} subclasses.
 *
 * Created 11/11/15
 * @author Edd
 */
@Service
public class OntologyServiceImpl<T extends OBOTerm> implements OntologyService<T> {
    private OntologyRepository ontologyRepository;
    private OntologyDocConverter<T> converter;

    private String ontologyType;

    public OntologyServiceImpl() {}

    public OntologyServiceImpl(OntologyRepository repository, OntologyDocConverter<T> converter,
            OntologyType type) {
        ontologyType = type.name();

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

    private Optional<T> convertOptionalDoc(Optional<OntologyDocument> optionalDoc) {
        if (optionalDoc.isPresent()) {
            return Optional.of(converter.convert(optionalDoc.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override public List<OntologyDocument> findAll(Pageable pageable) {
        return StreamSupport.stream(
                ontologyRepository.findAll(pageable).spliterator(), false)
                .collect(Collectors.toList());
    }

}
