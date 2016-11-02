package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;

import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;

/**
 * Created by edd on 02/11/2016.
 */
public class GODescendantsFilterConverterTest extends AbstractDescendantsFilterConverterTest {
    public GODescendantsFilterConverterTest() {
        field = AnnotationFields.GO_ID;
    }

    @Override
    public String ontologyId(int id) {
        return createGoId(id);
    }
}
