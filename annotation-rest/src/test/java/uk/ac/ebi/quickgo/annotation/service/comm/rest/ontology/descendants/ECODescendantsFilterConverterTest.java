package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;

import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createEvidenceCode;

/**
 * Created by edd on 02/11/2016.
 */
class ECODescendantsFilterConverterTest extends AbstractDescendantsFilterConverterTest {

    public ECODescendantsFilterConverterTest() {
        field = AnnotationFields.Searchable.EVIDENCE_CODE;
    }
    @Override
    public String ontologyId(int id) {
        return createEvidenceCode(id);
    }
}