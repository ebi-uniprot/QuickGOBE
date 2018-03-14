package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemProcessor;

import static uk.ac.ebi.quickgo.geneproduct.common.ProteomeMembership.*;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.convertLinePropertiesToMap;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.splitValue;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.*;

/**
 * Converts a {@link GeneProduct} into an {@link GeneProductDocument}
 *
 * @author Ricardo Antunes
 */
public class GeneProductDocumentConverter implements ItemProcessor<GeneProduct, GeneProductDocument> {

    /**
     * The following values define how the contents of the Gene Product properties are described.
     * E.g <code>db_object_type=protein|go_aspect=cellular_component|target_set=KRUK,BHF-UCL</code>
     * where pipes are inter-value delimiters, the equals symbol is the intra-value delimiter
     * to split up keys and their value and comma is the delimiter for values for a single key.
     */
    private final String interValueDelimiter;
    private final String intraValueDelimiter;
    private final String specificValueDelimiter;

    public GeneProductDocumentConverter(String interValueDelimiter, String intraValueDelimiter, String
            specificValueDelimiter) {
        Preconditions.checkArgument(interValueDelimiter != null && interValueDelimiter.length() > 0,
                "Inter value delimiter can not be null or empty");
        Preconditions.checkArgument(intraValueDelimiter != null && intraValueDelimiter.length() > 0, "Intra " +
                "value delimiter can not be null or empty");
        Preconditions.checkArgument(specificValueDelimiter != null && specificValueDelimiter.length() > 0, "Specific " +
                "value delimiter can not be null or empty");
        this.interValueDelimiter = interValueDelimiter;
        this.intraValueDelimiter = intraValueDelimiter;
        this.specificValueDelimiter = specificValueDelimiter;
    }

    @Override public GeneProductDocument process(GeneProduct geneProduct) {
        if (geneProduct == null) {
            throw new DocumentReaderException("Gene product object is null");
        }

        Map<String, String> properties =
                convertLinePropertiesToMap(geneProduct.properties, interValueDelimiter, intraValueDelimiter);

        GeneProductDocument doc = new GeneProductDocument();
        doc.database = geneProduct.database;
        doc.id = geneProduct.id;
        doc.symbol = geneProduct.symbol;
        doc.name = geneProduct.name;
        doc.synonyms = convertToList(splitValue(geneProduct.synonym, interValueDelimiter));
        doc.type = geneProduct.type;
        doc.taxonId = extractTaxonIdFromValue(geneProduct.taxonId);
        doc.taxonName = properties.get(TAXON_NAME_KEY);
        doc.parentId = geneProduct.parentId;
        doc.referenceProteome = properties.get(REFERENCE_PROTEOME_KEY);
        doc.databaseSubset = properties.get(DATABASE_SUBSET_KEY);
        doc.targetSet = convertToList((splitValue(properties.get(TARGET_SET_KEY), specificValueDelimiter)));
        doc.isCompleteProteome = isTrue(properties.get(COMPLETE_PROTEOME_KEY));
        doc.isAnnotated = isTrue(properties.get(IS_ANNOTATED_KEY));
        doc.isIsoform = isTrue(properties.get(IS_ISOFORM_KEY));
        doc.proteomeMembership = extractProteomeMembership(geneProduct, properties);
        return doc;
    }

    private ProteomeMembership extractProteomeMembership(GeneProduct geneProduct, Map<String, String> properties) {
        if (GeneProductType.PROTEIN.getName().equals(geneProduct.type)) {
            if (Objects.nonNull(properties.get(REFERENCE_PROTEOME_KEY))) {
                return REFERENCE;
            } else if (isTrue(properties.get(COMPLETE_PROTEOME_KEY))) {
                return COMPLETE;
            } else {
                return NONE;
            }
        }
        return NOT_APPLICABLE;
    }

    private boolean isTrue(String value) {
        return value != null && value.equalsIgnoreCase(TRUE_STRING);
    }

    @SafeVarargs private final <T> List<T> convertToList(T... elements) {
        List<T> list = Arrays.stream(elements)
                .filter(element -> element != null)
                .collect(Collectors.toList());

        return list.size() == 0 ? null : list;
    }
}
