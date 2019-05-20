package uk.ac.ebi.quickgo.ontology.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.quickgo.ontology.service.AnnotationExtensionService;

import java.util.Map;

@Api(tags = {"Annotation extension relations & validation"},
        value = "Web services is used to check whether annotation extensions are valid, and which annotation" +
                " extension relations are valid for use with a particular GO term. https://youtu.be/VtmfhIAuhFo")
@RestController
@RequestMapping(value = "/ontology/ae")
public class AnnotationExtensionController {

    private AnnotationExtensionService annotationExtensionService;

    @Autowired
    public AnnotationExtensionController(AnnotationExtensionService annotationExtensionService) {
        this.annotationExtensionService = annotationExtensionService;
    }

    @ApiOperation(value = Docs.Relations.des, response = Map.class, notes = Docs.Relations.note)
    @RequestMapping(path = "/relations", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> displayAbleAnnotationExtensionRelationsHierarchy() {
        return annotationExtensionService.getDisplayAbleAnnotationExtensionRelationsHierarchy();
    }

    @ApiOperation(value = Docs.Domain.des, response = Map.class, notes = Docs.Domain.note)
    @RequestMapping(path = "/relations/{domain}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> allPossibleRelationsForDomain(@ApiParam(Docs.Domain.dDomain) @PathVariable String domain) {
        return annotationExtensionService.getAllPossibleRelationsForDomain(domain);
    }

    @ApiOperation(value = Docs.Validate.des, response = Map.class, notes = Docs.Validate.note)
    @RequestMapping(path = "/{goTermId}/validate/{candidate:.+}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    Map<String, Object> isValidRelation(@ApiParam(Docs.Validate.dGoTermId) @PathVariable String goTermId,
                                        @ApiParam(Docs.Validate.dCandidate) @PathVariable String candidate) {
        return annotationExtensionService.isAnnotationExtensionValidForGoTerm(candidate, goTermId);
    }

    static class Docs {
        static class Relations {
            static final String des = "Annotation extension relations hierarchy";
            static final String note = "Set of available relations," +
                    " and their relationship to each other. We only display relations that are flagged true for" +
                    " the display_for_curators subset in gorel.obo (plus our synthesized root relation)" +
                    " see video https://youtu.be/wUtw8vCv-M8";
        }

        static class Domain {
            static final String des = "Returns the set of relations that can be used with a specific GO term.";
            static final String note = "" +
                    " The relations are grouped into subsets (with each relation potentially featuring in multiple" +
                    " subsets, including the catch-all subset that includes all relations, called '(All relations)')." +
                    " The name of each valid relation is returned, along with a list of all regular expressions that" +
                    " can be used to validate the range of the relation. see video https://youtu.be/AigwTZJ6UWg";
            static final String dDomain = "Domain is nothing but Go term id. The domain specifies which GO term(s)" +
                    " can be extended using the relation. e-g GO:0016310";
        }

        static class Validate {
            static final String des = "whether a given extension is valid or not, and, if not, why not";
            static final String note = "see https://youtu.be/dX8k3qQOTck";
            static final String dGoTermId = "Go term on which you are trying to create extension. e-g GO:0032270";
            static final String dCandidate = "Annotation extension you are trying for create for specific go term." +
                    " e-g occurs_in(CL:0000235),has_output(UniProtKB:O95477)";
        }
    }
}
