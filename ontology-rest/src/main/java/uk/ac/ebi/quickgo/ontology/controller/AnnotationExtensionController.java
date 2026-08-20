package uk.ac.ebi.quickgo.ontology.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.quickgo.ontology.service.AnnotationExtensionService;

import java.io.File;
import java.util.Map;

@Tag(name = "Annotation extension relations & validation",
        description = "Web services is used to check whether annotation extensions are valid, and which annotation" +
                " extension relations are valid for use with a particular GO term. https://youtu.be/VtmfhIAuhFo")
@RestController
@RequestMapping(value = "/ontology/ae")
public class AnnotationExtensionController {

    private AnnotationExtensionService annotationExtensionService;

    @Autowired
    public AnnotationExtensionController(AnnotationExtensionService annotationExtensionService) {
        this.annotationExtensionService = annotationExtensionService;
    }

    @Operation(summary = Docs.Relations.des, description = Docs.Relations.note)
    @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @GetMapping(path = "/relations", produces = APPLICATION_JSON_VALUE)
    Map<String, Object> displayAbleAnnotationExtensionRelationsHierarchy() {
        return annotationExtensionService.getDisplayAbleAnnotationExtensionRelationsHierarchy();
    }

    @Operation(summary = Docs.Domain.des, description = Docs.Domain.note)
    @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @GetMapping(path = "/relations/{domain}", produces = APPLICATION_JSON_VALUE)
    Map<String, Object> allPossibleRelationsForDomain(@Parameter(description = Docs.Domain.dDomain) @PathVariable String domain) {
        return annotationExtensionService.getAllPossibleRelationsForDomain(domain);
    }

    @Operation(summary = Docs.Validate.des, description = Docs.Validate.note)
    @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    @GetMapping(path = "/{goTermId}/validate/{candidate:.+}", produces = APPLICATION_JSON_VALUE)
    Map<String, Object> isValidRelation(@Parameter(description = Docs.Validate.dGoTermId) @PathVariable String goTermId,
                                        @Parameter(description = Docs.Validate.dCandidate) @PathVariable String candidate) {
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
