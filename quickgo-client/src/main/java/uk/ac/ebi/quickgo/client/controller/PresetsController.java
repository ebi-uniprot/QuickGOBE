package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.ApiOperation;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This controller details to the QuickGO client specific preset information about the QuickGO project, including
 * valid filtering values that are ordered by relevance.
 *
 * Created 05/09/16
 * @author Edd
 */
@RestController
@RequestMapping(value = "/internal/presets")
public class PresetsController {
    private final CompositePreset presets;

    public PresetsController(CompositePreset presets) {
        checkArgument(presets != null, "Preset information cannot be null");

        this.presets = presets;
    }

    /**
     * Provides preset filtering information indicating valid terms and a corresponding description; all of which are
     * ordered by relevancy.
     *
     * @param fields the preset fields wanted. If empty, all fields are returned
     * @return a populated instance that encapsulates the preset information
     */
    @ApiOperation(value = "Provides preset filtering information indicating valid terms and a corresponding " +
            "description; all of which are ordered by relevancy.")
    @GetMapping( produces = {MediaType.APPLICATION_JSON_VALUE})
    public FilteredCompositePreset compositePreset(@RequestParam(required = false) String... fields) {
        return createFilteredPreset(fields);
    }

    /**
     * Creates a filtered version of the {@link CompositePreset} instance, which, when serialized to the
     * end-point, will show only the fields desired by the user.
     * @param fields the fields required by the user. If the value is null, all fields will be shown.
     * @return the filtered {@link CompositePreset} instance, showing only the desired fields.
     */
    private FilteredCompositePreset createFilteredPreset(String[] fields) {
        FilteredCompositePreset filteredPreset = new FilteredCompositePreset(presets);
        if (fields != null) {
            filteredPreset.showFields(fields);
        }
        return filteredPreset;
    }

    private static class FilteredCompositePreset extends MappingJacksonValue {
        private static final String COMPOSITE_PRESET_FILTER = "CompositePreset";

        FilteredCompositePreset(CompositePreset compositePreset) {
            super(compositePreset);

            checkArgument(compositePreset != null, "CompositePreset cannot be null");
            this.setFilters(filterNothing());
        }

        private static SimpleFilterProvider filterNothing() {
            return new SimpleFilterProvider().addFilter(COMPOSITE_PRESET_FILTER, SimpleBeanPropertyFilter.serializeAllExcept());
        }

        void showFields(String[] csvFields) {
            Set<String> fieldsSet = Stream.of(csvFields).collect(Collectors.toSet());
            if (fieldsSet.size() > 0) {
                SimpleFilterProvider filterProvider =
                        new SimpleFilterProvider()
                                .addFilter(COMPOSITE_PRESET_FILTER, SimpleBeanPropertyFilter.filterOutAllExcept(fieldsSet));
                this.setFilters(filterProvider);
            }
        }
    }
}
