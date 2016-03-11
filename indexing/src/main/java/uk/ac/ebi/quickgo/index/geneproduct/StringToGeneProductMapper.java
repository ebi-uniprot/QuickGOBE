package uk.ac.ebi.quickgo.index.geneproduct;

import uk.ac.ebi.quickgo.index.common.DocumentReaderException;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import static uk.ac.ebi.quickgo.index.geneproduct.StringToGeneProductMapper.Columns.*;

/**
 * Converts a String representing a gene product into a {@link GeneProduct} object.
 *
 * @author Ricardo Antunes
 */
public class StringToGeneProductMapper implements FieldSetMapper<GeneProduct> {
    @Override public GeneProduct mapFieldSet(FieldSet fieldSet) throws BindException {
        if (fieldSet == null) {
            throw new DocumentReaderException("Provided field set is null");
        }

        if (fieldSet.getFieldCount() < Columns.numColumns()) {
            throw new DocumentReaderException(
                    "Expected at least: " + Columns.numColumns() + ", but found: " + fieldSet.getFieldCount());
        }

        GeneProduct geneProduct = new GeneProduct();

        geneProduct.database = fieldSet.readString(COLUMN_DB.getPosition());
        geneProduct.id = fieldSet.readString(COLUMN_ID.getPosition());
        geneProduct.symbol = fieldSet.readString(COLUMN_SYMBOL.getPosition());
        geneProduct.name = fieldSet.readString(COLUMN_NAME.getPosition());
        geneProduct.synonym = fieldSet.readString(COLUMN_SYNONYM.getPosition());
        geneProduct.type = fieldSet.readString(COLUMN_TYPE.getPosition());
        geneProduct.taxonId = fieldSet.readString(COLUMN_TAXON_ID.getPosition());
        geneProduct.parentId = fieldSet.readString(COLUMN_PARENT_ID.getPosition());
        geneProduct.xref = fieldSet.readString(COLUMN_XREF.getPosition());
        geneProduct.properties = fieldSet.readString(COLUMN_PROPERTIES.getPosition());

        return geneProduct;
    }

    enum Columns {
         COLUMN_DB(0),
         COLUMN_ID(1),
         COLUMN_SYMBOL(2),
         COLUMN_NAME(3),
         COLUMN_SYNONYM(4),
         COLUMN_TYPE(5),
         COLUMN_TAXON_ID(6),
         COLUMN_PARENT_ID(7),
         COLUMN_XREF(8),
         COLUMN_PROPERTIES(9);

        private int position;

        Columns(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public static int numColumns() {
            return Columns.values().length;
        }
    }
}