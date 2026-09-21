package uk.ac.ebi.quickgo.client.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

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

    @Autowired
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
    @Operation(summary = "Provides preset filtering information indicating valid terms and a corresponding " +
            "description; all of which are ordered by relevancy.")
    @GetMapping( produces = {MediaType.APPLICATION_JSON_VALUE})
    public CompositePreset compositePreset(@RequestParam(name = "fields", required = false) String... fields) {
        return presets;
    }

    @ControllerAdvice(assignableTypes = { PresetsController.class })
    public static class CompositePresetFilterAdvice implements ResponseBodyAdvice<Object> {

        private static final String COMPOSITE_PRESET_FILTER = "CompositePreset";

        @Override
        public boolean supports(@NonNull MethodParameter returnType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
            return converterType == JacksonJsonHttpMessageConverter.class;
        }

        @Override
        public @Nullable Map<String, Object> determineWriteHints(
          @Nullable Object body,
          @NonNull MethodParameter returnType,
          @NonNull MediaType selectedContentType,
          @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType) {

            if (!(body instanceof CompositePreset)) return null;

            var request = getCurrentHttpRequest();
            var filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);

            if (request != null) {
                String fields = request.getParameter("fields");
                if (fields != null && !fields.isBlank()) {
                    Set<String> fieldsSet = Arrays.stream(fields.split(","))
                      .map(String::trim)
                      .filter(s -> !s.isEmpty())
                      .collect(Collectors.toSet());
                    if (!fieldsSet.isEmpty()) {
                        filterProvider.addFilter(COMPOSITE_PRESET_FILTER, SimpleBeanPropertyFilter.filterOutAllExcept(fieldsSet));
                    }
                }
            }

            return Map.of(FilterProvider.class.getName(), filterProvider);
        }

        @Override
        public @Nullable Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
                                                @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                                @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
            return body;
        }

        private @Nullable HttpServletRequest getCurrentHttpRequest() {
            return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
              .filter(ServletRequestAttributes.class::isInstance)
              .map(ServletRequestAttributes.class::cast)
              .map(ServletRequestAttributes::getRequest)
              .orElse(null);
        }
    }
}
