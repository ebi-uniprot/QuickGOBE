package uk.ac.ebi.quickgo.annotation.controller;

import uk.ac.ebi.quickgo.annotation.download.http.MediaTypeFactory;

import java.util.Arrays;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Contains logic required to verify the response of a download is correct.
 *
 * Created 30/01/17
 * @author Edd
 */
class DownloadResponseVerifier {
    private static final Logger LOGGER = getLogger(DownloadResponseVerifier.class);

    private DownloadResponseVerifier() {}

    static ResultMatcher nonNullMandatoryFieldsExist(MediaType mediaType) {
        Matcher<String> fieldMatcher;
        if (mediaType.getSubtype().equals(MediaTypeFactory.GAF_SUB_TYPE)) {
            fieldMatcher = new GAFMandatoryFieldMatcher();
        } else if (mediaType.getSubtype().equals(MediaTypeFactory.GPAD_SUB_TYPE)) {
            fieldMatcher = new GPADMandatoryFieldMatcher();
        } else if (mediaType.getSubtype().equals(MediaTypeFactory.TSV_SUB_TYPE)) {
            fieldMatcher = new TSVMandatoryFieldMatcher();
        } else {
            throw new IllegalArgumentException("Unknown media type: " + mediaType);
        }

        return content().string(fieldMatcher);
    }

    static ResultMatcher selectedFieldsExist() {
        Matcher<String> fieldMatcher = new TSVSelectedFieldsMatcher();
        return content().string(fieldMatcher);
    }


    static class GAFMandatoryFieldMatcher extends TypeSafeMatcher<String> {
        private static final List<Integer> MANDATORY_INDICES = asList(0, 1, 2, 4, 5, 6, 8, 11, 12, 13, 14);

        @Override public void describeTo(Description description) {
            description.appendText("mandatory indices were not populated: " + MANDATORY_INDICES);
        }

        @Override protected boolean matchesSafely(String s) {
            String[] lines = s.split("\n");
            for (String line : lines) {
                if (!line.startsWith("!")) {
                    String[] components = line.split("\t");
                    if (components.length != 17) {
                        LOGGER.error("GAF line should contain 17 fields, but found: " + components.length);
                        return false;
                    }
                    for (Integer mandatoryIndex : MANDATORY_INDICES) {
                        if (components[mandatoryIndex].isEmpty()) {
                            LOGGER.error("Mandatory GAF index should not be empty: " + mandatoryIndex);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    static class GPADMandatoryFieldMatcher extends TypeSafeMatcher<String> {
        private static final List<Integer> MANDATORY_INDICES = asList(0, 1, 2, 4, 5, 8, 9);

        @Override public void describeTo(Description description) {
            description.appendText("mandatory indices were not populated: " + MANDATORY_INDICES);
        }

        @Override protected boolean matchesSafely(String s) {
            String[] lines = s.split("\n");
            for (String line : lines) {
                if (!line.startsWith("!")) {
                    String[] components = line.split("\t");
                    if (components.length != 12) {
                        LOGGER.error("GPAD line should contain 12 fields, but found: " + components.length);
                        return false;
                    }
                    for (Integer mandatoryIndex : MANDATORY_INDICES) {
                        if (components[mandatoryIndex].isEmpty()) {
                            LOGGER.error("Mandatory GPAD index should not be empty: " + mandatoryIndex);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    static class TSVMandatoryFieldMatcher extends TypeSafeMatcher<String> {
        private static final int FIELD_COUNT = 13;
        private static final String TYPE = "TSV";
        private static final List<Integer> MANDATORY_INDICES = asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

        @Override public void describeTo(Description description) {
            description.appendText("mandatory indices were not populated: " + MANDATORY_INDICES);
        }

        @Override protected boolean matchesSafely(String s) {
            String[] allLines = s.split("\n");
            String[] dataLines = Arrays.copyOfRange(allLines, 1, allLines.length-1 );
            for (String line : dataLines) {
                if (!line.startsWith("!")) {
                    String[] components = line.split("\t");
                    if (components.length != FIELD_COUNT) {
                        LOGGER.error(TYPE + " line should contain " + FIELD_COUNT + " fields, but found: " + components
                                .length);
                        return false;
                    }
                    for (Integer mandatoryIndex : MANDATORY_INDICES) {
                        if (components[mandatoryIndex].isEmpty()) {
                            LOGGER.error("Mandatory " + TYPE + " index should not be empty: " + mandatoryIndex);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    static class TSVSelectedFieldsMatcher extends TypeSafeMatcher<String> {
        private static final int FIELD_COUNT = 3;
        private static final String TYPE = "TSV";
        private static final List<Integer> MANDATORY_INDICES = asList(0,1,2);

        @Override public void describeTo(Description description) {
            description.appendText("mandatory indices were not populated: " + MANDATORY_INDICES);
        }

        @Override protected boolean matchesSafely(String s) {
            String[] allLines = s.split("\n");
            String[] dataLines = Arrays.copyOfRange(allLines, 1, allLines.length-1 );
            for (String line : dataLines) {
                if (!line.startsWith("!")) {
                    String[] components = line.split("\t");
                    if (components.length != FIELD_COUNT) {
                        LOGGER.error(TYPE + " line should contain " + FIELD_COUNT + " fields, but found: " + components
                                .length);
                        return false;
                    }
                    for (Integer mandatoryIndex : MANDATORY_INDICES) {
                        if (components[mandatoryIndex].isEmpty()) {
                            LOGGER.error("Mandatory " + TYPE + " index should not be empty: " + mandatoryIndex);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
}
