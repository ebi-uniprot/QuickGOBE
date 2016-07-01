package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts the information defined in {@link OntologyDocument#definition} and {@link OntologyDocument#definitionXrefs}
 * into a structured {@link uk.ac.ebi.quickgo.ontology.model.OBOTerm.Definition} object.
 *
 * @author Ricardo Antunes
 */
class DefinitionConverter implements MultiDocFieldToFieldConverter<OBOTerm.Definition> {

    @Override public OBOTerm.Definition apply(OntologyDocument ontologyDocument) {
        OBOTerm.Definition def = new OBOTerm.Definition();
        def.definition = ontologyDocument.definition;
        def.definitionXrefs = ontologyDocument.definitionXrefs.stream()
                .map(this::convert)
                .collect(Collectors.toList());

        return def;
    }

    private OBOTerm.XRef convert(String xrefText) {
        List<FlatField> fields = FlatFieldBuilder.newFlatField().parse(xrefText).getFields();

        OBOTerm.XRef xref = new OBOTerm.XRef();
        xref.dbCode = fields.get(0).buildString();
        xref.dbId = fields.get(1).buildString();

        return xref;
    }
}
