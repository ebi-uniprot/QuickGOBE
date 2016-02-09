package uk.ac.ebi.quickgo.common.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created 08/02/16
 * @author Edd
 */
public class ServiceRetrievalConfigHelper {

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
                mapletMap.put(mapletMatcher.group(1).trim(), mapletMatcher.group(2).trim());
            }
        }
        return mapletMap;
    }
}
