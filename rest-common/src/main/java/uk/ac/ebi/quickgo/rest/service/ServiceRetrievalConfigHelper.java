package uk.ac.ebi.quickgo.rest.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper functions associated with {@link ServiceRetrievalConfig}.
 *
 * Created 08/02/16
 * @author Edd
 */
public class ServiceRetrievalConfigHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRetrievalConfigHelper.class);

    private static final Pattern FIELD_FORMAT_REPO_2_DOMAIN = Pattern.compile("([\\w\\W]+)->([\\w\\W]+)");

    /**
     * <p>
     *     Extracts a comma separated list of mappings from a single {@link String}. For example,
     *     it is used to represent a list of mappings of internal document field names to domain
     *     relevant field names.
     * </p>
     * <p>
     *     Each mapping is of the form: {@code internalName->domainName}. Multiple mappings
     *     are separated by commas.
     * </p>
     *
     * @param fieldMappings the {@link String} representation of the mappings.
     * @return the mappings of {@code internal} names, to {@code domain} names.
     */
    public static Map<String, String> extractFieldMappings(String fieldMappings, String delim) {
        Map<String, String> mapletMap = new HashMap<>();
        for (String maplet : fieldMappings.split(delim)) {
            Matcher mapletMatcher = FIELD_FORMAT_REPO_2_DOMAIN.matcher(maplet);
            if (mapletMatcher.matches()) {
                String domainElement = mapletMatcher.group(1).trim();
                if (!mapletMap.containsKey(domainElement)) {
                    mapletMap.put(domainElement, mapletMatcher.group(2).trim());
                } else {
                    ServiceConfigException exception = new ServiceConfigException("Problem encountered when " +
                            "extracting field mappings");
                    LOGGER.error("Field mappings must be a function! Domain element '{}' has multiple mappings. " +
                            "Please make sure it has just 1 mapping.",
                            domainElement, exception);
                    throw exception;
                }
            }
        }
        return mapletMap;
    }
}
