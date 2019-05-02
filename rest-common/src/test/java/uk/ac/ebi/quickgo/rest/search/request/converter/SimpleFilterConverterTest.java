package uk.ac.ebi.quickgo.rest.search.request.converter;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.request.FilterRequest;
import uk.ac.ebi.quickgo.rest.search.request.config.FilterConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery.*;
import static uk.ac.ebi.quickgo.rest.search.request.converter.SimpleFilterConverter.*;

/**
 * Created 06/06/16
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleFilterConverterTest {
    private static final String FIELD1 = "field1";
    private static final String FIELD2 = "field2";
    private static final String FIELD_VALUE_1 = "value1";
    private static final String FIELD_VALUE_2 = "value2";

    @Mock
    private FilterConfig filterConfigMock;
    private SimpleFilterConverter converter;

    @Before
    public void setUp() {
        this.converter = new SimpleFilterConverter(filterConfigMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestConfigForConverterThrowsException() {
        new SimpleFilterConverter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestForConverterThrowsException() {
        converter.transform(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestNoPropertyThrowsException() {
        FilterRequest request = FilterRequest.newBuilder().build();
        converter.transform(request);
    }

    @Test
    public void transformsRequestWithSingleValueIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultipleValuesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(FIELD1, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                or(
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultiplePropertiesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(FIELD1, FIELD_VALUE_1)
                .addProperty(FIELD2, FIELD_VALUE_2)
                .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                and(
                        QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                        QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_2));

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestWithMultiplePropertiesAndMultipleValuesIntoAQuickGOQuery() {
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(FIELD1, FIELD_VALUE_1, FIELD_VALUE_2)
                .addProperty(FIELD2, FIELD_VALUE_2, FIELD_VALUE_1)
                .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
                and(
                        or(
                                QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_1),
                                QuickGOQuery.createQuery(FIELD1, FIELD_VALUE_2)
                        ),
                        or(
                                QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_2),
                                QuickGOQuery.createQuery(FIELD2, FIELD_VALUE_1)
                        )
                );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContainingAdvanceFilter_goIdAnd() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(GP_RELATED_AND_GO_IDS, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          and(
            QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_1),
            QuickGOQuery.createQuery(GO_ID, FIELD_VALUE_1)
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContainingAdvanceFilter_goIdAndMultipleValues() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GP_RELATED_AND_GO_IDS, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          and(
            or(
              QuickGOQuery.createQuery(GO_ID, FIELD_VALUE_1),
              QuickGOQuery.createQuery(GO_ID, FIELD_VALUE_2)
            ),
            and(
              QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_1),
              QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_2)
            )
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContainingAdvanceFilter_goIdNot() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(GP_RELATED_NOT_GO_IDS, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          not(
            QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_1)
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContainingAdvanceFilter_goIdNotMultipleValues() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GP_RELATED_NOT_GO_IDS, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          not(
            or(
              QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_1),
              QuickGOQuery.createQuery(GP_RELATED_GO_IDS, FIELD_VALUE_2)
            )
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extensionAll() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(EXTENSION, "*").build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(EXTENSION, "*");

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extension() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(EXTENSION, FIELD_VALUE_1).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery = QuickGOQuery.createContainQuery(EXTENSION, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extensionMultipleValues() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(EXTENSION, FIELD_VALUE_1, FIELD_VALUE_2).build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createContainQuery(EXTENSION, FIELD_VALUE_1),
            QuickGOQuery.createContainQuery(EXTENSION, FIELD_VALUE_2)
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extensionAND() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(EXTENSION, "a AND b").build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          and(
            QuickGOQuery.createContainQuery(EXTENSION, "a"),
            QuickGOQuery.createContainQuery(EXTENSION, "b")
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extensionOR() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(EXTENSION, "a OR b").build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createContainQuery(EXTENSION, "a"),
            QuickGOQuery.createContainQuery(EXTENSION, "b")
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequestContaining_extension_AND_OR() {
        FilterRequest request = FilterRequest.newBuilder().addProperty(EXTENSION, "a AND b OR c").build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();
        QuickGOQuery expectedQuery =
          or(
            and(
              QuickGOQuery.createContainQuery(EXTENSION, "a"),
              QuickGOQuery.createContainQuery(EXTENSION, "b")
            ),
            QuickGOQuery.createContainQuery(EXTENSION, "c")
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequest_geneproductOnly() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GENE_PRODUCT_TYPE, FIELD_VALUE_1)
          .addProperty(FIELD2, FIELD_VALUE_2)
          .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery = QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, FIELD_VALUE_1);

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequest_geneproduct_proteins() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GENE_PRODUCT_TYPE, FIELD_VALUE_1,PROTEIN)
          .addProperty(FIELD2, FIELD_VALUE_2)
          .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, FIELD_VALUE_1),
            QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, PROTEIN)
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequest_geneproduct_proteins_geneProductSubset() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GENE_PRODUCT_TYPE, FIELD_VALUE_1, PROTEIN)
          .addProperty(GENE_PRODUCT_SUBSET, FIELD_VALUE_2)
          .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, FIELD_VALUE_1),
            and(
              QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, PROTEIN),
              QuickGOQuery.createQuery(GENE_PRODUCT_SUBSET, FIELD_VALUE_2)
            )
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequest_geneproduct_proteins_proteome() {
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GENE_PRODUCT_TYPE, FIELD_VALUE_1, PROTEIN)
          .addProperty(PROTEOME, FIELD_VALUE_2)
          .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, FIELD_VALUE_1),
            and(
              QuickGOQuery.createQuery(PROTEOME, FIELD_VALUE_2),
              QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, PROTEIN)
            )
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

    @Test
    public void transformsRequest_geneproduct_proteins_geneProductSubset_proteome() {
        String VALUE3 = "value3";
        FilterRequest request = FilterRequest.newBuilder()
          .addProperty(GENE_PRODUCT_TYPE, FIELD_VALUE_1, PROTEIN)
          .addProperty(GENE_PRODUCT_SUBSET, FIELD_VALUE_2)
          .addProperty(PROTEOME, VALUE3)
          .build();
        QuickGOQuery resultingQuery = converter.transform(request).getConvertedValue();

        QuickGOQuery expectedQuery =
          or(
            QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, FIELD_VALUE_1),
            and(
              QuickGOQuery.createQuery(PROTEOME, VALUE3),
              and(
                QuickGOQuery.createQuery(GENE_PRODUCT_TYPE, PROTEIN),
                QuickGOQuery.createQuery(GENE_PRODUCT_SUBSET, FIELD_VALUE_2)
              )
            )
          );

        assertThat(resultingQuery, is(expectedQuery));
    }

}