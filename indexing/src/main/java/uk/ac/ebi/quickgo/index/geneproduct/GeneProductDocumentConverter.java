package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.batch.item.ItemProcessor;

/**
 * Converts a {@link GeneProduct} into an {@link uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument}
 *
 * @author Ricardo Antunes
 */
public class GeneProductDocumentConverter implements ItemProcessor<GeneProduct, GeneProductDocument> {
    private static final String TAXON_NAME_KEY = "taxon_name";
    private static final String COMPLETE_PROTEOME_KEY = "proteome";
    private static final String REFERENCE_PROTEOME_KEY = "reference_proteome";
    private static final String IS_ANNOTATED_KEY = "is_annotated";
    private static final String IS_ISOFORM = "is_isoform";
    private static final String DATABASE_SUBSET_KEY = "db_subsets";

    private static final String TRUE_STRING = "Y";
    private final static Pattern TAXON_ID_PATTERN = Pattern.compile("taxon:([0-9]+)");


    private final String interValueDelimiter;
    private final String intraValueDelimiter;

    public GeneProductDocumentConverter(String interValueDelimiter, String intraValueDelimiter) {
        Preconditions.checkArgument(interValueDelimiter != null && interValueDelimiter.length() > 0,
                "Inter value delimiter can not be null or empty");
        Preconditions.checkArgument(intraValueDelimiter != null && intraValueDelimiter.length() > 0, "Intra " +
                "value delimiter can not be null or empty");

        this.interValueDelimiter = interValueDelimiter;
        this.intraValueDelimiter = intraValueDelimiter;
    }

    @Override public GeneProductDocument process(GeneProduct geneProduct) throws Exception {
        if(geneProduct == null) {
            throw new DocumentReaderException("Gene product object is null");
        }

        Map<String, String> properties = convertToMap(geneProduct.properties);

        GeneProductDocument doc = new GeneProductDocument();
        doc.database = geneProduct.database;
        doc.id = geneProduct.id;
        doc.symbol = geneProduct.symbol;
        doc.name = geneProduct.name;
        doc.synonyms = convertToList(splitValue(geneProduct.synonym, interValueDelimiter));
        doc.type = geneProduct.type;
        doc.taxonId = extractTaxonIdFromValue(geneProduct.taxonId);
        doc.parentId = geneProduct.parentId;
        doc.taxonName = properties.get(TAXON_NAME_KEY);
        doc.referenceProteome = properties.get(REFERENCE_PROTEOME_KEY);
        doc.databaseSubsets = convertToList(properties.get(DATABASE_SUBSET_KEY));

        doc.isCompleteProteome = isTrue(properties.get(COMPLETE_PROTEOME_KEY));
        doc.isAnnotated = isTrue(properties.get(IS_ANNOTATED_KEY));
        doc.isIsoform = isTrue(properties.get(IS_ISOFORM));

        return doc;
    }

    private String[] splitValue(String value, String delimiter) {
        String[] splitValues;
        if (value != null) {
            splitValues = value.split(delimiter);
        } else {
            splitValues = new String[0];
        }

        return splitValues;
    }

    private Map<String, String> convertToMap(String propsText) {
        Map<String, String> propMap = new HashMap<>();

        String[] unformattedProps = splitValue(propsText, interValueDelimiter);

        Arrays.stream(unformattedProps)
                .forEach(unformattedProp -> {
                    String[] splitProp = splitValue(unformattedProp, intraValueDelimiter);
                    propMap.put(splitProp[0], splitProp[1]);
                });

        return propMap;
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

    private int extractTaxonIdFromValue(String value) {
        int taxonId = 0;

        if(value != null) {
            Matcher matcher = TAXON_ID_PATTERN.matcher(value);

            if(matcher.matches()) {
                taxonId = Integer.parseInt(matcher.group(1));
            }
        }

        return taxonId;
    }
}