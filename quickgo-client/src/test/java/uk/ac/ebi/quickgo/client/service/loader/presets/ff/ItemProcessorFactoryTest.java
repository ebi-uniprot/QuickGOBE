package uk.ac.ebi.quickgo.client.service.loader.presets.ff;

import uk.ac.ebi.quickgo.client.service.loader.presets.RestValuesRetriever;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class ItemProcessorFactoryTest {

    public static class DuplicateCheckingItemProcessorTest {
        private ItemProcessor<RawNamedPreset, RawNamedPreset> duplicateChecker;

        @Before()
        public void setUp() {
            this.duplicateChecker = ItemProcessorFactory.duplicateCheckingItemProcessor();
        }

        @Test
        public void preventDuplicates() throws Exception {
            RawNamedPreset rawNamedPreset1 = new RawNamedPreset();
            rawNamedPreset1.name = "AgBase";
            RawNamedPreset rawNamedPreset2 = new RawNamedPreset();
            rawNamedPreset2.name = "AspGD";
            RawNamedPreset rawNamedPreset3 = new RawNamedPreset();
            rawNamedPreset3.name = "ASPGD";
            RawNamedPreset rawNamedPreset4 = new RawNamedPreset();
            rawNamedPreset4.name = "Alzheimers_University_of_Toronto";

            assertThat(this.duplicateChecker.process(rawNamedPreset1), notNullValue());
            assertThat(this.duplicateChecker.process(rawNamedPreset2), notNullValue());
            assertThat(this.duplicateChecker.process(rawNamedPreset3), nullValue());
            assertThat(this.duplicateChecker.process(rawNamedPreset4), notNullValue());
        }
    }

    public static class ValidatingItemProcessorTest {
        private ItemProcessor<RawNamedPreset, RawNamedPreset> validator;

        @Before()
        public void setUp() {
            this.validator = ItemProcessorFactory.validatingItemProcessor();
        }

        @Test(expected = ValidationException.class)
        public void nullRawPresetIsInvalid() throws Exception {
            validator.process(null);
        }

        @Test(expected = ValidationException.class)
        public void nullNameIsInvalid() throws Exception {
            RawNamedPreset value = new RawNamedPreset();
            value.name = null;
            validator.process(value);
        }

        @Test(expected = ValidationException.class)
        public void emptyNameIsInvalid() throws Exception {
            RawNamedPreset value = new RawNamedPreset();
            value.name = "";
            validator.process(value);
        }

        @Test
        public void nonEmptyNameIsValid() throws Exception {
            RawNamedPreset value = new RawNamedPreset();
            value.name = "valid name";

            RawNamedPreset processedValue = validator.process(value);
            assertThat(processedValue, is(notNullValue()));
        }
    }

    public static class CheckPresetIsUsedItemProcessorTest {
        static final String RETRIEVE_KEY = "BogusKey";
        private RestValuesRetriever restValuesRetriever;
        private ItemProcessor<RawNamedPreset, RawNamedPreset> checkUsed;
        private RawNamedPreset rawItem;
        private RawNamedPreset rawItemAnother;

        @Before
        public void setup() {
            restValuesRetriever = mock(RestValuesRetriever.class);

            rawItem = new RawNamedPreset();
            rawItem.name = "UnionMills";

            rawItemAnother = new RawNamedPreset();
            rawItemAnother.name = "GlenHelen";

        }

        @Test
        public void rawItemFound() throws Exception {
            List<String> returnList = singletonList("UnionMills");
            when(restValuesRetriever.retrieveValues(RETRIEVE_KEY)).thenReturn(Optional.ofNullable(returnList));
            this.checkUsed = ItemProcessorFactory.checkPresetIsUsedItemProcessor(restValuesRetriever, RETRIEVE_KEY);

            RawNamedPreset rawItemReturned = checkUsed.process(rawItem);

            assertThat(rawItemReturned, equalTo(rawItem));
        }

        @Test
        public void rawItemNotFound() throws Exception {
            List<String> returnList = singletonList("GlenHelen");
            when(restValuesRetriever.retrieveValues(RETRIEVE_KEY)).thenReturn(Optional.ofNullable(returnList));
            this.checkUsed = ItemProcessorFactory.checkPresetIsUsedItemProcessor(restValuesRetriever, RETRIEVE_KEY);

            RawNamedPreset rawItemReturned = checkUsed.process(rawItem);

            assertThat(rawItemReturned, nullValue());
        }

        @Test
        public void noResultsFound() throws Exception {
            List<String> returnList = Collections.emptyList();
            when(restValuesRetriever.retrieveValues(RETRIEVE_KEY)).thenReturn(Optional.ofNullable(returnList));
            this.checkUsed = ItemProcessorFactory.checkPresetIsUsedItemProcessor(restValuesRetriever, RETRIEVE_KEY);

            RawNamedPreset rawItemReturned = checkUsed.process(rawItem);

            assertThat(rawItemReturned, equalTo(rawItem));
        }
    }
}