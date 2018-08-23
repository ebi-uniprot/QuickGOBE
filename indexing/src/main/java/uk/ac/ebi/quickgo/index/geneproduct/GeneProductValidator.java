package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.common.GeneProductType;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import com.google.common.base.Preconditions;
import java.util.Map;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.convertLinePropertiesToMap;
import static uk.ac.ebi.quickgo.index.common.datafile.GOADataFileParsingHelper.splitValue;
import static uk.ac.ebi.quickgo.index.geneproduct.Columns.*;
import static uk.ac.ebi.quickgo.index.geneproduct.GeneProductParsingHelper.*;

/**
 * Checks if the {@link GeneProduct} object has been populated properly.
 *
 * @author Ricardo Antunes
 */
public class GeneProductValidator implements Validator<GeneProduct> {
    private final String interValueDelimiter;
    private final String intraValueDelimiter;

    public GeneProductValidator(String interValueDelimiter, String intraValueDelimiter) {
        Preconditions.checkArgument(interValueDelimiter != null && interValueDelimiter.length() > 0,
                "Inter value delimiter can not be null or empty");
        Preconditions.checkArgument(intraValueDelimiter != null && intraValueDelimiter.length() > 0, "Intra " +
                "value delimiter can not be null or empty");

        this.interValueDelimiter = interValueDelimiter;
        this.intraValueDelimiter = intraValueDelimiter;
    }

    @Override public void validate(GeneProduct geneProduct) throws ValidationException {
        if(geneProduct == null) {
            throw new DocumentReaderException("Gene product can not be null");
        }

        try {
            checkIsNullOrEmpty(geneProduct.database, COLUMN_DB.getName());
            checkIsNullOrEmpty(geneProduct.id, COLUMN_ID.getName());
            checkIsNullOrEmpty(geneProduct.symbol, COLUMN_SYMBOL.getName());

            checkTaxonId(geneProduct.taxonId);
            checkType(geneProduct.type, COLUMN_TYPE.getName());

            checkHasAtMostOneParentId(geneProduct.parentId);

            Map<String, String> properties =
                    convertLinePropertiesToMap(geneProduct.properties, interValueDelimiter, intraValueDelimiter);

        } catch (ValidationException e) {
            throw new ValidationException("Error occurred in database: " + geneProduct.database + ", for entry: " +
                    geneProduct.id, e);
        }

    }

    /**
     * Checks to see if the expression is verified, if so it will throw a {@link ValidationException}.
     *
     * @param expression the expression to check
     * @param message the message that will be appended to the exception
     */
    private void checkAttribute(boolean expression, String message) {
        if (expression) {
            throw new ValidationException(message);
        }
    }

    private void checkIsNull(String value, String field) {
        checkAttribute(value == null, "Found null value in field: " + field);
    }

    private void checkIsEmpty(String value, String field) {
        checkAttribute(value.isEmpty(), "Found empty value in field: " + field);
    }

    private void checkIsNullOrEmpty(String value, String field) {
        checkIsNull(value, field);
        checkIsEmpty(value, field);
    }

    private void checkHasAtMostOneParentId(String parentId) {
        if (parentId != null) {
            String[] parentIds = splitValue(parentId, interValueDelimiter);

            checkAttribute(parentIds.length > 1,
                    "Found more than one id in field: " + COLUMN_PARENT_ID.getName());
        }
    }

    private void checkBooleanValue(String value, String field) {
        if (value != null) {
            if (!value.equalsIgnoreCase(TRUE_STRING) && !value.equalsIgnoreCase(FALSE_STRING)) {
                throw new ValidationException("Found invalid value for field: " + field + ": " + value);
            }
        }
    }

    private void checkType(String value, String field) {
        checkIsNullOrEmpty(value, field);
        try {
            GeneProductType.typeOf(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Error in field: " + field + " - [" + e.getMessage() + "]");
        }
    }

    private void checkTaxonId(String taxonId) {
        checkIsNullOrEmpty(taxonId, COLUMN_TAXON_ID.getName());

        if(!GeneProductParsingHelper.taxonIdMatchesRegex(taxonId)) {
            throw new ValidationException("Taxon id column does not conform to regex: " +taxonId);
        }
    }
}