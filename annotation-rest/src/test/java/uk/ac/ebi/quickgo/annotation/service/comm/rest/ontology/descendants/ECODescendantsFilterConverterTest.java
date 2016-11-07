package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;

import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createEvidenceCode;

/**
 * Created by edd on 02/11/2016.
 */
public class ECODescendantsFilterConverterTest extends AbstractDescendantsFilterConverterTest {

    public ECODescendantsFilterConverterTest() {
        field = AnnotationFields.EVIDENCE_CODE;
    }
    @Override
    public String ontologyId(int id) {
        return createEvidenceCode(id);
    }

}
