package uk.ac.ebi.quickgo.annotation.controller;

import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Supply the fields that should appear in the download output.
 * @author Tony Wardell
 * Date: 15/08/2017
 * Time: 15:32
 * Created with IntelliJ IDEA.
 */
public class DownloadResponseVerifierSelectedFields {
    private static final Logger LOGGER = getLogger(DownloadResponseVerifier.class);

    static ResultMatcher selectedFieldsExist(String[] expectedFields) {
        Matcher<String> fieldMatcher = new TSVSelectedFieldsMatcher(expectedFields);
        return content().string(fieldMatcher);
    }

    static class TSVSelectedFieldsMatcher extends TypeSafeMatcher<String> {
        private static final String TYPE = "TSV";
        private String[] expectedFields;

        TSVSelectedFieldsMatcher(String[] expectedFields) {
            this.expectedFields = expectedFields;
        }

        @Override public void describeTo(Description description) {
            description.appendText("mandatory columns were not populated: " + Arrays.toString(expectedFields));
        }

        @Override protected boolean matchesSafely(String s) {
            String[] allLines = s.split("\n");
            String[] dataLines = Arrays.copyOfRange(allLines, 1, allLines.length - 1);
            for (String line : dataLines) {
                if (!line.startsWith("!")) {
                    String[] components = line.split("\t");

                    if (components.length != expectedFields.length) {
                        LOGGER.error(TYPE + " line should contain " + expectedFields.length + " fields, but found: " +
                                             components
                                                     .length);
                        return false;
                    }
                    for (int i = 0; i > expectedFields.length; i++) {
                        if (components[i].isEmpty()) {
                            LOGGER.error("Mandatory " + TYPE + " index should not be empty at column " + i);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
}
