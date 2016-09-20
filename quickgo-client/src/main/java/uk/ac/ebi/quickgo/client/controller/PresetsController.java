package uk.ac.ebi.quickgo.client.controller;

import uk.ac.ebi.quickgo.client.model.presets.CompositePreset;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping(value = "/QuickGO/internal/presets")
public class PresetsController {
    private final CompositePreset presets;

    @Autowired
    public PresetsController(CompositePreset compositePreset) {
        checkArgument(compositePreset != null, "Preset information cannot be null");

        this.presets = compositePreset;
    }

    /**
     * Provides preset filtering information indicating valid terms and a corresponding description; all of which are
     * ordered by relevancy.
     *
     * @return a populated instance that encapsulates the preset information
     */
    @ApiOperation(value = "Provides preset filtering information indicating valid terms and a corresponding " +
            "description; all of which are ordered by relevancy.")
    @RequestMapping(method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CompositePreset> compositePreset() {
        return ResponseEntity.ok(presets);
    }
}
