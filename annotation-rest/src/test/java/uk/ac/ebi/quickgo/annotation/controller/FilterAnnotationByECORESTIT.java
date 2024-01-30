package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.IdGeneratorUtil;
import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;

import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.EVIDENCE_CODE_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.EVIDENCE_CODE_USAGE_PARAM;
import static uk.ac.ebi.quickgo.annotation.AnnotationParameters.EVIDENCE_CODE_USAGE_RELATIONS_PARAM;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;

/**
 * Created 02/11/16
 * @author Edd
 */
class FilterAnnotationByECORESTIT extends AbstractFilterAnnotationByOntologyRESTIT {
    private static final String ECO_DESCENDANTS_RESOURCE_FORMAT = "/ontology/eco/terms/%s/descendants?relations=%s";

    public FilterAnnotationByECORESTIT() {
        resourceFormat = ECO_DESCENDANTS_RESOURCE_FORMAT;
        usageParam = EVIDENCE_CODE_USAGE_PARAM.getName();
        idParam = EVIDENCE_CODE_PARAM.getName();
        usageRelations = EVIDENCE_CODE_USAGE_RELATIONS_PARAM.getName();
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
