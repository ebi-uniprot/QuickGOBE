package uk.ac.ebi.quickgo.index.geneproduct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.ac.ebi.quickgo.index.geneproduct.Columns.*;

/**
 * Tests the behaviour of the {@link StringToGeneProductMapper} class.
 */
class StringToGeneProductMapperTest {
    private StringToGeneProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StringToGeneProductMapper();
    }

    @Test
    void nullFieldSetThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapFieldSet(null);
        });
    }

    @Test
    void fieldSetWithInsufficientValuesThrowsException() {
        assertThrows(IncorrectTokenCountException.class, () -> {
            String[] tokens = new String[numColumns() - 1];
            FieldSet fieldSet = new DefaultFieldSet(tokens);

            mapper.mapFieldSet(fieldSet);
        });
    }

    @Test
    void convertFieldSetWithNullValuesIntoGeneProduct() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_DB.getPosition()] = null;
        tokens[COLUMN_ID.getPosition()] = null;
        tokens[COLUMN_SYMBOL.getPosition()] = null;
        tokens[COLUMN_NAME.getPosition()] = null;
        tokens[COLUMN_SYNONYM.getPosition()] = null;
        tokens[COLUMN_TYPE.getPosition()] = null;
        tokens[COLUMN_TAXON_ID.getPosition()] = null;
        tokens[COLUMN_PARENT_ID.getPosition()] = null;
        tokens[COLUMN_XREF.getPosition()] = null;
        tokens[COLUMN_PROPERTIES.getPosition()] = null;

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        GeneProduct geneProduct = mapper.mapFieldSet(fieldSet);

        assertThat(geneProduct.database, is(tokens[COLUMN_DB.getPosition()]));
        assertThat(geneProduct.id, is(tokens[COLUMN_ID.getPosition()]));
        assertThat(geneProduct.symbol, is(tokens[COLUMN_SYMBOL.getPosition()]));
        assertThat(geneProduct.name, is(tokens[COLUMN_NAME.getPosition()]));
        assertThat(geneProduct.synonym, is(tokens[COLUMN_SYNONYM.getPosition()]));
        assertThat(geneProduct.type, is(tokens[COLUMN_TYPE.getPosition()]));
        assertThat(geneProduct.taxonId, is(tokens[COLUMN_TAXON_ID.getPosition()]));
        assertThat(geneProduct.parentId, is(tokens[COLUMN_PARENT_ID.getPosition()]));
        assertThat(geneProduct.xref, is(tokens[COLUMN_XREF.getPosition()]));
        assertThat(geneProduct.properties, is(tokens[COLUMN_PROPERTIES.getPosition()]));
    }

    @Test
    void convertValidFieldSetIntoGeneProduct() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_DB.getPosition()] = "UniProtKB";
        tokens[COLUMN_ID.getPosition()] = "A0A000";
        tokens[COLUMN_SYMBOL.getPosition()] = "moeA5";
        tokens[COLUMN_NAME.getPosition()] = "MoeA5";
        tokens[COLUMN_SYNONYM.getPosition()] = "A0A000_9ACTN|moeA5";
        tokens[COLUMN_TYPE.getPosition()] = "protein";
        tokens[COLUMN_TAXON_ID.getPosition()] = "taxon:35758";
        tokens[COLUMN_PARENT_ID.getPosition()] = "A0A001";
        tokens[COLUMN_XREF.getPosition()] = "EMBL:DQ988994";
        tokens[COLUMN_PROPERTIES.getPosition()] = "db_subset=TrEMBL";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        GeneProduct geneProduct = mapper.mapFieldSet(fieldSet);

        assertThat(geneProduct.database, is(tokens[COLUMN_DB.getPosition()]));
        assertThat(geneProduct.id, is(tokens[COLUMN_ID.getPosition()]));
        assertThat(geneProduct.symbol, is(tokens[COLUMN_SYMBOL.getPosition()]));
        assertThat(geneProduct.name, is(tokens[COLUMN_NAME.getPosition()]));
        assertThat(geneProduct.synonym, is(tokens[COLUMN_SYNONYM.getPosition()]));
        assertThat(geneProduct.type, is(tokens[COLUMN_TYPE.getPosition()]));
        assertThat(geneProduct.taxonId, is(tokens[COLUMN_TAXON_ID.getPosition()]));
        assertThat(geneProduct.parentId, is(tokens[COLUMN_PARENT_ID.getPosition()]));
        assertThat(geneProduct.xref, is(tokens[COLUMN_XREF.getPosition()]));
        assertThat(geneProduct.properties, is(tokens[COLUMN_PROPERTIES.getPosition()]));
    }

    @Test
    void trimFieldsFromFieldSetWhenConvertingToGeneProduct() throws Exception {
        String[] tokens = new String[numColumns()];
        tokens[COLUMN_DB.getPosition()] = "UniProtKB ";
        tokens[COLUMN_ID.getPosition()] = " A0A000";
        tokens[COLUMN_SYMBOL.getPosition()] = "moeA5 ";
        tokens[COLUMN_NAME.getPosition()] = "  MoeA5";
        tokens[COLUMN_SYNONYM.getPosition()] = "A0A000_9ACTN|moeA5  ";
        tokens[COLUMN_TYPE.getPosition()] = "protein    ";
        tokens[COLUMN_TAXON_ID.getPosition()] = "   taxon:35758";
        tokens[COLUMN_PARENT_ID.getPosition()] = "   A0A001   ";
        tokens[COLUMN_XREF.getPosition()] = "   EMBL:DQ988994   ";
        tokens[COLUMN_PROPERTIES.getPosition()] = "   db_subset=TrEMBL ";

        FieldSet fieldSet = new DefaultFieldSet(tokens);

        GeneProduct geneProduct = mapper.mapFieldSet(fieldSet);

        assertThat(geneProduct.database, is(trim(tokens[COLUMN_DB.getPosition()])));
        assertThat(geneProduct.id, is(trim(tokens[COLUMN_ID.getPosition()])));
        assertThat(geneProduct.symbol, is(trim(tokens[COLUMN_SYMBOL.getPosition()])));
        assertThat(geneProduct.name, is(trim(tokens[COLUMN_NAME.getPosition()])));
        assertThat(geneProduct.synonym, is(trim(tokens[COLUMN_SYNONYM.getPosition()])));
        assertThat(geneProduct.type, is(trim(tokens[COLUMN_TYPE.getPosition()])));
        assertThat(geneProduct.taxonId, is(trim(tokens[COLUMN_TAXON_ID.getPosition()])));
        assertThat(geneProduct.parentId, is(trim(tokens[COLUMN_PARENT_ID.getPosition()])));
        assertThat(geneProduct.xref, is(trim(tokens[COLUMN_XREF.getPosition()])));
        assertThat(geneProduct.properties, is(trim(tokens[COLUMN_PROPERTIES.getPosition()])));
    }

    private String trim(String value) {
        return value.trim();
    }
}