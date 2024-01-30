package uk.ac.ebi.quickgo.annotation.service.comm.rest.ontology.descendants;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;

import static uk.ac.ebi.quickgo.annotation.IdGeneratorUtil.createGoId;

/**
 * Created by edd on 02/11/2016.
 */
class GODescendantsFilterConverterTest extends AbstractDescendantsFilterConverterTest {
    public GODescendantsFilterConverterTest() {
        field = AnnotationFields.Searchable.GO_ID;
    }

    @Override
    public String ontologyId(int id) {
        return createGoId(id);
    }
}