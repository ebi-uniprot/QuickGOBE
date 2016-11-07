package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.FieldType;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.util.function.Function;

/**
 * Is used to convert multiple fields represented within an {@link OntologyDocument} into a single {@link FieldType}.
 *
 * @author Ricardo Antunes
 */
interface MultiDocFieldToFieldConverter<T extends FieldType> extends Function<OntologyDocument, T>  {}
