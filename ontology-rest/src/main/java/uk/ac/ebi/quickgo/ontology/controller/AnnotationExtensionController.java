package uk.ac.ebi.quickgo.ontology.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.quickgo.ontology.service.AnnotationExtensionService;

import java.util.Map;

@Api(tags = {"Annotation extension relations & validation"},
        value = "Web services is used to check whether annotation extensions are valid, and which annotation" +
                " extension relations are valid for use with a particular GO term")
@RestController
@RequestMapping(value = "/ontology/ae")
public class AnnotationExtensionController {

    private AnnotationExtensionService annotationExtensionService;

    public AnnotationExtensionController(AnnotationExtensionService annotationExtensionService) {
        this.annotationExtensionService = annotationExtensionService;
    }

    @ApiOperation(Docs.Relations.des)
    @RequestMapping(path = "/relations", method = RequestMethod.GET)
    Map<String, Object> displayAbleAnnotationExtensionRelationsHierarchy() {
        return annotationExtensionService.getDisplayAbleAnnotationExtensionRelationsHierarchy();
    }

    @ApiOperation(Docs.Domain.des)
    @RequestMapping(path = "/relations/{domain}", method = RequestMethod.GET)
    Map<String, Object> allPossibleRelationsForDomain(@ApiParam(Docs.Domain.dDomain) @PathVariable String domain) {
        return annotationExtensionService.getAllPossibleRelationsForDomain(domain);
    }

    @ApiOperation(Docs.Validate.des)
    @RequestMapping(path = "/{goTermId}/validate/{candidate}", method = RequestMethod.GET)
    Map<String, Object> isValidRelation(@ApiParam(Docs.Validate.dGoTermId) @PathVariable String goTermId,
                                        @ApiParam(Docs.Validate.dCandidate) @PathVariable String candidate) {
        return annotationExtensionService.isAnnotationExtensionValidForGoTerm(candidate, goTermId);
    }

    static class Docs {
        static class Relations {
            static final String des = "Annotation extension relations hierarchy. Set of available relations," +
                    " and their relationship to each other. We only display relations that are flagged are in" +
                    " the display_for_curators subset in gorel.obo (plus our synthesized root relation)";
        }

        static class Domain {
            static final String des = "Returns the set of relations that can be used with a specific GO term." +
                    " The relations are grouped into subsets (with each relation potentially featuring in multiple" +
                    " subsets, including the catch-all subset that includes all relations, called '(All relations)')." +
                    " The name of each valid relation is returned, along with a list of all regular expressions that" +
                    " can be used to validate the range of the relation";
            static final String dDomain = "Domain is nothing but Go term id. The domain specifies which GO term(s)" +
                    " can be extended using the relation. For example GO:0016310";
        }

        static class Validate {
            static final String des = "whether a given extension is valid or not, and, if not, why not";
            static final String dGoTermId = "Go term on which you are trying to create extension";
            static final String dCandidate = "Annotation extension you are trying for create for specific go term";
        }
    }
}
