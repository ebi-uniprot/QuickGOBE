package uk.ac.ebi.quickgo.rest.search.request;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created 17/06/16
 * @author Edd
 */
class FilterRequestTest {
    @Test
    void cannotCreateRequestWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> FilterRequest.newBuilder().addProperty(null, "value"));
    }

    @Test
    void cannotCreateRequestWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> FilterRequest.newBuilder().addProperty("  ", "value"));
    }

    @Test
    void createValidRequestWith1PropertyAnd1Value() {
        String field = "name";
        String fieldValue = "value";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, fieldValue).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValue(field).isPresent(), is(true));
        assertThat(request.getValue(field).get(), contains(fieldValue));
    }

    @Test
    void createValidRequestWith1PropertyWithEmptyValue() {
        String field = "name";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValue(field).isPresent(), is(true));
        assertThat(request.getValue(field).get(), is(empty()));
    }

    @Test
    void createValidRequestWith1PropertyAnd2Values() {
        String field = "name";
        String fieldValue1 = "value1";
        String fieldValue2 = "value2";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, fieldValue1, fieldValue2).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValue(field).isPresent(), is(true));
        assertThat(request.getValue(field).get(), contains(fieldValue1, fieldValue2));
    }

    @Test
    void createValidRequestWith2Properties() {
        String field1 = "name1";
        String field1Value = "value1";
        String field2 = "name2";
        String field2Value = "value2";
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(field1, field1Value)
                .addProperty(field2, field2Value).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValue(field1).isPresent(), is(true));
        assertThat(request.getValue(field2).isPresent(), is(true));
        assertThat(request.getValue(field1).get(), contains(field1Value));
        assertThat(request.getValue(field2).get(), contains(field2Value));
    }

    @Test
    void fetchingValuesFindsAll() {
        String field1 = "name1";
        String field1Value = "value1";
        String field2 = "name2";
        String field2Value1 = "value2";
        String field2Value2 = "value3";

        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(field1, field1Value)
                .addProperty(field2, field2Value1, field2Value2).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValues(), containsInAnyOrder(singletonList(field1Value), asList(field2Value1, field2Value2)));
    }

    @Test
    void addingPropertyWithoutValueResultsInPropertyWithEmptyValuesList() {
        String field = "name";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field).build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getValue(field).isPresent(), is(true));
        assertThat(request.getValue(field).get(), is(emptyList()));
    }

    /**
     * A filter request without properties is an unlikely situation but possible.
     */
    @Test
    void filterRequestWithNoPropertiesHasEmptySignature() {
        FilterRequest request = FilterRequest.newBuilder().build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getSignature(), is(empty()));
    }

    @Test
    void filterRequestWithOnePropertyHasSignatureContainingOneValue() {
        String field = "name";
        FilterRequest request = FilterRequest.newBuilder().addProperty(field, "value").build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getSignature(), contains(field));
    }

    @Test
    void filterRequestWithMultiplePropertiesHasSignatureContainingMultipleValues() {
        String field1 = "name1";
        String field2 = "name2";
        FilterRequest request = FilterRequest.newBuilder()
                .addProperty(field1, "value")
                .addProperty(field2, "value")
                .build();
        assertThat(request, is(notNullValue()));
        assertThat(request.getSignature(), containsInAnyOrder(field1, field2));
    }

}