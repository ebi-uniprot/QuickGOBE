package uk.ac.ebi.quickgo.service.ontology.impl;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.ontology.OntologyRepository;
import uk.ac.ebi.quickgo.service.ontology.OntologyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service to query information from the {@link OntologyRepository}
 * and return OntologyDocuments.
 *
 * TODO:
 *  I think it'd be cleaner if the service retrieved Documents from Solr, and converted
 *  them into domain relevant objects (simple DTOs); which, if annotated correctly (and
 *  even without annotations!) can be serialized by the REST end into json/xml accordingly.
 *
 *
 * Created 11/11/15
 * @author Edd
 */
@Service
public class OntologyServiceImpl implements OntologyService {

    private static final Pattern IGNORED_CHARS_PATTERN = Pattern.compile("\\p{Punct}");

    @Autowired
    private OntologyRepository ontologyRepository;

    @Override public List<OntologyDocument> findAll(Pageable pageable) {
        return StreamSupport.stream(
                ontologyRepository.findAll(pageable).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override public List<OntologyDocument> findByGoId(String searchTerm, Pageable pageable) {
        if (StringUtils.isEmpty(searchTerm)) {
            return findAll(pageable);
        }

        return ontologyRepository.findByTermId("term", "go", searchTerm, pageable);
    }

    @Override public List<OntologyDocument> findByEcoId(String ecoId, Pageable pageable) {
        if (StringUtils.isEmpty(ecoId)) {
            return findAll(pageable);
        }

        return ontologyRepository.findByTermId("term", "eco", ecoId, pageable);
    }

    private Collection<String> splitSearchTermAndRemoveIgnoredCharacters(String searchTerm) {
        String[] searchTerms = StringUtils.split(searchTerm, " ");
        List<String> result = new ArrayList<>(searchTerms.length);
        for (String term : searchTerms) {
            if (!StringUtils.isEmpty(term)) {
                result.add(IGNORED_CHARS_PATTERN.matcher(term).replaceAll(" "));
            }
        }
        return result;
    }
}
