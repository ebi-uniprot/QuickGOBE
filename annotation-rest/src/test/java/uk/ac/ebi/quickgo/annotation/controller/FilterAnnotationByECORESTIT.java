package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;

import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;

/**
 * Created 02/11/16
 * @author Edd
 */
public class FilterAnnotationByECORESTIT extends AbstractFilterAnnotationByOntologyRESTIT {
    private static final String ECO_DESCENDANTS_RESOURCE_FORMAT = "/ontology/eco/terms/%s/descendants?relations=%s";
    private static final String EVIDENCE_CODE_USAGE = "evidenceCodeUsage";
    private static final String EVIDENCE_CODE_USAGE_RELATIONS = "evidenceCodeUsageRelationships";

    public FilterAnnotationByECORESTIT() {
        resourceFormat = ECO_DESCENDANTS_RESOURCE_FORMAT;
        usageParam = EVIDENCE_CODE_USAGE;
        idParam = AnnotationFields.EVIDENCE_CODE;
        usageRelations = EVIDENCE_CODE_USAGE_RELATIONS;
    }

    @Override protected AnnotationDocument createAnnotationDocWithId(String geneProductId, String ecoId) {
        AnnotationDocument doc = createAnnotationDoc(geneProductId);
        doc.evidenceCode = ecoId;

        return doc;
    }

    @Override protected String ontologyId(int id) {
        return IdGeneratorUtil.createEvidenceCode(id);
    }
}
